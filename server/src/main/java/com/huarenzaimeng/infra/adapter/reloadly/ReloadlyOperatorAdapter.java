package com.huarenzaimeng.infra.adapter.reloadly;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReloadlyOperatorAdapter {

    private final ReloadlyClient client;

    public ReloadlyOperatorAdapter(ReloadlyClient client) {
        this.client = client;
    }

    public JsonNode getOperators(String countryCode) {
        return client.get("/operators?countryCode=" + countryCode + "&includeBundles=true&includeDataPlans=true");
    }

    public JsonNode getOperatorFxRates() {
        return client.get("/operators/fx-rates?countryCode=BD");
    }
}
