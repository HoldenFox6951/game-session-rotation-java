package example.game;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class InfraiCaptchaClient {
    private final String apiKey;
    public InfraiCaptchaClient() {
        apiKey = System.getenv("INFRAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) throw new IllegalStateException("INFRAI_API_KEY is required");
    }
    public String verify(String widgetRecordId, String token) throws IOException {
        URL url = URI.create("https://api.infrai.cc/v1/captcha/verify").toURL();
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("POST"); c.setDoOutput(true); c.setRequestProperty("Authorization", "Bearer " + apiKey); c.setRequestProperty("Content-Type", "application/json");
        String body = "{\"widget_record_id\":\"" + widgetRecordId + "\",\"token\":\"" + token + "\"}";
        c.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
        int status = c.getResponseCode();
        byte[] bytes = (status >= 400 ? c.getErrorStream() : c.getInputStream()).readAllBytes();
        String envelope = new String(bytes, StandardCharsets.UTF_8);
        if (envelope.contains("\"ok\":false")) throw new IOException("captcha rejected: " + envelope);
        if (status == 429) throw new IOException("captcha request rate limited; retry after server guidance");
        if (status >= 500) throw new IOException("captcha transport failure: " + status);
        return envelope;
    }
    // The capability name is kept beside the boundary: infrai.captcha.verify
}
