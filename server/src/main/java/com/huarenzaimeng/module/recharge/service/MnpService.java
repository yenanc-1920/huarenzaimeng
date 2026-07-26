package com.huarenzaimeng.module.recharge.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.huarenzaimeng.common.exception.ReloadlyException;
import com.huarenzaimeng.infra.adapter.reloadly.ReloadlyMnpAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class MnpService {

    private static final int DEGRADE_THRESHOLD = 3;

    private final ReloadlyMnpAdapter mnpAdapter;
    private final AtomicInteger consecutiveTimeouts = new AtomicInteger(0);
    private volatile boolean degraded = false;

    public MnpService(ReloadlyMnpAdapter mnpAdapter) {
        this.mnpAdapter = mnpAdapter;
    }

    public MnpResult verify(String phone) {
        if (degraded) {
            log.warn("MNP degraded mode: skipping lookup for phone={}", phone);
            return new MnpResult(false, null, true);
        }

        try {
            JsonNode result = mnpAdapter.lookup(phone);
            consecutiveTimeouts.set(0);

            long operatorId = result.has("operatorId") ? result.get("operatorId").asLong() : 0;
            boolean valid = result.has("isValid") && result.get("isValid").asBoolean();

            return new MnpResult(valid, operatorId, false);
        } catch (ReloadlyException e) {
            if ("NETWORK_ERROR".equals(e.getReloadlyErrorCode())) {
                int count = consecutiveTimeouts.incrementAndGet();
                log.warn("MNP timeout count: {}/{}", count, DEGRADE_THRESHOLD);
                if (count >= DEGRADE_THRESHOLD) {
                    degraded = true;
                    log.error("MNP degraded: {} consecutive timeouts, switching to local matching", count);
                }
            }
            return new MnpResult(false, null, true);
        }
    }

    public boolean tryRecover() {
        if (!degraded) {
            return true;
        }
        try {
            mnpAdapter.lookup("01700000000");
            degraded = false;
            consecutiveTimeouts.set(0);
            log.info("MNP recovered: service restored");
            return true;
        } catch (Exception e) {
            log.debug("MNP recovery probe failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isDegraded() {
        return degraded;
    }

    public record MnpResult(boolean valid, Long operatorId, boolean degraded) {
    }
}
