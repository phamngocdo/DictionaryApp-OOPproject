package app.api;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class GoogleFormFeedback {

    private static final String GOOGLE_FORM_URL = "https://docs.google.com/forms/d/e/1FAIpQLSef6hAHRVYxCV1YD_6VbFL5mJBz9qTBtMCk32UbZ_7XbeVa6g/formResponse";
    private static final String FEEDBACK_ENTRY_ID = "entry.661606306";

    public static void sendFeedback(String feedback) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(GOOGLE_FORM_URL);
            String encodedFeedback = URLEncoder.encode(feedback, StandardCharsets.UTF_8.toString());
            String payload = String.format("%s=%s", FEEDBACK_ENTRY_ID, encodedFeedback);
            StringEntity entity = new StringEntity(payload, "UTF-8");
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setEntity(entity);
            httpClient.execute(httpPost);
            System.out.println("Feedback sent successfully to Google Forms!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}