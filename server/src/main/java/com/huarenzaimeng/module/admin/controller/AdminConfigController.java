package com.huarenzaimeng.module.admin.controller;

import com.huarenzaimeng.common.result.Result;
import com.huarenzaimeng.module.admin.entity.SysConfig;
import com.huarenzaimeng.module.admin.service.SysConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/config")
public class AdminConfigController {

    private final SysConfigService sysConfigService;

    public AdminConfigController(SysConfigService sysConfigService) {
        this.sysConfigService = sysConfigService;
    }

    @GetMapping
    public Result<List<SysConfig>> list() {
        return Result.ok(sysConfigService.listAll());
    }

    @GetMapping("/{key}")
    public Result<String> getValue(@PathVariable String key) {
        String value = sysConfigService.getValue(key);
        if (value == null) {
            return Result.fail(404, "配置项不存在");
        }
        return Result.ok(value);
    }

    @PutMapping("/{key}")
    public Result<Void> updateValue(@PathVariable String key, @RequestBody ConfigUpdateRequest request) {
        sysConfigService.updateValue(key, request.value());
        return Result.ok(null);
    }

    @PostMapping("/invalidate-cache")
    public Result<Void> invalidateCache() {
        sysConfigService.invalidateCache();
        return Result.ok(null);
    }

    public record ConfigUpdateRequest(String value) {
    }
}
