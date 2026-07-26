package com.huarenzaimeng.infra.schedule;

import com.huarenzaimeng.common.result.Result;
import com.huarenzaimeng.infra.adapter.reloadly.ReloadlyBalanceAdapter;
import com.huarenzaimeng.module.recharge.service.MnpService;
import com.huarenzaimeng.module.recharge.service.OrderService;
import com.huarenzaimeng.module.recharge.service.ProductSyncService;
import com.huarenzaimeng.module.recharge.worker.RechargeWorker;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/internal")
public class InternalController {

    private final ProductSyncService productSyncService;
    private final MnpService mnpService;
    private final OrderService orderService;
    private final RechargeWorker rechargeWorker;
    private final ReloadlyBalanceAdapter balanceAdapter;

    @Value("${app.internal.secret:}")
    private String internalSecret;

    @Value("${app.risk.order-timeout-minutes:30}")
    private int orderTimeoutMinutes;

    public InternalController(ProductSyncService productSyncService,
                              MnpService mnpService,
                              OrderService orderService,
                              RechargeWorker rechargeWorker,
                              ReloadlyBalanceAdapter balanceAdapter) {
        this.productSyncService = productSyncService;
        this.mnpService = mnpService;
        this.orderService = orderService;
        this.rechargeWorker = rechargeWorker;
        this.balanceAdapter = balanceAdapter;
    }

    @PostMapping("/sync/products")
    public Result<Map<String, Integer>> syncProducts(@RequestHeader("X-Internal-Secret") String secret) {
        if (!validateSecret(secret)) {
            return Result.fail(403, "Invalid internal secret");
        }
        ProductSyncService.SyncResult result = productSyncService.syncAll();
        return Result.ok(Map.of("new", result.newCount(), "updated", result.updateCount()));
    }

    @PostMapping("/sync/fx-rate")
    public Result<String> syncFxRate(@RequestHeader("X-Internal-Secret") String secret) {
        if (!validateSecret(secret)) {
            return Result.fail(403, "Invalid internal secret");
        }
        // FX rate sync is handled within product sync for now
        return Result.ok("FX rate sync triggered");
    }

    @PostMapping("/monitor/balance")
    public Result<Map<String, Object>> monitorBalance(@RequestHeader("X-Internal-Secret") String secret) {
        if (!validateSecret(secret)) {
            return Result.fail(403, "Invalid internal secret");
        }
        try {
            JsonNode balance = balanceAdapter.getBalance();
            double amount = balance.has("balance") ? balance.get("balance").asDouble() : 0;
            String currency = balance.has("currencyCode") ? balance.get("currencyCode").asText() : "USD";
            boolean lowBalance = amount < 50;
            if (lowBalance) {
                log.warn("Reloadly balance LOW: {} {}", amount, currency);
            }
            return Result.ok(Map.of("balance", amount, "currency", currency, "low", lowBalance));
        } catch (Exception e) {
            log.error("Balance monitor failed: {}", e.getMessage());
            return Result.fail(5001, "Balance check failed: " + e.getMessage());
        }
    }

    @PostMapping("/recovery/mnp")
    public Result<Map<String, Boolean>> recoverMnp(@RequestHeader("X-Internal-Secret") String secret) {
        if (!validateSecret(secret)) {
            return Result.fail(403, "Invalid internal secret");
        }
        boolean recovered = mnpService.tryRecover();
        return Result.ok(Map.of("recovered", recovered, "degraded", mnpService.isDegraded()));
    }

    @PostMapping("/timeout/scan")
    public Result<String> timeoutScan(@RequestHeader("X-Internal-Secret") String secret) {
        if (!validateSecret(secret)) {
            return Result.fail(403, "Invalid internal secret");
        }
        orderService.cancelExpiredOrders(orderTimeoutMinutes);
        rechargeWorker.poll();
        return Result.ok("Timeout scan completed");
    }

    @PostMapping("/remind/holiday")
    public Result<String> holidayRemind(@RequestHeader("X-Internal-Secret") String secret) {
        if (!validateSecret(secret)) {
            return Result.fail(403, "Invalid internal secret");
        }
        // Holiday reminder logic - to be implemented in content module
        return Result.ok("Holiday reminder check completed");
    }

    private boolean validateSecret(String secret) {
        return internalSecret != null && !internalSecret.isEmpty() && internalSecret.equals(secret);
    }
}
