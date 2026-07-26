package com.huarenzaimeng.infra.adapter.reloadly;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ReloadlyFxRateAdapter {

    private final ReloadlyClient client;

    public ReloadlyFxRateAdapter(ReloadlyClient client) {
        this.client = client;
    }

    public JsonNode getFxRate(long operatorId) {
        Map<String, Object> body = Map.of("operatorId", operatorId);
        return client.post("/fx-rate", body);
    }
}
