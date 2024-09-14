package app.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import app.trie.Trie;
import javafx.util.Pair;

public class WordRelations {
    private static final String SYN_API = "https://api.datamuse.com/words?rel_syn=";
    private static final String ANT_API = "https://api.datamuse.com/words?rel_ant=";

    public static ArrayList<Pair<Integer, String>> getSynonyms(String word) throws IOException {
        return responseAPI(SYN_API, word);
    }

    public static ArrayList<Pair<Integer, String>> getAntonyms(String word) throws IOException {
        return responseAPI(ANT_API, word);
    }

    private static ArrayList<Pair<Integer, String>> responseAPI(String API, String word) throws IOException {
        ArrayList<Pair<Integer, String>> list = new ArrayList<>();
        String encodedWord;
        encodedWord = URLEncoder.encode(word, StandardCharsets.UTF_8);

        String urlStr = API + encodedWord;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(urlStr);
        HttpResponse response = client.execute(request);
        String jsonResponse = EntityUtils.toString(response.getEntity());
        JSONArray jsonArray = new JSONArray(jsonResponse);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            Pair<Integer, String> temp = Trie.getWord(obj.getString("word"));
            if (temp != null){
                list.add(temp);
            }
        }
        return list;
    }

    public static void main(String[] args) {
        //Test
        ArrayList<Pair<Integer, String>> list = null;
        try {
            list = getSynonyms("good");
            for(Pair<Integer, String> str : list) {
                System.out.println(str.getValue());
            }
        } catch (IOException e) {
            System.out.println("No internet connection");
        }

    }
}
