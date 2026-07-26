package com.huarenzaimeng.module.recharge.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.huarenzaimeng.common.constant.OrderStatus;
import com.huarenzaimeng.common.exception.ReloadlyException;
import com.huarenzaimeng.infra.adapter.reloadly.ReloadlyTopupAdapter;
import com.huarenzaimeng.module.recharge.entity.Order;
import com.huarenzaimeng.module.recharge.entity.Operator;
import com.huarenzaimeng.module.recharge.mapper.OperatorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RechargeService {

    private final ReloadlyTopupAdapter topupAdapter;
    private final OperatorMapper operatorMapper;
    private final OrderService orderService;

    public RechargeService(ReloadlyTopupAdapter topupAdapter,
                           OperatorMapper operatorMapper,
                           OrderService orderService) {
        this.topupAdapter = topupAdapter;
        this.operatorMapper = operatorMapper;
        this.orderService = orderService;
    }

    public void executeTopup(Order order) {
        boolean transitioned = orderService.transitionStatus(
                order.getOrderNo(), OrderStatus.PENDING_CHARGE, OrderStatus.CHARGING);
        if (!transitioned) {
            log.info("Order {} already being processed, skip", order.getOrderNo());
            return;
        }

        Operator operator = operatorMapper.selectById(order.getOperatorId());
        if (operator == null) {
            orderService.markException(order.getOrderNo(), "EXCEPTION_OPERATOR_NOT_FOUND");
            return;
        }

        try {
            JsonNode result = topupAdapter.topup(
                    order.getOrderNo(),
                    operator.getReloadlyOperatorId(),
                    order.getPhone(),
                    order.getUsdCost().doubleValue());

            String status = result.has("status") ? result.get("status").asText() : "";
            long transactionId = result.has("transactionId") ? result.get("transactionId").asLong() : 0;

            order.setReloadlyTransactionId(transactionId);

            switch (status.toUpperCase()) {
                case "SUCCESSFUL" -> orderService.markSuccess(order.getOrderNo());
                case "FAILED" -> orderService.markFailed(order.getOrderNo(),
                        result.has("message") ? result.get("message").asText() : "Reloadly failed");
                default -> log.info("Order {} topup pending, transactionId={}", order.getOrderNo(), transactionId);
            }
        } catch (ReloadlyException e) {
            if ("NETWORK_ERROR".equals(e.getReloadlyErrorCode())) {
                log.warn("Order {} topup timeout, will retry via worker", order.getOrderNo());
            } else {
                orderService.markFailed(order.getOrderNo(), e.getReloadlyErrorCode() + ": " + e.getMessage());
            }
        }
    }
}
