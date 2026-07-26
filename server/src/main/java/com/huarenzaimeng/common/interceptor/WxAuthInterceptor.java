package com.huarenzaimeng.common.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huarenzaimeng.module.user.entity.User;
import com.huarenzaimeng.module.user.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class WxAuthInterceptor implements HandlerInterceptor {

    private final UserMapper userMapper;

    public WxAuthInterceptor(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String openid = request.getHeader("x-wx-openid");
        if (openid == null || openid.isBlank()) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未授权\",\"data\":null}");
            return false;
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getOpenid, openid));

        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setUserType("VISITOR");
            user.setLastLoginAt(LocalDateTime.now(ZoneOffset.UTC));
            userMapper.insert(user);
        } else {
            user.setLastLoginAt(LocalDateTime.now(ZoneOffset.UTC));
            userMapper.updateById(user);
        }

        request.setAttribute("openid", openid);
        request.setAttribute("userId", user.getId());
        request.setAttribute("userType", user.getUserType());
        return true;
    }
}
