package com.huarenzaimeng.module.recharge.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huarenzaimeng.common.constant.OrderStatus;
import com.huarenzaimeng.common.constant.RefundStatus;
import com.huarenzaimeng.common.util.OrderNoGenerator;
import com.huarenzaimeng.module.payment.service.WxPayService;
import com.huarenzaimeng.module.recharge.entity.Order;
import com.huarenzaimeng.module.recharge.entity.Refund;
import com.huarenzaimeng.module.recharge.mapper.OrderMapper;
import com.huarenzaimeng.module.recharge.mapper.RefundMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class RefundService {

    private final RefundMapper refundMapper;
    private final OrderMapper orderMapper;
    private final WxPayService wxPayService;
    private final OrderService orderService;

    private final AtomicInteger consecutiveFailures = new AtomicInteger(0);
    private volatile boolean circuitBroken = false;

    @Value("${app.risk.refund-circuit-break-count:3}")
    private int circuitBreakThreshold;

    public RefundService(RefundMapper refundMapper, OrderMapper orderMapper,
                         WxPayService wxPayService, OrderService orderService) {
        this.refundMapper = refundMapper;
        this.orderMapper = orderMapper;
        this.wxPayService = wxPayService;
        this.orderService = orderService;
    }

    public Refund autoRefund(String orderNo, String reason) {
        if (circuitBroken) {
            log.error("Refund circuit broken, skipping auto refund for order: {}", orderNo);
            return null;
        }

        Order order = orderService.getByOrderNo(orderNo);
        if (order == null || !OrderStatus.FAILED.name().equals(order.getOrderStatus())) {
            log.warn("Order {} not eligible for auto refund", orderNo);
            return null;
        }

        return doRefund(order, "AUTO", reason);
    }

    public Refund manualRefund(String orderNo, Long adminId, String reason) {
        Order order = orderService.getByOrderNo(orderNo);
        if (order == null) {
            return null;
        }
        Refund refund = doRefund(order, "MANUAL", reason);
        if (refund != null) {
            refund.setOperatorId(adminId);
            refundMapper.updateById(refund);
        }
        return refund;
    }

    public Refund preFundedRefund(String orderNo, String reason) {
        Order order = orderService.getByOrderNo(orderNo);
        if (order == null) {
            return null;
        }
        return doRefund(order, "PRE_FUNDED", reason);
    }

    private Refund doRefund(Order order, String refundType, String reason) {
        Refund refund = new Refund();
        refund.setRefundNo("RF" + OrderNoGenerator.generate().substring(2));
        refund.setOrderNo(order.getOrderNo());
        refund.setOpenid(order.getOpenid());
        refund.setRefundAmount(order.getCnyPrice());
        refund.setRefundType(refundType);
        refund.setRefundStatus(RefundStatus.PROCESSING.name());
        refund.setReason(reason);
        refundMapper.insert(refund);

        try {
            int totalCents = order.getCnyPrice().multiply(new java.math.BigDecimal("100")).intValue();
            Map<String, String> result = wxPayService.refund(
                    order.getOrderNo(), refund.getRefundNo(), totalCents, totalCents, reason);

            if ("SUCCESS".equals(result.get("result_code"))) {
                refund.setRefundStatus(RefundStatus.SUCCESS.name());
                refund.setWxRefundId(result.get("refund_id"));
                refund.setRefundedAt(LocalDateTime.now(ZoneOffset.UTC));
                consecutiveFailures.set(0);
                orderService.transitionStatus(order.getOrderNo(),
                        OrderStatus.valueOf(order.getOrderStatus()), OrderStatus.REFUNDED);
                log.info("Refund success: refundNo={}, orderNo={}", refund.getRefundNo(), order.getOrderNo());
            } else {
                refund.setRefundStatus(RefundStatus.FAILED.name());
                onRefundFailure();
            }
        } catch (Exception e) {
            refund.setRefundStatus(RefundStatus.FAILED.name());
            log.error("Refund exception: refundNo={}, error={}", refund.getRefundNo(), e.getMessage());
            onRefundFailure();
        }

        refundMapper.updateById(refund);
        return refund;
    }

    private void onRefundFailure() {
        int count = consecutiveFailures.incrementAndGet();
        if (count >= circuitBreakThreshold) {
            circuitBroken = true;
            log.error("Refund circuit breaker triggered: {} consecutive failures", count);
        }
    }

    public boolean isCircuitBroken() {
        return circuitBroken;
    }

    public void resetCircuitBreaker() {
        circuitBroken = false;
        consecutiveFailures.set(0);
        log.info("Refund circuit breaker reset by admin");
    }
}
