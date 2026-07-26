package com.huarenzaimeng.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class InternalApiInterceptor implements HandlerInterceptor {

    @Value("${app.internal.secret}")
    private String internalSecret;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String secret = request.getHeader("X-Internal-Secret");
        if (internalSecret.equals(secret)) {
            return true;
        }

        response.setStatus(403);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":403,\"message\":\"禁止访问\",\"data\":null}");
        return false;
    }
}
