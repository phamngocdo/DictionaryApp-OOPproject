package app.api;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class TextToSpeech {
    private static final String API_KEY = "68001ae522624aed9acf52141cbb75ed";
    private static final String VOICERSS_URL = "http://api.voicerss.org/";
    private static HashMap<String, String> voices = new HashMap<>();
    private static String format = "44khz_16bit_stereo"; 
    private static String codec = "MP3";
    private static Player currentPlayer; 
    private static final Object playerLock = new Object(); 

    public static synchronized void speakText(String text, String language) {
        if (text == null) {
            return;
        }
        synchronized (playerLock) {
            if (currentPlayer != null) {
                currentPlayer.close();
            }
        }

        byte[] audio;
        String voice = voices.get(language);
        String encodedText;
        try {
            encodedText = URLEncoder.encode(text, "UTF-8");
        } 
        catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String urlStr = String.format("%s?key=%s&hl=%s&v=%s&c=%s&f=%s&src=%s", VOICERSS_URL, API_KEY, language, voice, codec, format, encodedText);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpGet request = new HttpGet(urlStr);
            HttpResponse response = httpClient.execute(request);
            audio = EntityUtils.toByteArray(response.getEntity());
            new Thread(() -> {
                try {
                    playAudio(audio);
                } 
                catch (IOException | JavaLayerException e) {
                    e.printStackTrace();
                }
            }).start();
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
        finally {
            try {
                httpClient.close();
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addVoice() {
        voices.put("en-us", "Linda");
        voices.put("en-gb", "Harry");
        voices.put("vi-vn", "Chi");
    }

    private static void playAudio(byte[] audio) throws IOException, JavaLayerException {
        Path tempFile = Files.createTempFile("audio", "." + codec.toLowerCase());
        Files.write(tempFile, audio);
        try (FileInputStream fis = new FileInputStream(tempFile.toFile())) {
            synchronized (playerLock) {
                currentPlayer = new Player(fis);
            }
            currentPlayer.play();
        } 
        finally {
            Files.deleteIfExists(tempFile);
        }
    }

    public static void stopSpeaking() {
        synchronized (playerLock) {
            if (currentPlayer != null) {
                currentPlayer.close();
                currentPlayer = null;
            }
        }
    }

    public static void main(String[] args) {
        //Test
        addVoice();
        speakText("Xin chào", "vi-vn");
    }
}
