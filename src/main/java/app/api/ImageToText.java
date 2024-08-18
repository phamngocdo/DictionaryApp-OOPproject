package app.api;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class ImageToText {

    public static String getText(URL imageUrl, String sourceLanguage) throws URISyntaxException {
        String result = "Error";
        String imagePath;
        imagePath = new File(imageUrl.toURI()).getAbsolutePath();
        URL tessDataUrl = ImageToText.class.getResource("/tessdata/");
        if (tessDataUrl == null) {
            System.err.println("Tessdata directory not found.");
            return result;
        }
        if (sourceLanguage.equals("en")){
            sourceLanguage += "g";
        }
        else if (sourceLanguage.equals("vi")){
            sourceLanguage += "e";
        }
        String dataPath;
        try {
            dataPath = new File(tessDataUrl.toURI()).getAbsolutePath();
        } 
        catch (URISyntaxException e) {
            e.printStackTrace();
            return result;
        }
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(dataPath);
        tesseract.setLanguage(sourceLanguage); 
        File imageFile = new File(imagePath);
        try {
            result = tesseract.doOCR(imageFile);
        } 
        catch (TesseractException e) {
            e.printStackTrace();
        }
        
        return result;
    }

    public static void main(String[] args) {
        try {
            System.out.println(getText(null, null));
        } 
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
