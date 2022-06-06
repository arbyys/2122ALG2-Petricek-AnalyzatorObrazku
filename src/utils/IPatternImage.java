package utils;

import com.drew.imaging.ImageProcessingException;

import java.io.IOException;
import java.util.Map;

/**
 * Rozhraní, které implementuje třída obrázku
 * @author Adam Petříček
 */
public interface IPatternImage extends Comparable<IPatternImage>
{
    public String getName();
    public String getExtension();

    public StringBuilder getMetadata() throws ImageProcessingException, IOException;
    public void saveMetadata() throws IOException, ImageProcessingException;
    public int getTotalPixels();
    public Map<String, Integer> getMostCommonColors();
    public void copyAndInvert() throws IOException;
    public void copyAndMakeGrey() throws IOException;
    public void copyAndBlur() throws IOException;

}
