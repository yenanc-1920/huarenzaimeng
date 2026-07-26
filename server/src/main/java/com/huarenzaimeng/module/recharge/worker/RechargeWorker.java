package com.huarenzaimeng.module.recharge.worker;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.huarenzaimeng.common.constant.OrderStatus;
import com.huarenzaimeng.infra.adapter.reloadly.ReloadlyTopupAdapter;
import com.huarenzaimeng.module.recharge.entity.Order;
import com.huarenzaimeng.module.recharge.mapper.OrderMapper;
import com.huarenzaimeng.module.recharge.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Component
public class RechargeWorker {

    private static final int MAX_RETRY = 3;
    private static final int TIMEOUT_MINUTES = 15;
    private static final int[] RETRY_INTERVALS = {30, 60, 120};

    private final OrderMapper orderMapper;
    private final OrderService orderService;
    private final ReloadlyTopupAdapter topupAdapter;

    public RechargeWorker(OrderMapper orderMapper, OrderService orderService,
                          ReloadlyTopupAdapter topupAdapter) {
        this.orderMapper = orderMapper;
        this.orderService = orderService;
        this.topupAdapter = topupAdapter;
    }

    public void poll() {
        List<Order> chargingOrders = orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderStatus, OrderStatus.CHARGING.name())
                .isNotNull(Order::getReloadlyTransactionId));

        for (Order order : chargingOrders) {
            processOrder(order);
        }

        handleTimeouts();
    }

    private void processOrder(Order order) {
        if (order.getRetryCount() >= MAX_RETRY) {
            orderService.markException(order.getOrderNo(), "EXCEPTION_MAX_RETRY_EXCEEDED");
            return;
        }

        try {
            JsonNode result = topupAdapter.getTransactionStatus(order.getReloadlyTransactionId());
            String status = result.has("status") ? result.get("status").asText() : "";

            switch (status.toUpperCase()) {
                case "SUCCESSFUL" -> orderService.markSuccess(order.getOrderNo());
                case "FAILED" -> orderService.markFailed(order.getOrderNo(),
                        result.has("message") ? result.get("message").asText() : "Reloadly confirmed failed");
                default -> {
                    order.setRetryCount(order.getRetryCount() + 1);
                    orderMapper.updateById(order);
                    log.debug("Order {} still processing, retry={}", order.getOrderNo(), order.getRetryCount());
                }
            }
        } catch (Exception e) {
            order.setRetryCount(order.getRetryCount() + 1);
            orderMapper.updateById(order);
            log.warn("Order {} poll failed: {}", order.getOrderNo(), e.getMessage());
        }
    }

    private void handleTimeouts() {
        LocalDateTime deadline = LocalDateTime.now(ZoneOffset.UTC).minusMinutes(TIMEOUT_MINUTES);
        List<Order> timedOut = orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderStatus, OrderStatus.CHARGING.name())
                .lt(Order::getUpdatedAt, deadline));

        for (Order order : timedOut) {
            orderService.markException(order.getOrderNo(), "EXCEPTION_CHARGE_TIMEOUT");
            log.error("Order {} timed out after {} minutes", order.getOrderNo(), TIMEOUT_MINUTES);
        }
    }
}
