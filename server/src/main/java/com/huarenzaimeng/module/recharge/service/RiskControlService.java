package com.huarenzaimeng.module.recharge.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huarenzaimeng.common.constant.OrderStatus;
import com.huarenzaimeng.common.exception.BusinessException;
import com.huarenzaimeng.common.result.ResultCode;
import com.huarenzaimeng.module.recharge.entity.Order;
import com.huarenzaimeng.module.recharge.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
public class RiskControlService {

    private final OrderMapper orderMapper;

    @Value("${app.risk.openid-daily-limit:9}")
    private int openidDailyLimit;

    @Value("${app.risk.phone-daily-limit:5}")
    private int phoneDailyLimit;

    @Value("${app.risk.order-max-amount:500}")
    private BigDecimal orderMaxAmount;

    @Value("${app.risk.daily-max-amount:1000}")
    private BigDecimal dailyMaxAmount;

    public RiskControlService(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    public void checkFrequency(String openid, String phone) {
        LocalDateTime dayStart = getDayStart();

        Long openidCount = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getOpenid, openid)
                .ge(Order::getCreatedAt, dayStart)
                .ne(Order::getOrderStatus, OrderStatus.CANCELLED.name())
                .ne(Order::getOrderStatus, OrderStatus.PENDING_PAY.name()));

        if (openidCount >= openidDailyLimit) {
            throw new BusinessException(ResultCode.RISK_FREQUENCY_LIMIT, "今日充值次数已达上限(" + openidDailyLimit + "次)");
        }

        Long phoneCount = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getPhone, phone)
                .ge(Order::getCreatedAt, dayStart)
                .ne(Order::getOrderStatus, OrderStatus.CANCELLED.name())
                .ne(Order::getOrderStatus, OrderStatus.PENDING_PAY.name()));

        if (phoneCount >= phoneDailyLimit) {
            throw new BusinessException(ResultCode.RISK_FREQUENCY_LIMIT, "该号码今日充值次数已达上限(" + phoneDailyLimit + "次)");
        }
    }

    public void checkAmount(String openid, BigDecimal amount) {
        if (amount.compareTo(orderMaxAmount) > 0) {
            throw new BusinessException(ResultCode.RISK_AMOUNT_LIMIT, "单笔金额超出限制(最大" + orderMaxAmount + "元)");
        }

        LocalDateTime dayStart = getDayStart();
        List<Order> todayOrders = orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .eq(Order::getOpenid, openid)
                .ge(Order::getCreatedAt, dayStart)
                .ne(Order::getOrderStatus, OrderStatus.CANCELLED.name())
                .ne(Order::getOrderStatus, OrderStatus.PENDING_PAY.name()));

        BigDecimal totalToday = todayOrders.stream()
                .map(Order::getCnyPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalToday.add(amount).compareTo(dailyMaxAmount) > 0) {
            throw new BusinessException(ResultCode.RISK_AMOUNT_LIMIT, "今日累计金额超出限制(最大" + dailyMaxAmount + "元)");
        }
    }

    private LocalDateTime getDayStart() {
        // 日切线：北京时间00:00 = UTC 16:00前一天
        LocalDate todayBeijing = LocalDate.now(ZoneOffset.ofHours(8));
        return todayBeijing.atStartOfDay().minusHours(8);
    }
}
