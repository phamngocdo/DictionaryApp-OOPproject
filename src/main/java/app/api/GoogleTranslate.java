package app.api;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GoogleTranslate {

    private static final String SCRIPT_URL = "https://script.google.com/macros/s/AKfycbyfwrd_46ZVQMXMQH21Q2sORE-UYkYSNo1NWcMXzCYWbM-XmxQ4eqOl6luxyKEGMV6Y/exec";
    public static String translate(String text, String sourceLanguage, String targetLanguage) {
        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String url = SCRIPT_URL + "?text=" + encodedText + "&sourceLanguage=" + sourceLanguage + "&targetLanguage=" + targetLanguage;
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(url);
            CloseableHttpResponse response = client.execute(request);
            try {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity);
            } 
            finally {
                response.close();
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    public static void main(String[] args) {
        //Test here
        String text = "Hello, how are you today?";
        System.out.println(translate(text, "en", "vi"));

    }
}
