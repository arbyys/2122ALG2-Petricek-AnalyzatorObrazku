package utils;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.text.DecimalFormat;

public final class Helpers {
    private Helpers() {}

    /**
     * Pomocná metoda na vytvoření čitelné verze dlouhého čísla
     * @param input
     * @return String
     */
    public static String makeNumberReadable(int input)
    {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(input);
    }

    /**
     * Pomocná metoda na klonování daného obrázku
     * @param bufferImage
     * @return BufferedImage
     */
    public static BufferedImage cloneBufferedImage(BufferedImage bufferImage)
    {
        ColorModel colorModel = bufferImage.getColorModel();
        WritableRaster raster = bufferImage.copyData(null);
        boolean isAlphaPremultiplied = colorModel.isAlphaPremultiplied();
        return new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
    }

}
