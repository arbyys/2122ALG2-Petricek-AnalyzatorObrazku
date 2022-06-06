package app;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import utils.GaussianBlurService;
import utils.IPatternImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;

import static utils.Helpers.cloneBufferedImage;

/**
 * Třída obrázku
 * @author Adam Petříček
 */
public class Image implements IPatternImage {
    private String path;
    private String extension;
    private String name;
    BufferedImage bufferedImageInstance;

    /**
     * Vrátí jméno obrázku
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Vrátí příponu souboru
     * @return String
     */
    public String getExtension()
    {
        return this.extension;
    }

    /**
     * Konstruktor
     * @param fileInstance instance daného souboru
     */
    public Image(File fileInstance)
    {
        this.path = fileInstance.getAbsolutePath();
        this.name = fileInstance.getName();
        String[] split = this.name.split("\\.");
        this.extension = split[split.length-1];
        try
        {
            this.bufferedImageInstance = ImageIO.read(fileInstance);
        }
        catch (IOException ex)
        {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            throw new IllegalArgumentException("Wrong image");
        }
    }

    /**
     *
     * Vrací metadata daného obrázku
     * @return String Builder
     * @throws ImageProcessingException
     * @throws IOException
     */
    public StringBuilder getMetadata() throws ImageProcessingException, IOException {
        StringBuilder returnString = new StringBuilder();
        Metadata metadata = null;
        metadata = ImageMetadataReader.readMetadata(new File(path));
        if (metadata != null)
        {
            returnString.append(String.format("\n\nMetadata obrázku %s:\n\n", name));
            for (Directory directory : metadata.getDirectories())
            {
                for (Tag tag : directory.getTags()) {
                    returnString.append(String.format("%s\n", tag));
                }
            }
        }
        return returnString;
    }

