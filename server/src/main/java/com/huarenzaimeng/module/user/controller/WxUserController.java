package com.huarenzaimeng.module.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huarenzaimeng.common.result.Result;
import com.huarenzaimeng.module.user.entity.User;
import com.huarenzaimeng.module.user.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/wx/user")
public class WxUserController {

    private final UserMapper userMapper;

    public WxUserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public Result<User> login(HttpServletRequest request) {
        return Result.ok(currentUser(request));
    }

    @GetMapping("/me")
    public Result<User> profile(HttpServletRequest request) {
        return Result.ok(currentUser(request));
    }

    @PutMapping("/me")
    public Result<User> updateProfile(HttpServletRequest request,
                                      @Valid @RequestBody UpdateProfileRequest body) {
        User user = currentUser(request);
        user.setNickname(body.nickname());
        user.setAvatarUrl(body.avatarUrl());
        if (body.nickname() != null || body.avatarUrl() != null) {
            user.setUserType("MEMBER");
        }
        userMapper.updateById(user);
        return Result.ok(user);
    }

    private User currentUser(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId != null) {
            User user = userMapper.selectById(Long.valueOf(userId.toString()));
            if (user != null) {
                return user;
            }
        }
        String openid = request.getHeader("x-wx-openid");
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));
    }

    public record UpdateProfileRequest(
            @Size(max = 64, message = "昵称不能超过64个字符") String nickname,
            @Size(max = 512, message = "头像地址不能超过512个字符") String avatarUrl) {
    }
}