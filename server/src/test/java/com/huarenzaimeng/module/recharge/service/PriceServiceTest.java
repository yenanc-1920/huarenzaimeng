package com.huarenzaimeng.module.recharge.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PriceService 单元测试")
class PriceServiceTest {

    private PriceService priceService;

    @BeforeEach
    void setUp() {
        priceService = new PriceService();
    }

    @Test
    @DisplayName("自动定价 - 正常计算")
    void calculateAutoPrice_normal() {
        BigDecimal usdCost = new BigDecimal("5.00");
        BigDecimal fxRate = new BigDecimal("7.25");
        BigDecimal lossRate = new BigDecimal("0.02");
        BigDecimal profitRate = new BigDecimal("0.08");

        BigDecimal result = priceService.calculateAutoPrice(usdCost, fxRate, lossRate, profitRate);

        // 5.00 * 7.25 = 36.25, * 1.02 = 36.975, * 1.08 = 39.933 -> 39.93
        assertEquals(new BigDecimal("39.93"), result);
    }

    @Test
    @DisplayName("自动定价 - 汇率为null使用兜底")
    void calculateAutoPrice_nullFxRate() {
        BigDecimal usdCost = new BigDecimal("10.00");

        BigDecimal result = priceService.calculateAutoPrice(usdCost, null, BigDecimal.ZERO, BigDecimal.ZERO);

        assertEquals(new BigDecimal("72.00"), result);
    }

    @Test
    @DisplayName("自动定价 - 汇率为0使用兜底")
    void calculateAutoPrice_zeroFxRate() {
        BigDecimal usdCost = new BigDecimal("10.00");

        BigDecimal result = priceService.calculateAutoPrice(usdCost, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        assertEquals(new BigDecimal("72.00"), result);
    }

    @Test
    @DisplayName("价格倒挂检测 - 售价低于成本")
    void isPriceInverted_true() {
        BigDecimal cnyPrice = new BigDecimal("35.00");
        BigDecimal usdCost = new BigDecimal("5.00");
        BigDecimal fxRate = new BigDecimal("7.25");

        assertTrue(priceService.isPriceInverted(cnyPrice, usdCost, fxRate));
    }

    @Test
    @DisplayName("价格倒挂检测 - 售价高于成本")
    void isPriceInverted_false() {
        BigDecimal cnyPrice = new BigDecimal("40.00");
        BigDecimal usdCost = new BigDecimal("5.00");
        BigDecimal fxRate = new BigDecimal("7.25");

        assertFalse(priceService.isPriceInverted(cnyPrice, usdCost, fxRate));
    }

    @Test
    @DisplayName("价格倒挂检测 - 汇率无效返回false")
    void isPriceInverted_invalidFxRate() {
        assertFalse(priceService.isPriceInverted(new BigDecimal("10"), new BigDecimal("5"), null));
        assertFalse(priceService.isPriceInverted(new BigDecimal("10"), new BigDecimal("5"), BigDecimal.ZERO));
    }

    @Test
    @DisplayName("价格变动超阈值 - 超过2%")
    void isPriceChangeExceedsThreshold_exceeds() {
        BigDecimal oldPrice = new BigDecimal("100.00");
        BigDecimal newPrice = new BigDecimal("103.00");
        BigDecimal threshold = new BigDecimal("0.02");

        assertTrue(priceService.isPriceChangeExceedsThreshold(oldPrice, newPrice, threshold));
    }

    @Test
    @DisplayName("价格变动超阈值 - 未超过2%")
    void isPriceChangeExceedsThreshold_within() {
        BigDecimal oldPrice = new BigDecimal("100.00");
        BigDecimal newPrice = new BigDecimal("101.50");
        BigDecimal threshold = new BigDecimal("0.02");

        assertFalse(priceService.isPriceChangeExceedsThreshold(oldPrice, newPrice, threshold));
    }

    @Test
    @DisplayName("价格变动超阈值 - 旧价格为0返回false")
    void isPriceChangeExceedsThreshold_zeroOldPrice() {
        assertFalse(priceService.isPriceChangeExceedsThreshold(BigDecimal.ZERO, new BigDecimal("100"), new BigDecimal("0.02")));
        assertFalse(priceService.isPriceChangeExceedsThreshold(null, new BigDecimal("100"), new BigDecimal("0.02")));
    }
}
