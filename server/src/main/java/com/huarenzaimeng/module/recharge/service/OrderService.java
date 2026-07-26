package com.huarenzaimeng.module.recharge.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huarenzaimeng.common.constant.OrderStatus;
import com.huarenzaimeng.common.exception.BusinessException;
import com.huarenzaimeng.common.result.ResultCode;
import com.huarenzaimeng.common.util.OrderNoGenerator;
import com.huarenzaimeng.module.recharge.entity.Order;
import com.huarenzaimeng.module.recharge.entity.Product;
import com.huarenzaimeng.module.recharge.mapper.OrderMapper;
import com.huarenzaimeng.module.recharge.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

@Slf4j
@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final RiskControlService riskControlService;

    public OrderService(OrderMapper orderMapper, ProductMapper productMapper,
                        RiskControlService riskControlService) {
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
        this.riskControlService = riskControlService;
    }

    public Order createOrder(String openid, String phone, Long productId) {
        riskControlService.checkFrequency(openid, phone);

        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() != 1) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        riskControlService.checkAmount(openid, product.getCnyPrice());

        Order order = new Order();
        order.setOrderNo(OrderNoGenerator.generate());
        order.setOpenid(openid);
        order.setPhone(phone);
        order.setOperatorId(product.getOperatorId());
        order.setProductId(product.getId());
        order.setTopupType(product.getTopupType());
        order.setUsdCost(product.getUsdCost());
        order.setBdtAmount(product.getBdtAmount());
        order.setCnyPrice(product.getCnyPrice());
        order.setOrderStatus(OrderStatus.PENDING_PAY.name());
        order.setMnpVerified(1);
        order.setRetryCount(0);

        orderMapper.insert(order);
        log.info("Order created: orderNo={}, openid={}, amount={}", order.getOrderNo(), openid, product.getCnyPrice());
        return order;
    }

    public boolean transitionStatus(String orderNo, OrderStatus from, OrderStatus to) {
        int rows = orderMapper.updateStatus(orderNo, from.name(), to.name());
        if (rows == 1) {
            log.info("Order status transition: {} -> {} -> {}", orderNo, from, to);
            return true;
        }
        log.warn("Order status transition failed (concurrent): {} {} -> {}", orderNo, from, to);
        return false;
    }

    public Order getByOrderNo(String orderNo) {
        return orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo)
                .eq(Order::getDeleted, 0));
    }

    public void markPaid(String orderNo, String wxTransactionId) {
        Order order = getByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        order.setWxTransactionId(wxTransactionId);
        order.setPaidAt(LocalDateTime.now(ZoneOffset.UTC));
        orderMapper.updateById(order);
        transitionStatus(orderNo, OrderStatus.PENDING_PAY, OrderStatus.PENDING_CHARGE);
    }

    public void markCharging(String orderNo, Long reloadlyTransactionId) {
        Order order = getByOrderNo(orderNo);
        if (order != null) {
            order.setReloadlyTransactionId(reloadlyTransactionId);
            orderMapper.updateById(order);
        }
        transitionStatus(orderNo, OrderStatus.PENDING_CHARGE, OrderStatus.CHARGING);
    }

    public void markSuccess(String orderNo) {
        Order order = getByOrderNo(orderNo);
        if (order != null) {
            order.setChargedAt(LocalDateTime.now(ZoneOffset.UTC));
            orderMapper.updateById(order);
        }
        transitionStatus(orderNo, OrderStatus.CHARGING, OrderStatus.SUCCESS);
    }

    public void markFailed(String orderNo, String reason) {
        Order order = getByOrderNo(orderNo);
        if (order != null) {
            order.setStatusDesc(reason);
            orderMapper.updateById(order);
        }
        transitionStatus(orderNo, OrderStatus.CHARGING, OrderStatus.FAILED);
    }

    public void markException(String orderNo, String desc) {
        Order order = getByOrderNo(orderNo);
        if (order != null) {
            order.setStatusDesc(desc);
            orderMapper.updateById(order);
        }
        transitionStatus(orderNo, OrderStatus.CHARGING, OrderStatus.EXCEPTION);
    }

    public void cancelExpiredOrders(int timeoutMinutes) {
        LocalDateTime deadline = LocalDateTime.now(ZoneOffset.UTC).minusMinutes(timeoutMinutes);
        var expiredOrders = orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderStatus, OrderStatus.PENDING_PAY.name())
                .lt(Order::getCreatedAt, deadline));

        for (Order order : expiredOrders) {
            transitionStatus(order.getOrderNo(), OrderStatus.PENDING_PAY, OrderStatus.CANCELLED);
            log.info("Order cancelled (timeout): {}", order.getOrderNo());
        }
    }

    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<Order> getOrdersByOpenid(
            String openid, int page, int size) {
        var pageParam = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<Order>(page, size);
        return orderMapper.selectPage(pageParam, new LambdaQueryWrapper<Order>()
                .eq(Order::getOpenid, openid)
                .orderByDesc(Order::getCreatedAt));
    }
}
