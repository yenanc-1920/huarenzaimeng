package com.huarenzaimeng.module.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huarenzaimeng.common.result.Result;
import com.huarenzaimeng.module.recharge.entity.Refund;
import com.huarenzaimeng.module.recharge.mapper.RefundMapper;
import com.huarenzaimeng.module.recharge.service.RefundService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/refunds")
public class AdminRefundController {

    private final RefundMapper refundMapper;
    private final RefundService refundService;

    public AdminRefundController(RefundMapper refundMapper, RefundService refundService) {
        this.refundMapper = refundMapper;
        this.refundService = refundService;
    }

    @GetMapping
    public Result<Page<Refund>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String refundStatus,
            @RequestParam(required = false) String refundType) {

        LambdaQueryWrapper<Refund> wrapper = new LambdaQueryWrapper<>();
        if (orderNo != null && !orderNo.isBlank()) {
            wrapper.eq(Refund::getOrderNo, orderNo);
        }
        if (refundStatus != null && !refundStatus.isBlank()) {
            wrapper.eq(Refund::getRefundStatus, refundStatus);
        }
        if (refundType != null && !refundType.isBlank()) {
            wrapper.eq(Refund::getRefundType, refundType);
        }
        wrapper.orderByDesc(Refund::getCreatedAt);

        Page<Refund> result = refundMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.ok(result);
    }

    @PostMapping("/manual")
    public Result<Refund> manualRefund(@RequestBody ManualRefundRequest request, HttpServletRequest httpRequest) {
        Long adminId = (Long) httpRequest.getAttribute("adminId");
        Refund refund = refundService.manualRefund(request.orderNo(), adminId, request.reason());
        if (refund == null) {
            return Result.fail(4002, "退款失败，请检查订单状态");
        }
        return Result.ok(refund);
    }

    @PostMapping("/pre-funded")
    public Result<Refund> preFundedRefund(@RequestBody ManualRefundRequest request) {
        Refund refund = refundService.preFundedRefund(request.orderNo(), request.reason());
        if (refund == null) {
            return Result.fail(4002, "垫资退款失败");
        }
        return Result.ok(refund);
    }

    @GetMapping("/circuit-breaker")
    public Result<Map<String, Object>> circuitBreakerStatus() {
        Map<String, Object> data = new HashMap<>();
        data.put("broken", refundService.isCircuitBroken());
        return Result.ok(data);
    }

    @PostMapping("/circuit-breaker/reset")
    public Result<Void> resetCircuitBreaker() {
        refundService.resetCircuitBreaker();
        return Result.ok(null);
    }

    public record ManualRefundRequest(String orderNo, String reason) {
    }
}
