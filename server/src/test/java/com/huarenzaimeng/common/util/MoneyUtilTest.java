package com.huarenzaimeng.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MoneyUtil 单元测试")
class MoneyUtilTest {

    @Test
    @DisplayName("USD转CNY - 含损耗率和利润率")
    void usdToCny_normal() {
        BigDecimal result = MoneyUtil.usdToCny(
                new BigDecimal("5.00"),
                new BigDecimal("7.25"),
                new BigDecimal("0.02"),
                new BigDecimal("0.08"));

        // 5 * 7.25 = 36.25, * 1.02 = 36.975, * 1.08 = 39.933 -> 39.93
        assertEquals(new BigDecimal("39.93"), result);
    }

    @Test
    @DisplayName("USD转CNY - 零损耗零利润")
    void usdToCny_zeroRates() {
        BigDecimal result = MoneyUtil.usdToCny(
                new BigDecimal("10.00"),
                new BigDecimal("7.20"),
                BigDecimal.ZERO,
                BigDecimal.ZERO);

        assertEquals(new BigDecimal("72.00"), result);
    }

    @Test
    @DisplayName("乘法 - 保留两位小数")
    void multiply_scale() {
        BigDecimal result = MoneyUtil.multiply(new BigDecimal("3.33"), new BigDecimal("3"));
        assertEquals(new BigDecimal("9.99"), result);
    }

    @Test
    @DisplayName("应用费率")
    void applyRate_normal() {
        BigDecimal result = MoneyUtil.applyRate(new BigDecimal("100.00"), new BigDecimal("0.05"));
        assertEquals(new BigDecimal("105.00"), result);
    }

    @Test
    @DisplayName("比较大小")
    void isGreaterThan_normal() {
        assertTrue(MoneyUtil.isGreaterThan(new BigDecimal("10.01"), new BigDecimal("10.00")));
        assertFalse(MoneyUtil.isGreaterThan(new BigDecimal("10.00"), new BigDecimal("10.00")));
        assertFalse(MoneyUtil.isGreaterThan(new BigDecimal("9.99"), new BigDecimal("10.00")));
    }
}
