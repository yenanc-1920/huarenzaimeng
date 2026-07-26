package com.huarenzaimeng.infra.adapter.reloadly;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ReloadlyTopupAdapter {

    private final ReloadlyClient client;

    public ReloadlyTopupAdapter(ReloadlyClient client) {
        this.client = client;
    }

    public JsonNode topup(String customIdentifier, long operatorId, String phone, double amount) {
        Map<String, Object> body = new HashMap<>();
        body.put("recipientPhone", Map.of("countryCode", "BD", "number", phone));
        body.put("operatorId", operatorId);
        body.put("amount", amount);
        body.put("customIdentifier", customIdentifier);
        body.put("useLocalAmount", false);

        log.info("Reloadly topup request: customIdentifier={}, operatorId={}, phone={}, amount={}",
                customIdentifier, operatorId, phone, amount);

        JsonNode result = client.post("/topups", body);

        log.info("Reloadly topup response: transactionId={}, status={}",
                result.has("transactionId") ? result.get("transactionId").asText() : "N/A",
                result.has("status") ? result.get("status").asText() : "N/A");

        return result;
    }

    public JsonNode getTransactionStatus(long transactionId) {
        return client.get("/topups/" + transactionId);
    }
}
