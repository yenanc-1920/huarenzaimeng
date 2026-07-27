package com.huarenzaimeng.module.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huarenzaimeng.module.content.entity.Company;
import com.huarenzaimeng.module.content.entity.Holiday;
import com.huarenzaimeng.module.content.entity.News;
import com.huarenzaimeng.module.content.mapper.CompanyMapper;
import com.huarenzaimeng.module.content.mapper.HolidayMapper;
import com.huarenzaimeng.module.content.mapper.NewsMapper;
import com.huarenzaimeng.module.recharge.entity.Order;
import com.huarenzaimeng.module.recharge.mapper.OrderMapper;
import com.huarenzaimeng.module.user.mapper.UserMapper;
import com.huarenzaimeng.common.constant.HolidayConfirmStatus;
import com.huarenzaimeng.common.constant.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final NewsMapper newsMapper;
    private final CompanyMapper companyMapper;
    private final HolidayMapper holidayMapper;
    private final SysConfigService sysConfigService;

    public Map<String, Object> getOverview() {
        Map<String, Object> data = new HashMap<>();

        LocalDateTime todayStart = LocalDate.now(ZoneOffset.UTC).atStartOfDay();

        // 用户统计
        long totalUsers = userMapper.selectCount(null);
        long todayNewUsers = userMapper.selectCount(new LambdaQueryWrapper<com.huarenzaimeng.module.user.entity.User>()
                .ge(com.huarenzaimeng.module.user.entity.User::getCreatedAt, todayStart));
        data.put("totalUsers", totalUsers);
        data.put("todayNewUsers", todayNewUsers);

        // 交易统计
        long totalOrders = orderMapper.selectCount(null);
        long todayOrders = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .ge(Order::getCreatedAt, todayStart));
        long successOrders = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderStatus, OrderStatus.SUCCESS.name()));
        long exceptionOrders = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .in(Order::getOrderStatus, OrderStatus.EXCEPTION.name(), OrderStatus.FAILED.name()));
        data.put("totalOrders", totalOrders);
        data.put("todayOrders", todayOrders);
        data.put("successOrders", successOrders);
        data.put("exceptionOrders", exceptionOrders);

        // 内容统计
        long totalNews = newsMapper.selectCount(new LambdaQueryWrapper<News>().eq(News::getStatus, 1));
        long totalCompanies = companyMapper.selectCount(new LambdaQueryWrapper<Company>().eq(Company::getStatus, 1));
        data.put("totalNews", totalNews);
        data.put("totalCompanies", totalCompanies);

        // 待观月节假日
        long pendingMoonCount = holidayMapper.selectCount(new LambdaQueryWrapper<Holiday>()
                .eq(Holiday::getConfirmStatus, HolidayConfirmStatus.PENDING_MOON.name())
                .ge(Holiday::getHolidayDate, LocalDate.now(ZoneOffset.UTC)));
        data.put("pendingMoonHolidays", pendingMoonCount);

        return data;
    }
}
