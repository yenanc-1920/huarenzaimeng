package com.huarenzaimeng.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderNoGenerator 单元测试")
class OrderNoGeneratorTest {

    @Test
    @DisplayName("订单号格式 - HM前缀+22位总长")
    void generate_format() {
        String orderNo = OrderNoGenerator.generate();

        assertTrue(orderNo.startsWith("HM"));
        assertEquals(22, orderNo.length());
    }

    @Test
    @DisplayName("订单号唯一性 - 100次生成无重复")
    void generate_uniqueness() {
        Set<String> orderNos = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            orderNos.add(OrderNoGenerator.generate());
        }
        assertEquals(100, orderNos.size());
    }

    @Test
    @DisplayName("订单号纯数字后缀")
    void generate_numericSuffix() {
        String orderNo = OrderNoGenerator.generate();
        String suffix = orderNo.substring(2);
        assertTrue(suffix.matches("\\d{20}"));
    }
}
