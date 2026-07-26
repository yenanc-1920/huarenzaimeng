package com.huarenzaimeng.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.reloadly")
public class ReloadlyProperties {

    private String clientId;
    private String clientSecret;
    private String authUrl;
    private String apiUrl;
}
