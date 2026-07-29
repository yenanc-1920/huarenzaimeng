package com.huarenzaimeng.infra.adapter.reloadly;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.huarenzaimeng.common.exception.ReloadlyException;
import com.huarenzaimeng.common.result.ResultCode;
import com.huarenzaimeng.config.ReloadlyProperties;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ReloadlyClient {

    private static final String TOKEN_CACHE_KEY = "reloadly_token";
    private static final MediaType JSON_MEDIA = MediaType.parse("application/json; charset=utf-8");

    private final ReloadlyProperties properties;
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient;
    private final Cache<String, String> tokenCache;

    public ReloadlyClient(ReloadlyProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        this.tokenCache = Caffeine.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(23, TimeUnit.HOURS)
                .build();
    }

    public String getAccessToken() {
        String cached = tokenCache.getIfPresent(TOKEN_CACHE_KEY);
        if (cached != null) {
            return cached;
        }
        return refreshToken();
    }

    private synchronized String refreshToken() {
        String cached = tokenCache.getIfPresent(TOKEN_CACHE_KEY);
        if (cached != null) {
            return cached;
        }

        String body;
        try {
            body = objectMapper.writeValueAsString(Map.of(
                    "client_id", properties.getClientId().trim(),
                    "client_secret", properties.getClientSecret().trim(),
                    "grant_type", "client_credentials",
                    "audience", properties.getApiUrl()
            ));
        } catch (IOException e) {
            throw new ReloadlyException("Reloadly OAuth request serialization failed", "OAUTH_SERIALIZE");
        }

        Request request = new Request.Builder()
                .url(properties.getAuthUrl())
                .post(RequestBody.create(body, JSON_MEDIA))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new ReloadlyException("Reloadly OAuth failed: HTTP " + response.code(), "OAUTH_ERROR");
            }
            JsonNode json = objectMapper.readTree(response.body().string());
            String token = json.get("access_token").asText();
            tokenCache.put(TOKEN_CACHE_KEY, token);
            log.info("Reloadly token refreshed successfully");
            return token;
        } catch (IOException e) {
            throw new ReloadlyException("Reloadly OAuth network error: " + e.getMessage(), "OAUTH_NETWORK");
        }
    }

    public JsonNode get(String path) {
        Request request = new Request.Builder()
                .url(properties.getApiUrl() + path)
                .header("Authorization", "Bearer " + getAccessToken())
                .header("Accept", "application/json")
                .get()
                .build();
        return execute(request);
    }

    public JsonNode post(String path, Object requestBody) {
        String json;
        try {
            json = objectMapper.writeValueAsString(requestBody);
        } catch (IOException e) {
            throw new ReloadlyException("Request serialization error", "SERIALIZE_ERROR");
        }

        Request request = new Request.Builder()
                .url(properties.getApiUrl() + path)
                .header("Authorization", "Bearer " + getAccessToken())
                .header("Content-Type", "application/json")
                .post(RequestBody.create(json, JSON_MEDIA))
                .build();
        return execute(request);
    }

    private JsonNode execute(Request request) {
        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";

            if (response.code() == 401) {
                tokenCache.invalidate(TOKEN_CACHE_KEY);
                throw new ReloadlyException("Reloadly token expired", "TOKEN_EXPIRED");
            }

            JsonNode json = objectMapper.readTree(responseBody);

            if (!response.isSuccessful()) {
                String errorCode = json.has("errorCode") ? json.get("errorCode").asText() : "HTTP_" + response.code();
                String message = json.has("message") ? json.get("message").asText() : responseBody;
                throw new ReloadlyException("Reloadly API error: " + message, errorCode);
            }

            return json;
        } catch (IOException e) {
            throw new ReloadlyException("Reloadly network error: " + e.getMessage(), "NETWORK_ERROR");
        }
    }

    public void invalidateToken() {
        tokenCache.invalidate(TOKEN_CACHE_KEY);
    }
}
