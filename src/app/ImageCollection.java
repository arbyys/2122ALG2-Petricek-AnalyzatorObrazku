package app;

import com.drew.imaging.ImageProcessingException;
import utils.SortingMethod;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Třída zajišťující uložení obrázků do kolekce
 * @author Adam Petříček
 */
public class ImageCollection {
    public int imagesLoaded;
    private ArrayList<Image> images;
    private String changed;

    public ArrayList<Image> getImages() {
        return images;
    }

    public String getChanged()
    {
        return changed;
    }

    public Image getImage(int i)
    {
        return images.get(i);
    }

    /**
     * Konstruktor
     */
    public ImageCollection()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        this.changed = formatter.format(LocalTime.now());
        this.imagesLoaded = 0;
        this.images = new ArrayList<>();
    }

    /**
     * Smaže obrázek z kolekce podle indexu
     * @param index
     */
    public void removeImage(int index)
    {
        images.remove(index-1);
        imagesLoaded-=1;
    }

    /**
     * Naplní kolekci novými instancemi příslušené třídy, vrací počet obrázků co se načetly
     * @param files
     * @return int
     */
    public int fillCollection(File[] files)
    {
        if(files == null || files.length == 0)
        {
            return 0;
        }
        images = new ArrayList<>();
        imagesLoaded = 0;
        for (File currentFile : files)
        {
            boolean succesfullyCreated = false;
            Image i = new Image(currentFile);
            try
            {
                images.add(new Image(currentFile));
                succesfullyCreated = true;
            }
            catch(Exception ignored)
            {

            }
            if(succesfullyCreated)
            {
                imagesLoaded += 1;
            }
        }
        sort(SortingMethod.name);
        return imagesLoaded;
    }

    /**
     * Vrací celkový počet pixelů všech obrázků v kolekci
     * @return int
     */
    public int getTotalPixelsAll()
    {
        int total = 0;
        for(Image image : images )
        {
            total += image.getTotalPixels();
        }
        return total;
    }

    /**
     * Vrací metadata všech obrázků v kolekci
     * @return StringBuilder
     * @throws ImageProcessingException
     * @throws IOException
     */
    public StringBuilder getMetadataAll() throws ImageProcessingException, IOException
    {
        StringBuilder returnString = new StringBuilder();
        for(Image image : images)
        {
            returnString.append(image.getMetadata());
        }
        return returnString;
    }

    /**
     * Ukládá metadata všech obrázků v kolekci do souboru
     * @throws IOException
     * @throws ImageProcessingException
     */
    public void saveMetadataAll() throws IOException, ImageProcessingException {
        for(Image image : images)
        {
            image.saveMetadata();
        }
    }

    /**
     * Vytváří černobílé kopie všech obrázků v kolekci
     * @throws IOException
     */
    public void copyAndMakeGreyAll() throws IOException
    {
        for(Image image : images)
        {
            image.copyAndMakeGrey();
        }
    }

    /**
     * Vytváří rozmazané kopie všech obrázků v kolekci
     * @throws IOException
     */
    public void copyAndBlurAll() throws IOException
    {
        for(Image image : images)
        {
            image.copyAndBlur();
        }
    }

    /**
     * Vrací invertované kopie všech obrázků v kolekci
     * @throws IOException
     */
    public void invertAll() throws IOException
    {
        for(Image image : images)
        {
            image.copyAndInvert();
        }
    }

    /**
     * Vrací mapu barvy a počtu výskytů ze všech obrázků v kolekci
     * @return
     */
    public Map<String, Integer> getMostCommonColorsAll()
    {
        Map<String, Integer> map = new HashMap<>();
        for(Image image : images)
        {
            Map<String, Integer> returnedValues = image.getMostCommonColors();
            returnedValues.forEach((k, v) -> map.merge(k, v, Integer::sum));
        }
        return map.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(15)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    /**
     * Metoda pro seřazení kolekce podle příslušného způsobu
     * @param method
     */
    public void sort(SortingMethod method)
    {
        switch(method)
        {
            case name ->
            { // podle jména
                Collections.sort(images);
            }
            case extension ->
            { // podle přípony
                images.sort((Image i1, Image i2) -> i1.getExtension().compareToIgnoreCase(i2.getExtension()));
            }
        }
    }
}
