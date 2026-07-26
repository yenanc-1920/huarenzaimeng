package com.huarenzaimeng.module.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huarenzaimeng.common.result.Result;
import com.huarenzaimeng.module.recharge.entity.Order;
import com.huarenzaimeng.module.recharge.entity.Refund;
import com.huarenzaimeng.module.recharge.mapper.OrderMapper;
import com.huarenzaimeng.module.recharge.mapper.RefundMapper;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderMapper orderMapper;
    private final RefundMapper refundMapper;

    public AdminOrderController(OrderMapper orderMapper, RefundMapper refundMapper) {
        this.orderMapper = orderMapper;
        this.refundMapper = refundMapper;
    }

    @GetMapping
    public Result<Page<Order>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String openid,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String statusGroup,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (orderNo != null && !orderNo.isBlank()) {
            wrapper.eq(Order::getOrderNo, orderNo);
        }
        if (phone != null && !phone.isBlank()) {
            wrapper.eq(Order::getPhone, phone);
        }
        if (openid != null && !openid.isBlank()) {
            wrapper.eq(Order::getOpenid, openid);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(Order::getOrderStatus, status);
        }
        if (statusGroup != null && !statusGroup.isBlank()) {
            List<String> statuses = resolveStatusGroup(statusGroup);
            wrapper.in(Order::getOrderStatus, statuses);
        }
        if (startDate != null && !startDate.isBlank()) {
            wrapper.ge(Order::getCreatedAt, startDate + " 00:00:00");
        }
        if (endDate != null && !endDate.isBlank()) {
            wrapper.le(Order::getCreatedAt, endDate + " 23:59:59");
        }

        wrapper.orderByDesc(Order::getCreatedAt);

        Page<Order> result = orderMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.ok(result);
    }

    @GetMapping("/{orderNo}")
    public Result<Map<String, Object>> detail(@PathVariable String orderNo) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo));
        if (order == null) {
            return Result.fail(3001, "订单不存在");
        }

        List<Refund> refunds = refundMapper.selectList(new LambdaQueryWrapper<Refund>()
                .eq(Refund::getOrderNo, orderNo)
                .orderByDesc(Refund::getCreatedAt));

        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("refunds", refunds);
        data.put("timeline", buildTimeline(order));
        return Result.ok(data);
    }

    @GetMapping("/exception")
    public Result<Page<Order>> exceptionList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String statusDesc) {

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Order::getOrderStatus, List.of("EXCEPTION", "FAILED"));
        if (statusDesc != null && !statusDesc.isBlank()) {
            wrapper.like(Order::getStatusDesc, statusDesc);
        }
        wrapper.orderByDesc(Order::getCreatedAt);

        Page<Order> result = orderMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.ok(result);
    }

    @GetMapping("/exception/count")
    public Result<Map<String, Long>> exceptionCount() {
        Long exceptionCount = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderStatus, "EXCEPTION"));
        Long failedCount = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderStatus, "FAILED"));

        Map<String, Long> data = new HashMap<>();
        data.put("exception", exceptionCount);
        data.put("failed", failedCount);
        data.put("total", exceptionCount + failedCount);
        return Result.ok(data);
    }

    private List<String> resolveStatusGroup(String group) {
        return switch (group) {
            case "PENDING" -> List.of("PENDING_PAY", "PRICE_CHECKING", "MNP_CHECKING");
            case "PROCESSING" -> List.of("PENDING_CHARGE", "CHARGING");
            case "COMPLETED" -> List.of("SUCCESS", "REFUNDED");
            case "ABNORMAL" -> List.of("FAILED", "EXCEPTION", "CANCELLED");
            default -> List.of(group);
        };
    }

    private List<Map<String, Object>> buildTimeline(Order order) {
        List<Map<String, Object>> timeline = new java.util.ArrayList<>();
        addTimelineNode(timeline, "创建订单", order.getCreatedAt().toString(), "CREATED", null);

        if (order.getPaidAt() != null) {
            addTimelineNode(timeline, "支付成功", order.getPaidAt().toString(), "PAID", null);
        }
        if (order.getChargedAt() != null) {
            addTimelineNode(timeline, "充值完成", order.getChargedAt().toString(), "CHARGED", null);
        }
        if ("FAILED".equals(order.getOrderStatus()) || "EXCEPTION".equals(order.getOrderStatus())) {
            addTimelineNode(timeline, order.getStatusDesc() != null ? order.getStatusDesc() : "异常",
                    order.getUpdatedAt().toString(), order.getOrderStatus(), "error");
        }
        return timeline;
    }

    private void addTimelineNode(List<Map<String, Object>> timeline, String title, String time, String status, String type) {
        Map<String, Object> node = new HashMap<>();
        node.put("title", title);
        node.put("time", time);
        node.put("status", status);
        node.put("type", type);
        timeline.add(node);
    }
}
