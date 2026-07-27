package com.huarenzaimeng.module.admin.controller;

import com.huarenzaimeng.common.result.Result;
import com.huarenzaimeng.module.admin.service.AdminAuthService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> data = adminAuthService.login(request.username(), request.password());
        return Result.ok(data);
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }
}