    /**
     * Ukládá metadata obrázku do souboru
     * @throws IOException
     * @throws ImageProcessingException
     */
    public void saveMetadata() throws IOException, ImageProcessingException {
        StringBuilder metadata = getMetadata();
        File result =  new File("img/metadata.txt");
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(result))))
        {
            pw.print(metadata);
        }
    }

    /**
     * Vrací celkový počet pixelů obrázku
     * @return int
     */
    public int getTotalPixels()
    {
        int width = bufferedImageInstance.getWidth();
        int height = bufferedImageInstance.getHeight();
        return width * height;
    }

    /**
     * Invertuje barvy obrázku a ukládá ho
     * @throws IOException
     */
    public void copyAndInvert() throws IOException
    {
        BufferedImage copiedImage = cloneBufferedImage(bufferedImageInstance);
        for (int y = 0; y < copiedImage.getHeight(); y++)
        {
            for (int x = 0; x < copiedImage.getWidth(); x++)
            {
                Color c = new Color(copiedImage.getRGB(x, y), true);
                Color newColor = new Color(255-c.getRed(),255-c.getBlue(), 255-c.getGreen());
                copiedImage.setRGB(x,y,newColor.getRGB());
            }
        }
        String nameNoExtension = name.replaceFirst("[.][^.]+$", "");
        File outputfile = new File(String.format("img/%s_inverted.%s", nameNoExtension, extension));
        ImageIO.write(copiedImage, extension, outputfile);
    }

    /**
     * Mění obrázek na černobílý a ukládá ho
     * @throws IOException
     */
    public void copyAndMakeGrey() throws IOException
    {
        BufferedImage copiedImage = cloneBufferedImage(bufferedImageInstance);
        for (int y = 0; y < copiedImage.getHeight(); y++)
        {
            for (int x = 0; x < copiedImage.getWidth(); x++)
            {
                Color c = new Color(copiedImage.getRGB(x, y), true);
                int calculatedGrey = (c.getRed() + c.getBlue() + c.getGreen()) / 3;
                Color newColor = new Color(calculatedGrey,calculatedGrey,calculatedGrey);
                copiedImage.setRGB(x,y,newColor.getRGB());
            }
        }
        String nameNoExtension = name.replaceFirst("[.][^.]+$", "");
        File outputfile = new File(String.format("img/%s_grey.%s", nameNoExtension, extension));
        ImageIO.write(copiedImage, extension, outputfile);
    }

    /**
     * Mění obrázek na rozmazaný a ukládá ho
     * @throws IOException
     */
    public void copyAndBlur() throws IOException
    {
        String nameNoExtension = name.replaceFirst("[.][^.]+$", "");
        File outFile = new File(String.format("img/%s_blurred.%s", nameNoExtension, extension));

        BufferedImage image = cloneBufferedImage(bufferedImageInstance);
        int width = image.getWidth();
        int height = image.getHeight();
        int numPixels = width * height;

        double[][] pixels = new double[4][numPixels];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                for (int ch = 0; ch < 3; ch++) {
                    int val = (rgb >>> ((2 - ch) * 8)) & 0xFF;
                    pixels[ch][y * width + x] = Math.pow(val / 255.0, 2.2);
                }
                pixels[3][y * width + x] = 1;
            }
        }

        gaussianBlur(pixels, width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double weight = pixels[3][y * width + x];
                int rgb = 0xFF;
                for (int ch = 0; ch < 3; ch++) {
                    int val = (int)Math.round(Math.pow(pixels[ch][y * width + x] / weight, 1 / 2.2) * 255);
                    if (val < 0)
                        val = 0;
                    else if (val > 255)
                        val = 255;
                    rgb = rgb << 8 | val;
                }
                image.setRGB(x, y, rgb);
            }
        }
        ImageIO.write(image, extension, outFile);

    }

    private static void gaussianBlur(double[][] pixels, int width, int height)
    {
        final double RADIUS = 4.5;
        double scaler = -1 / (RADIUS * RADIUS * 2);

        int length = Integer.highestOneBit(width * 2 - 1) * 2;
        double[] kernel = new double[length];
        for (int i = -(width - 1); i < width; i++)
            kernel[(i + length) % length] = Math.exp(scaler * i * i);
        GaussianBlurService conv = new GaussianBlurService(kernel);

        double[] lineReal = new double[length];
        double[] lineImag = new double[length];
        for (double[] pixel : pixels) {
            for (int y = 0; y < height; y++) {
                System.arraycopy(pixel, y * width, lineReal, 0, width);
                Arrays.fill(lineReal, width, lineReal.length, 0);
                Arrays.fill(lineImag, 0);
                conv.convolve(lineReal, lineImag);
                System.arraycopy(lineReal, 0, pixel, y * width, width);
            }
        }

        length = Integer.highestOneBit(height * 2 - 1) * 2;
        kernel = new double[length];
        for (int i = -(height - 1); i < height; i++)
            kernel[(i + length) % length] = Math.exp(scaler * i * i);
        conv = new GaussianBlurService(kernel);

        lineReal = new double[length];
        lineImag = new double[length];
        for (int ch = 0; ch < pixels.length; ch++) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++)
                    lineReal[y] = pixels[ch][y * width + x];
                Arrays.fill(lineReal, height, lineReal.length, 0);
                Arrays.fill(lineImag, 0);
                conv.convolve(lineReal, lineImag);
                for (int y = 0; y < height; y++)
                    pixels[ch][y * width + x] = lineReal[y];
            }
        }
    }

    /**
     * Vrací mapu barev a jejich počtu výskytu v obrázku
     * @return Map<String, Integer>
     */
    public Map<String, Integer> getMostCommonColors()
    {
        Map<String, Integer> dictionary = new HashMap<>();
        for (int y = 0; y < bufferedImageInstance.getHeight(); y++)
        {
            for (int x = 0; x < bufferedImageInstance.getWidth(); x++)
            {
                Color c = new Color(bufferedImageInstance.getRGB(x, y), true);

                String hex = String.format("#%06x", c.getRGB() & 0x00FFFFFF);
                if(dictionary.containsKey(hex))
                {
                    dictionary.put(hex, dictionary.get(hex)+1);
                }
                else
                {
                    dictionary.put(hex, 1);
                }
            }
        }

        dictionary = dictionary.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(15)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        return dictionary;
    }

    /**
     * Defaultní seřazení podle jména
     * @param o the object to be compared.
     * @return int
     */
    @Override
    public int compareTo(IPatternImage o) {
        return this.getName().compareToIgnoreCase(o.getName());
    }
}
