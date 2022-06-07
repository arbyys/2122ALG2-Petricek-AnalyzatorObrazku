# Analyzátor obrázků - Adam Petříček
___

## Zadání práce

Aplikace slouží pro analýzu jednoho či více obrázků zároveň. Program dokáže vypsat veškeré informace o obrázku, jeho metadata tagy, histogram četnosti barev (i s vizualizací). Kromě vypisování informací dokáže aplikace dané obrázky také rozmazat, udělat je černobílé a invertovat jim barvy. Aplikace pracuje se zabudovanou pamětí obrázků, do které může uživatel libovolně načítat další obrázky známých přípon (`.png`, `.jpg`, `.jpeg`) ze složky `img`, která se nachází v rootu projektu. Do této složky se i ukládají vygenerované obrázky + metadata. Uživatel si může v aplikaci vybrat, zda bude pracovat se všemi obrázky, nebo pouze s jedním vybraným.

## Funkční specifikace

- analýza obrázků + aplikace barevných filtrů
- png, jpg, jpeg formáty
- aplikace na všechny obrázky naráz nebo jen na jeden vybraný

Uživatel má po zapnutí aplikace na výběr menu, zde je jeho přibližný popis:
- `1` - načíst obsah složky `img` do paměti aplikace
- `2` - vypsat všechny načtené obrázky
- `3` - smazat jeden obrázek z paměti podle výběru
- `4` - akce pro jeden obrázek
    - `1` - zobrazit histogram barev
    - `2` - zobrazit metatagy
    - `3` - vygenerovat metatagy do txt souboru
    - `4` - vygenerovat černobílou kopii obrázku
    - `5` - vygenerovat rozmazanou kopii obrázku
    - `6` - vygenerovat invertovanou kopii obrázku
    - `7` - zpět
- `5` - akce pro všechny obrázky 
    - `1` - zobrazit histogram barev
    - `2` - zobrazit metatagy
    - `3` - vygenerovat metatagy do txt souboru
    - `4` - vygenerovat černobílou kopii obrázku
    - `5` - vygenerovat rozmazanou kopii obrázku
    - `6` - vygenerovat invertovanou kopii obrázku
    - `7` - zpět

Informace o aplikaci:

- v aplikaci jsou ve složce `img` vytvořeny 4 testovací obrázky
- obrázky vygenerované aplikací se vždy uloží do stejné složky, jejich název se skládá z názvu původního obrázku podtržítko provedená akce (grey / blur / inverted)
    - např obrázek `vlk.png` se po provedení akce rozmazání bude jmenovat `vlk_blur.png`
- histogram barev se zobrazuje společně s vizualizací a přesným vykreslením barvy

Informace o filtrech:

-  černobílý filtr barvu každého pixelu vypočítává pomocí součtu všech tří barevných složek (R,G,B) a vydělením třemi
- filtr rozmazání používá metodu Gaussian blur, pro kterou vypočítává příslušnou matici s radiusem 4.5
- filtr inverze barev každou barevnou složku (R,G,B) odečítá od 255, čímž vypočítá její inverzní hodnotu

## Class diagram

![Class diagram](https://www.arbystools.eu/storage/uploaded/5955.png)

## Požadavky

1. Menu, které umožní opakovaný výběr funkcí aplikace a ukončení aplikace ✓
2. Přehledný výpis výsledků na konzoli - použijte alespoň jednou String.format() a StringBuilder ✓
3. Načítání vstupních dat z minimálně dvou souborů ✓
4. Zápis výstupních dat do souboru ✓
5. Možnost práce s textovými a binárními soubory (alespoň někde) ✓
6. Ideálně využití reálných otevřených dat ✓
7. Adresář data se všemi datovými soubory a případně třídu Datastore se statickými metodami, které budou poskytovat další statická data ✓
8. Tři balíčky:
   1. `ui` – třídy, tvořící uživatelské rozhraní - komunikaci s uživatelem ✓
   2. `app` – třídy, tvořící logiku s daty aplikace - modely, kontrolery ✓
   3. `utils` – pomocné třídy např. vlastní výjimky, vlastní rozhraní ✓
9. Programování vůči rozhraní a použití vlastního rozhraní ✓
10. Použití java.time API pro práci s časem ✓
11. Použít enum typ ✓
12. Použití kontejnerové třídy jazyka Java (ArrayList, LinkedList, HashMap ...) z Collections frameworku ✓
13. Alespoň dvě možnosti třídění s využitím rozhraní Comparable a Comparator ✓
14. Použití regulárního výrazu ✓
15. Ošetření vstupů, aby chybné vstupy nezpůsobily pád programu - pomocí existujících a vlastních výjimek ✓
16. Vhodné ošetření povinně ošetřovaných výjimek ✓
17. Použití Vámi vybrané externí knihovny (audio, posílání emailů, práce s obrázkem, junit testování, jiné formáty uložení dat ...) ✓
18. Javadoc - každá třída a metoda musí mít javadoc popis, abyste mohli na závěr vygenerovat javadoc dokumentaci ✓

## Příklad fungování externí knihovny


1) [metadata-extractor](https://github.com/drewnoakes/metadata-extractor)
    ```java
    StringBuilder returnString = new StringBuilder();
    Metadata metadata = null;
    metadata = ImageMetadataReader.readMetadata(new File(path));
    for (Directory directory : metadata.getDirectories())
    {
        for (Tag tag : directory.getTags()) {
            returnString.append(String.format("%s\n", tag));
        }
    }
    ```
2) [JColor](https://github.com/dialex/JColor)
    ```java
    Attribute bkgColor = BACK_COLOR(255, 0, 0);
    System.out.print(colorize("červený text", bkgColor));
    ```
