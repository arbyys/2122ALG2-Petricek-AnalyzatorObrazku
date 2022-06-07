package ui;

import app.Image;
import app.ImageCollection;
import com.diogonunes.jcolor.Attribute;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import com.drew.metadata.Metadata;
import utils.SortingMethod;
import utils.Helpers;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import static com.diogonunes.jcolor.Ansi.colorize;
import static com.diogonunes.jcolor.Attribute.*;

/**
 * Třída UI logiky
 * @author Adam Petříček
 */
public class App extends Component {

    private static Scanner sc;
    private static ImageCollection imageCollection;

    /**
     * Hlavní UI metoda
     * @param args
     */
    public static void main(String[] args)
    {
        sc = new Scanner(System.in);
        imageCollection = new ImageCollection();
        boolean notValidFlag;
        int validInput = 0;

        try
        {
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, "UTF-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new InternalError("No UTF-8 support");
        }

        loadImages();
        while(true)
        {
            System.out.format("\n\nAnalyzátor obrázků\n=====================\nnačteno %d obrázků \n\n1 = načíst aktuální obsah složky img/\n2 = vypsat seznam obrázků\n3 = smazat obrázek z paměti aplikace\n4 = akce pro všechny obrázky\n5 = akce pro jeden obrázek\n\n6 = ukončit", imageCollection.imagesLoaded);
            String input = sc.next();

            if(!validateInput(input, 1, 6))
            {
                System.out.println("\nNeznámá instrukce");
                promptEnterKey();
                continue;
            }

            validInput = Integer.parseInt(input);

            switch (validInput) {
                case 1 ->
                {
                    submenuOne();
                }
                case 2 ->
                {
                    submenuTwo();
                }
                case 3 ->
                {
                    submenuThree();
                }
                case 4 ->
                {
                    submenuFour();
                }
                case 5 ->
                {
                    submenuFive();
                }
                case 6 ->
                {
                    System.exit(0);
                }
            }
        }
    }

