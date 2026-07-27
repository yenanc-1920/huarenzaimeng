package com.huarenzaimeng.module.recharge.service;

import com.huarenzaimeng.common.constant.OrderStatus;
import com.huarenzaimeng.common.exception.BusinessException;
import com.huarenzaimeng.module.recharge.entity.Order;
import com.huarenzaimeng.module.recharge.entity.Product;
import com.huarenzaimeng.module.recharge.mapper.OrderMapper;
import com.huarenzaimeng.module.recharge.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 单元测试")
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private RiskControlService riskControlService;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderMapper, productMapper, riskControlService);
    }

    @Test
    @DisplayName("创建订单 - 正常流程")
    void createOrder_success() {
        Product product = new Product();
        product.setId(1L);
        product.setOperatorId(10L);
        product.setTopupType("AIRTIME");
        product.setUsdCost(new BigDecimal("5.00"));
        product.setBdtAmount(new BigDecimal("550"));
        product.setCnyPrice(new BigDecimal("36.50"));
        product.setStatus(1);

        when(productMapper.selectById(1L)).thenReturn(product);
        when(orderMapper.insert(any(Order.class))).thenReturn(1);

        Order order = orderService.createOrder("openid_test", "01712345678", 1L);

        assertNotNull(order);
        assertTrue(order.getOrderNo().startsWith("HM"));
        assertEquals(22, order.getOrderNo().length());
        assertEquals("openid_test", order.getOpenid());
        assertEquals("01712345678", order.getPhone());
        assertEquals(OrderStatus.PENDING_PAY.name(), order.getOrderStatus());
        assertEquals(new BigDecimal("36.50"), order.getCnyPrice());

        verify(riskControlService).checkFrequency("openid_test", "01712345678");
        verify(riskControlService).checkAmount("openid_test", new BigDecimal("36.50"));
    }

    @Test
    @DisplayName("创建订单 - 商品不存在")
    void createOrder_productNotFound() {
        when(productMapper.selectById(99L)).thenReturn(null);

        assertThrows(BusinessException.class, () ->
                orderService.createOrder("openid_test", "01712345678", 99L));
    }

    @Test
    @DisplayName("创建订单 - 商品已下架")
    void createOrder_productOffShelf() {
        Product product = new Product();
        product.setId(2L);
        product.setStatus(0);

        when(productMapper.selectById(2L)).thenReturn(product);

        assertThrows(BusinessException.class, () ->
                orderService.createOrder("openid_test", "01712345678", 2L));
    }

    @Test
    @DisplayName("创建订单 - 风控拦截")
    void createOrder_riskBlocked() {
        doThrow(new BusinessException(com.huarenzaimeng.common.result.ResultCode.RISK_FREQUENCY_LIMIT)).when(riskControlService)
                .checkFrequency(anyString(), anyString());

        assertThrows(BusinessException.class, () ->
                orderService.createOrder("openid_test", "01712345678", 1L));
    }

    @Test
    @DisplayName("状态转换 - 成功")
    void transitionStatus_success() {
        when(orderMapper.updateStatus("HM001", OrderStatus.PENDING_PAY.name(), OrderStatus.PENDING_CHARGE.name()))
                .thenReturn(1);

        boolean result = orderService.transitionStatus("HM001", OrderStatus.PENDING_PAY, OrderStatus.PENDING_CHARGE);

        assertTrue(result);
    }

    @Test
    @DisplayName("状态转换 - 并发冲突返回false")
    void transitionStatus_concurrentConflict() {
        when(orderMapper.updateStatus("HM001", OrderStatus.PENDING_PAY.name(), OrderStatus.PENDING_CHARGE.name()))
                .thenReturn(0);

        boolean result = orderService.transitionStatus("HM001", OrderStatus.PENDING_PAY, OrderStatus.PENDING_CHARGE);

        assertFalse(result);
    }

    @Test
    @DisplayName("标记已支付 - 正常流程")
    void markPaid_success() {
        Order order = new Order();
        order.setOrderNo("HM001");
        order.setOrderStatus(OrderStatus.PENDING_PAY.name());

        when(orderMapper.selectOne(any())).thenReturn(order);
        when(orderMapper.updateById(any())).thenReturn(1);
        when(orderMapper.updateStatus("HM001", OrderStatus.PENDING_PAY.name(), OrderStatus.PENDING_CHARGE.name()))
                .thenReturn(1);

        orderService.markPaid("HM001", "wx_txn_123");

        assertEquals("wx_txn_123", order.getWxTransactionId());
        assertNotNull(order.getPaidAt());
    }

    @Test
    @DisplayName("标记已支付 - 订单不存在")
    void markPaid_orderNotFound() {
        when(orderMapper.selectOne(any())).thenReturn(null);

        assertThrows(BusinessException.class, () ->
                orderService.markPaid("HM_NOT_EXIST", "wx_txn_123"));
    }

    @Test
    @DisplayName("标记充值成功")
    void markSuccess_success() {
        Order order = new Order();
        order.setOrderNo("HM001");
        order.setOrderStatus(OrderStatus.CHARGING.name());

        when(orderMapper.selectOne(any())).thenReturn(order);
        when(orderMapper.updateById(any())).thenReturn(1);
        when(orderMapper.updateStatus("HM001", OrderStatus.CHARGING.name(), OrderStatus.SUCCESS.name()))
                .thenReturn(1);

        orderService.markSuccess("HM001");

        assertNotNull(order.getChargedAt());
    }

    @Test
    @DisplayName("标记充值失败")
    void markFailed_success() {
        Order order = new Order();
        order.setOrderNo("HM001");
        order.setOrderStatus(OrderStatus.CHARGING.name());

        when(orderMapper.selectOne(any())).thenReturn(order);
        when(orderMapper.updateById(any())).thenReturn(1);
        when(orderMapper.updateStatus("HM001", OrderStatus.CHARGING.name(), OrderStatus.FAILED.name()))
                .thenReturn(1);

        orderService.markFailed("HM001", "Reloadly timeout");

        assertEquals("Reloadly timeout", order.getStatusDesc());
    }
}
