package com.huarenzaimeng.infra.adapter.reloadly;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ReloadlyMnpAdapter {

    private final ReloadlyClient client;

    public ReloadlyMnpAdapter(ReloadlyClient client) {
        this.client = client;
    }

    public JsonNode lookup(String phone) {
        Map<String, Object> body = Map.of(
                "phoneNumber", phone,
                "countryCode", "BD"
        );
        return client.post("/lookup", body);
    }

    public JsonNode autoDetect(String phone) {
        return client.get("/auto-detect?phoneNumber=" + phone + "&countryCode=BD");
    }
}
