package com.huarenzaimeng.module.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huarenzaimeng.common.result.Result;
import com.huarenzaimeng.module.recharge.entity.Operator;
import com.huarenzaimeng.module.recharge.mapper.OperatorMapper;
import com.huarenzaimeng.module.recharge.service.MnpService;
import com.huarenzaimeng.module.recharge.service.RefundService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/operators")
public class AdminOperatorController {

    private final OperatorMapper operatorMapper;
    private final MnpService mnpService;
    private final RefundService refundService;

    public AdminOperatorController(OperatorMapper operatorMapper, MnpService mnpService, RefundService refundService) {
        this.operatorMapper = operatorMapper;
        this.mnpService = mnpService;
        this.refundService = refundService;
    }

    @GetMapping
    public Result<List<Operator>> list() {
        List<Operator> operators = operatorMapper.selectList(new LambdaQueryWrapper<Operator>()
                .orderByAsc(Operator::getSortOrder));
        return Result.ok(operators);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        Operator operator = operatorMapper.selectById(id);
        if (operator == null) {
            return Result.fail(2001, "运营商不存在");
        }
        operator.setStatus(request.status());
        operatorMapper.updateById(operator);
        return Result.ok(null);
    }

    @GetMapping("/system-status")
    public Result<Map<String, Object>> systemStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("mnpDegraded", mnpService.isDegraded());
        status.put("refundCircuitBroken", refundService.isCircuitBroken());
        return Result.ok(status);
    }

    public record StatusRequest(Integer status) {
    }
}
