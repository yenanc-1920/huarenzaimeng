package com.huarenzaimeng.module.content.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huarenzaimeng.module.content.service.CompanyService;
import com.huarenzaimeng.module.content.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/wx/security")
@RequiredArgsConstructor
public class WxSecurityCallbackController {

    private final NewsService newsService;
    private final CompanyService companyService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/callback")
    public Map<String, Object> onMediaCheckResult(@RequestBody String body) {
        try {
            JsonNode json = objectMapper.readTree(body);
            log.info("Wx security callback: {}", body);

            if (json.has("result") && json.has("trace_id")) {
                JsonNode result = json.get("result");
                int suggest = result.has("suggest") ? parseSuggest(result.get("suggest").asText()) : 0;
                String traceId = json.get("trace_id").asText();

                // traceId 格式约定: {type}_{entityId}
                // type: news_cover / company_logo
                if (traceId.contains("_")) {
                    String[] parts = traceId.split("_", 2);
                    String type = parts[0];
                    Long entityId = Long.parseLong(parts[1]);
                    String auditStatus = suggest == 0 ? "PASS" : "REJECT";

                    if ("news".equals(type)) {
                        newsService.updateAuditStatus(entityId, auditStatus);
                    } else if ("company".equals(type)) {
                        companyService.updateLogoAuditStatus(entityId, auditStatus);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error processing wx security callback", e);
        }
        return Map.of("errcode", 0, "errmsg", "ok");
    }

    private int parseSuggest(String suggest) {
        return switch (suggest) {
            case "pass" -> 0;
            case "review" -> 1;
            case "risky" -> 2;
            default -> 0;
        };
    }
}
