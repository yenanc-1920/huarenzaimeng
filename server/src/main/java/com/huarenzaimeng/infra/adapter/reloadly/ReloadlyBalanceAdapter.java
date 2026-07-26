package com.huarenzaimeng.infra.adapter.reloadly;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReloadlyBalanceAdapter {

    private final ReloadlyClient client;

    public ReloadlyBalanceAdapter(ReloadlyClient client) {
        this.client = client;
    }

    public JsonNode getBalance() {
        return client.get("/accounts/balance");
    }
}
