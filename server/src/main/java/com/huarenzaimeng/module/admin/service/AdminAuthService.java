package com.huarenzaimeng.module.admin.service;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huarenzaimeng.common.exception.BusinessException;
import com.huarenzaimeng.common.result.ResultCode;
import com.huarenzaimeng.module.admin.entity.AdminUser;
import com.huarenzaimeng.module.admin.mapper.AdminUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AdminAuthService {

    private final AdminUserMapper adminUserMapper;
    private final JwtService jwtService;

    public AdminAuthService(AdminUserMapper adminUserMapper, JwtService jwtService) {
        this.adminUserMapper = adminUserMapper;
        this.jwtService = jwtService;
    }

    public Map<String, Object> login(String username, String password) {
        AdminUser admin = adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getUsername, username));

        if (admin == null || !BCrypt.checkpw(password, admin.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }

        if (admin.getStatus() != 1) {
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
        }

        admin.setLastLoginAt(LocalDateTime.now(ZoneOffset.UTC));
        adminUserMapper.updateById(admin);

        String token = jwtService.generateToken(admin.getId(), admin.getUsername(), admin.getRole());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("adminId", admin.getId());
        result.put("username", admin.getUsername());
        result.put("role", admin.getRole());

        log.info("Admin login success: username={}", username);
        return result;
    }
}
