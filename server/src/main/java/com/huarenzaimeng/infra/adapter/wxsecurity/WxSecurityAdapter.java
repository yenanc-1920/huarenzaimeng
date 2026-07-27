package com.huarenzaimeng.infra.adapter.wxsecurity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huarenzaimeng.config.WxProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class WxSecurityAdapter {

    private final WxProperties wxProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();

    private static final String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
    private static final String MSG_SEC_CHECK_URL = "https://api.weixin.qq.com/wxa/msg_sec_check";
    private static final String MEDIA_CHECK_URL = "https://api.weixin.qq.com/wxa/media_check_async";

    private volatile String accessToken;
    private volatile long tokenExpireAt = 0;

    public synchronized String getAccessToken() {
        if (accessToken != null && System.currentTimeMillis() < tokenExpireAt) {
            return accessToken;
        }
        try {
            HttpUrl url = HttpUrl.parse(TOKEN_URL).newBuilder()
                    .addQueryParameter("grant_type", "client_credential")
                    .addQueryParameter("appid", wxProperties.getAppId())
                    .addQueryParameter("secret", wxProperties.getAppSecret())
                    .build();

            Request request = new Request.Builder().url(url).get().build();
            try (Response response = httpClient.newCall(request).execute()) {
                JsonNode json = objectMapper.readTree(response.body().string());
                if (json.has("access_token")) {
                    accessToken = json.get("access_token").asText();
                    int expiresIn = json.get("expires_in").asInt();
                    tokenExpireAt = System.currentTimeMillis() + (expiresIn - 300) * 1000L;
                    return accessToken;
                }
                log.error("Failed to get wx access_token: {}", json);
            }
        } catch (IOException e) {
            log.error("Error getting wx access_token", e);
        }
        return null;
    }

    /**
     * 文本内容安全检测（同步）
     * @return true=通过, false=不通过
     */
    public boolean msgSecCheck(String content, String openid) {
        try {
            String token = getAccessToken();
            if (token == null) {
                log.warn("msgSecCheck skipped: no access_token");
                return true;
            }

            Map<String, Object> body = Map.of(
                    "version", 2,
                    "scene", 2,
                    "openid", openid != null ? openid : "",
                    "content", content
            );

            RequestBody requestBody = RequestBody.create(
                    objectMapper.writeValueAsString(body),
                    MediaType.parse("application/json"));

            HttpUrl url = HttpUrl.parse(MSG_SEC_CHECK_URL).newBuilder()
                    .addQueryParameter("access_token", token)
                    .build();

            Request request = new Request.Builder().url(url).post(requestBody).build();
            try (Response response = httpClient.newCall(request).execute()) {
                JsonNode json = objectMapper.readTree(response.body().string());
                int errCode = json.has("errcode") ? json.get("errcode").asInt() : -1;
                if (errCode == 0) {
                    JsonNode result = json.get("result");
                    if (result != null && result.has("suggest")) {
                        String suggest = result.get("suggest").asText();
                        return !"risky".equals(suggest);
                    }
                    return true;
                }
                log.warn("msgSecCheck error: errcode={}, errmsg={}", errCode,
                        json.has("errmsg") ? json.get("errmsg").asText() : "");
                return true;
            }
        } catch (Exception e) {
            log.error("msgSecCheck exception", e);
            return true;
        }
    }

    /**
     * 图片异步安全检测
     * @return traceId 用于回调匹配
     */
    public String mediaCheckAsync(String mediaUrl, String openid) {
        try {
            String token = getAccessToken();
            if (token == null) {
                log.warn("mediaCheckAsync skipped: no access_token");
                return null;
            }

            Map<String, Object> body = Map.of(
                    "version", 2,
                    "scene", 2,
                    "openid", openid != null ? openid : "",
                    "media_url", mediaUrl,
                    "media_type", 2
            );

            RequestBody requestBody = RequestBody.create(
                    objectMapper.writeValueAsString(body),
                    MediaType.parse("application/json"));

            HttpUrl url = HttpUrl.parse(MEDIA_CHECK_URL).newBuilder()
                    .addQueryParameter("access_token", token)
                    .build();

            Request request = new Request.Builder().url(url).post(requestBody).build();
            try (Response response = httpClient.newCall(request).execute()) {
                JsonNode json = objectMapper.readTree(response.body().string());
                int errCode = json.has("errcode") ? json.get("errcode").asInt() : -1;
                if (errCode == 0 && json.has("trace_id")) {
                    return json.get("trace_id").asText();
                }
                log.warn("mediaCheckAsync error: {}", json);
            }
        } catch (Exception e) {
            log.error("mediaCheckAsync exception", e);
        }
        return null;
    }
}
