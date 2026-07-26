package com.huarenzaimeng.config;

import com.huarenzaimeng.common.interceptor.AdminAuthInterceptor;
import com.huarenzaimeng.common.interceptor.InternalApiInterceptor;
import com.huarenzaimeng.common.interceptor.WxAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AdminAuthInterceptor adminAuthInterceptor;
    private final InternalApiInterceptor internalApiInterceptor;
    private final WxAuthInterceptor wxAuthInterceptor;

    public WebMvcConfig(AdminAuthInterceptor adminAuthInterceptor,
                        InternalApiInterceptor internalApiInterceptor,
                        WxAuthInterceptor wxAuthInterceptor) {
        this.adminAuthInterceptor = adminAuthInterceptor;
        this.internalApiInterceptor = internalApiInterceptor;
        this.wxAuthInterceptor = wxAuthInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(wxAuthInterceptor)
                .addPathPatterns("/wx/**")
                .excludePathPatterns("/wx/pay/callback");

        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login");

        registry.addInterceptor(internalApiInterceptor)
                .addPathPatterns("/internal/**");
    }
}