    private static void submenuOne()
    {
        int imagesLoaded = loadImages();
        System.out.format("\nBylo načteno celkem %d obrázků", imagesLoaded);
        promptEnterKey();
    }
    private static void submenuTwo()
    {
        loop: while(true)
        {
            System.out.format("\n\n\nAktuálně načtené obrázky (nahrané v %s):\n\n", imageCollection.getChanged());
            ArrayList<Image> images = imageCollection.getImages();
            int iteration = 1;
            for(Image currentImage : images)
            {
                System.out.format("%d. %s\n", iteration, currentImage.getName());
                iteration += 1;
            }

            System.out.println("\n\n1 = seřadit podle jména\n2 = seřadit podle přípony\n3 = zpět");
            String input = sc.next();

            if(!validateInput(input, 1, 3))
            {
                System.out.println("\nNeznámá instrukce");
                promptEnterKey();
            }

            int validInput = Integer.parseInt(input);

            switch (validInput) {
                case 1 ->
                { // podle jména
                    imageCollection.sort(SortingMethod.name);
                }
                case 2 ->
                { // podle přípony
                    imageCollection.sort(SortingMethod.extension);
                }
                case 3 ->
                {
                    break loop;
                }
            }
        }

    }
    private static void submenuThree()
    {
        System.out.println("\n\n\nAktuálně načtené obrázky:\n\n");
        ArrayList<Image> images = imageCollection.getImages();
        int iteration = 1;
        for(Image currentImage : images)
        {
            System.out.format("%d. %s\n", iteration, currentImage.getName());
            iteration += 1;
        }

        System.out.println("Zadej číslo obrázku, který chceš smazat:");
        String input = sc.next();

        if(!validateInput(input, 1, images.size()))
        {
            System.out.println("\nNeznámá instrukce");
            promptEnterKey();
        }

        int validInput = Integer.parseInt(input);

        imageCollection.removeImage(validInput);
        System.out.println("\n\nObrázek byl úspěšně smazán");
        promptEnterKey();
    }
    private static void submenuFour()
    {
        System.out.println("\n\n\nakce pro všechny obrázky\n=====================\n1 = zobrazit histogram barev v obrázcích\n2 = zobrazit metadata obrázků\n3 = vygenerovat metadata do txt souboru\n4 = vygenerovat černobílé obrázky\n5 = vygenerovat rozmazané obrázky\n6 = vygenerovat obrázky s invertovanými barvami\n7 = zpět\n");
        String input = sc.next();

        if(!validateInput(input, 1, 7))
        {
            System.out.println("\nNeznámá instrukce");
            promptEnterKey();
        }

        int validInput = Integer.parseInt(input);

        switch (validInput) {
            case 1 ->
            { // histogram
                int totalPixels = imageCollection.getTotalPixelsAll();
                System.out.format("\n\nHistogram barev z %d obrázků:\nObrázky mají dohromady %spx\n\n", imageCollection.imagesLoaded, Helpers.makeNumberReadable(totalPixels));
                Map<String, Integer> calculatedValues = imageCollection.getMostCommonColorsAll();
                for (Map.Entry<String, Integer> entry : calculatedValues.entrySet())
                {
                    String color = entry.getKey();
                    int pixels = entry.getValue();

                    double percent = (double)pixels/((double)(totalPixels)/100);

                    Color converted = Color.decode(color);
                    Attribute bkgColor = BACK_COLOR(converted.getRed(), converted.getGreen(), converted.getBlue());
                    System.out.print(colorize("           ", bkgColor));
                    System.out.format(" %s - celkem v %s pixelech (%.2f%% z celkového počtu)\n", color, Helpers.makeNumberReadable(pixels), percent);
                }
                promptEnterKey();
            }
            case 2 -> { // metadata do console
                try
                {
                    System.out.println(imageCollection.getMetadataAll());
                }
                catch(IOException | ImageProcessingException exception)
                {
                    System.out.println("Při čtení metadat se vyskytla chyba");
                }
                promptEnterKey();
            }
            case 3 -> { // metadata do txt
                try
                {
                    imageCollection.saveMetadataAll();
                }
                catch(IOException | ImageProcessingException exception)
                {
                    System.out.println("Error při generování textového souboru");
                }
                promptEnterKey();
            }
            case 4 -> { // cernobily
                try
                {
                    imageCollection.copyAndMakeGreyAll();
                    System.out.println("Černobílé obrázky byly úspěšně vygenerovány a uloženy");
                }
                catch(IOException exception)
                {
                    System.out.println("Při vytváření černobílého obrázku se vyskytla chyba");
                }
                promptEnterKey();
            }
            case 5 -> { // rozmazany
                try
                {
                    imageCollection.copyAndBlurAll();
                    System.out.println("Rozmazané obrázky byly úspěšně vygenerovány a uloženy");
                }
                catch(IOException exception)
                {
                    System.out.println("Při rozmazávání obrázku se vyskytla chyba");
                }
                promptEnterKey();
            }
            case 6 -> { // invertovany
                try
                {
                    imageCollection.invertAll();
                    System.out.println("Invertované obrázky byly úspěšně vygenerovány a uloženy");
                }
                catch(IOException exception)
                {
                    System.out.println("Při invertování souboru se vyskytla chyba");
                }
                promptEnterKey();
            }
        }
    }
    private static void submenuFive()
    {
        System.out.println("\n\n\nAktuálně načtené obrázky:\n\n");
        ArrayList<Image> images = imageCollection.getImages();
        int iteration = 1;
        for(Image currentImage : images)
        {
            System.out.format("%d. %s\n", iteration, currentImage.getName());
            iteration += 1;
        }

        System.out.println("Zadej číslo obrázku, pro který chceš provést akci:");
        String input = sc.next();

        if(!validateInput(input, 1, images.size()))
        {
            System.out.println("\nNeznámá instrukce");
            promptEnterKey();
        }

        int validInput = Integer.parseInt(input);
        Image selectedImage = imageCollection.getImage(validInput-1);

        System.out.println("\n\n\nakce pro jeden obrázek\n=====================\n1 = zobrazit histogram barev v obrázku\n2 = zobrazit metadata obrázku\n3 = vygenerovat metadata do txt souboru\n4 = vygenerovat černobílý obrázek\n5 = vygenerovat rozmazaný obrázek\n6 = vygenerovat obrázek s invertovanými barvami\n7 = zpět\n");
        String input2 = sc.next();

        if(!validateInput(input2, 1, 7))
        {
            System.out.println("\nNeznámá instrukce");
            promptEnterKey();
        }

        int validInput2 = Integer.parseInt(input2);

        switch (validInput2) {
            case 1 ->
            { // histogram
                int totalPixels = selectedImage.getTotalPixels();
                System.out.format("\n\nHistogram barev z obrázku:\nObrázek má %spx\n\n", Helpers.makeNumberReadable(totalPixels));
                Map<String, Integer> calculatedValues = selectedImage.getMostCommonColors();
                for (Map.Entry<String, Integer> entry : calculatedValues.entrySet())
                {
                    String color = entry.getKey();
                    int pixels = entry.getValue();

                    double percent = (double)pixels/((double)(totalPixels)/100);

                    Color converted = Color.decode(color);
                    Attribute bkgColor = BACK_COLOR(converted.getRed(), converted.getGreen(), converted.getBlue());
                    System.out.print(colorize("           ", bkgColor));
                    System.out.format(" %s - celkem v %s pixelech (%.2f%% z celkového počtu)\n", color, Helpers.makeNumberReadable(pixels), percent);
                }
                promptEnterKey();
            }
            case 2 -> { // metadata do console
                try
                {
                    System.out.println(imageCollection.getMetadataAll());
                }
                catch(IOException | ImageProcessingException exception)
                {
                    System.out.println("Při čtení metadat se vyskytla chyba");
                }
                promptEnterKey();
            }
            case 3 -> { // metadata do txt
                try
                {
                    selectedImage.saveMetadata();
                }
                catch(IOException | ImageProcessingException exception)
                {
                    System.out.println("Error při generování textového souboru");
                }
                promptEnterKey();
            }
            case 4 -> { // cernobily
                try
                {
                    selectedImage.copyAndMakeGrey();
                    System.out.println("Černobílé obrázky byly úspěšně vygenerovány a uloženy");
                }
                catch(IOException exception)
                {
                    System.out.println("Při vytváření černobílého obrázku se vyskytla chyba");
                }
                System.out.println("Černobílý obrázek byl úspěšně vygenerován a uložen");
                promptEnterKey();
            }
            case 5 -> { // rozmazany
                try
                {
                    selectedImage.copyAndBlur();
                    System.out.println("Rozmazané obrázky byly úspěšně vygenerovány a uloženy");
                }
                catch(IOException exception)
                {
                    System.out.println("Při rozmazávání obrázku se vyskytla chyba");
                }
                System.out.println("Rozmazaný obrázek byl úspěšně vygenerován a uložen");
                promptEnterKey();
            }
            case 6 -> { // invertovany
                try
                {
                    selectedImage.copyAndInvert();
                    System.out.println("Invertované obrázky byly úspěšně vygenerovány a uloženy");
                }
                catch(IOException exception)
                {
                    System.out.println("Při invertování souboru se vyskytla chyba");
                }
                System.out.println("Invertovaný obrázek byl úspěšně vygenerován a uložen");
                promptEnterKey();
            }
        }
    }

    private static boolean validateInput(String input, int min, int max)
    {
        int validInput;
        try
        {
            validInput = Integer.parseInt(input);
            if(validInput < min || validInput > max)
            {
                return false;
            }
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
        return true;
    }

    private static void promptEnterKey(){
        System.out.println("\nzmáčkni ENTER pro pokračování");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    private static int loadImages()
    {
        List<String> availableExtensions = Arrays.asList("png", "jpg", "jpeg");
        File f = new File("img/");
        File [] files = f.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name)
            {
                String[] splittedString = name.split("\\.");
                String extension = splittedString[splittedString.length-1];
                return availableExtensions.contains(extension);
            }
        });
        return imageCollection.fillCollection(files);
    }
}
