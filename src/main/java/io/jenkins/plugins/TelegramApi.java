package io.jenkins.plugins;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TelegramApi {

    private final String botToken;

    private final ObjectMapper objectMapper;

    public TelegramApi(@NonNull String botToken) {
        this.botToken = botToken;
        this.objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void sendMessage(@NonNull String[] chatIds, String message) throws IOException, TelegramApiException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            for (String chatId: chatIds) {
                HttpPost httpPost = new HttpPost(String.format(
                    "https://api.telegram.org/bot%s/sendMessage",
                    botToken
                ));
                httpPost.addHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");

                List<NameValuePair> nvps = new ArrayList<>();
                nvps.add(new BasicNameValuePair("chat_id", chatId));
                nvps.add(new BasicNameValuePair("text", message));
                httpPost.setEntity(new UrlEncodedFormEntity(nvps));

                SendMessageResponse response = httpClient.execute(httpPost, resp -> {
                    HttpEntity entity = resp.getEntity();
                    return this.objectMapper.readValue(entity.getContent(), SendMessageResponse.class);
                });
                if (!response.isOk()) {
                    throw new TelegramApiException(response.getErrorCode(), response.getDescription());
                }
            }
        }
    }
}
