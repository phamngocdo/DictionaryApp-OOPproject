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

    public static ArrayList<Pair<Integer, String>> getSynonyms(String word){
        return responseAPI(SYN_API, word);
    }

    public static ArrayList<Pair<Integer, String>> getAntonyms(String word){
        return responseAPI(ANT_API, word);
    }

    private static ArrayList<Pair<Integer, String>> responseAPI(String API, String word){
        ArrayList<Pair<Integer, String>> list = new ArrayList<>();
        String encodedWord;
        try {
            encodedWord = URLEncoder.encode(word, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return list;
        }

        String urlStr = API + encodedWord;
        CloseableHttpClient client = HttpClients.createDefault();
        try {
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
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
        finally {
            try {
                client.close();
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static void main(String[] args) {
        //Test
        ArrayList<Pair<Integer, String>> list = getSynonyms("good");
        for(Pair<Integer, String> str : list) {
            System.out.println(str.getValue());
        }
    }
}
