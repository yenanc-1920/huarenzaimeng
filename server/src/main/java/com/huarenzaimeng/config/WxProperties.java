package com.huarenzaimeng.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.wx")
public class WxProperties {

    private String appId;
    private String appSecret;
    private String mchId;
    private String mchKey;
}
