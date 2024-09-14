package app.api;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private static final String VOICELESS_URL = "http://api.voicerss.org/";
    private static final HashMap<String, String> VOICES = new HashMap<>();
    private static final String FORMAT = "44khz_16bit_stereo";
    private static final String CODEC = "MP3";
    private static final Object PLAYER_LOCK = new Object();
    private static Player currentPlayer;

    public static synchronized void speakText(String text, String language) throws IOException {
        if (text == null) {
            return;
        }
        synchronized (PLAYER_LOCK) {
            if (currentPlayer != null) {
                currentPlayer.close();
            }
        }

        byte[] audio;
        String voice = VOICES.get(language);
        String encodedText;
        encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
        String urlStr = String.format("%s?key=%s&hl=%s&v=%s&c=%s&f=%s&src=%s", VOICELESS_URL, API_KEY, language, voice, CODEC, FORMAT, encodedText);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(urlStr);
        HttpResponse response = httpClient.execute(request);
        audio = EntityUtils.toByteArray(response.getEntity());
        new Thread(() -> {
            try {
                playAudio(audio);
            } catch (IOException | JavaLayerException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public static void addVoice() {
        VOICES.put("en-us", "Linda");
        VOICES.put("en-gb", "Harry");
        VOICES.put("vi-vn", "Chi");
    }

    private static void playAudio(byte[] audio) throws IOException, JavaLayerException {
        Path tempFile = Files.createTempFile("audio", "." + CODEC.toLowerCase());
        Files.write(tempFile, audio);
        try (FileInputStream fis = new FileInputStream(tempFile.toFile())) {
            synchronized (PLAYER_LOCK) {
                currentPlayer = new Player(fis);
            }
            currentPlayer.play();
        } 
        finally {
            Files.deleteIfExists(tempFile);
        }
    }

    public static void stopSpeaking() {
        synchronized (PLAYER_LOCK) {
            if (currentPlayer != null) {
                currentPlayer.close();
                currentPlayer = null;
            }
        }
    }

    public static void main(String[] args) {
        //Test
        addVoice();
        try {
            speakText("Xin chào", "vi-vn");
        } catch (IOException e) {
            System.out.println("No internet connection");
        }
    }
}
