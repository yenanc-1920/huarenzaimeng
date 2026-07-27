package com.huarenzaimeng.module.recharge.service;

import com.huarenzaimeng.common.exception.BusinessException;
import com.huarenzaimeng.module.recharge.entity.Order;
import com.huarenzaimeng.module.recharge.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RiskControlService 单元测试")
class RiskControlServiceTest {

    @Mock
    private OrderMapper orderMapper;

    private RiskControlService riskControlService;

    @BeforeEach
    void setUp() {
        riskControlService = new RiskControlService(orderMapper);
        ReflectionTestUtils.setField(riskControlService, "openidDailyLimit", 9);
        ReflectionTestUtils.setField(riskControlService, "phoneDailyLimit", 5);
        ReflectionTestUtils.setField(riskControlService, "orderMaxAmount", new BigDecimal("500"));
        ReflectionTestUtils.setField(riskControlService, "dailyMaxAmount", new BigDecimal("1000"));
    }

    @Test
    @DisplayName("频率检查 - 正常通过")
    void checkFrequency_pass() {
        when(orderMapper.selectCount(any())).thenReturn(3L);

        assertDoesNotThrow(() -> riskControlService.checkFrequency("openid_1", "01712345678"));
    }

    @Test
    @DisplayName("频率检查 - openid达到上限")
    void checkFrequency_openidLimit() {
        when(orderMapper.selectCount(any())).thenReturn(9L);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                riskControlService.checkFrequency("openid_1", "01712345678"));
        assertTrue(ex.getMessage().contains("上限"));
    }

    @Test
    @DisplayName("频率检查 - 手机号达到上限")
    void checkFrequency_phoneLimit() {
        when(orderMapper.selectCount(any()))
                .thenReturn(3L)
                .thenReturn(5L);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                riskControlService.checkFrequency("openid_1", "01712345678"));
        assertTrue(ex.getMessage().contains("号码"));
    }

    @Test
    @DisplayName("金额检查 - 单笔超限")
    void checkAmount_singleExceed() {
        BusinessException ex = assertThrows(BusinessException.class, () ->
                riskControlService.checkAmount("openid_1", new BigDecimal("501")));
        assertTrue(ex.getMessage().contains("单笔"));
    }

    @Test
    @DisplayName("金额检查 - 单笔正好500通过")
    void checkAmount_singleExactly500() {
        when(orderMapper.selectList(any())).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> riskControlService.checkAmount("openid_1", new BigDecimal("500")));
    }

    @Test
    @DisplayName("金额检查 - 日累计超限")
    void checkAmount_dailyExceed() {
        Order existingOrder = new Order();
        existingOrder.setCnyPrice(new BigDecimal("800"));

        when(orderMapper.selectList(any())).thenReturn(List.of(existingOrder));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                riskControlService.checkAmount("openid_1", new BigDecimal("300")));
        assertTrue(ex.getMessage().contains("累计"));
    }

    @Test
    @DisplayName("金额检查 - 日累计未超限通过")
    void checkAmount_dailyPass() {
        Order existingOrder = new Order();
        existingOrder.setCnyPrice(new BigDecimal("200"));

        when(orderMapper.selectList(any())).thenReturn(List.of(existingOrder));

        assertDoesNotThrow(() -> riskControlService.checkAmount("openid_1", new BigDecimal("300")));
    }
}
