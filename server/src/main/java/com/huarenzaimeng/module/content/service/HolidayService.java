package com.huarenzaimeng.module.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huarenzaimeng.common.constant.HolidayConfirmStatus;
import com.huarenzaimeng.module.content.entity.Holiday;
import com.huarenzaimeng.module.content.mapper.HolidayMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayMapper holidayMapper;

    public List<Holiday> listByYear(Integer year) {
        LambdaQueryWrapper<Holiday> wrapper = new LambdaQueryWrapper<>();
        if (year != null) {
            wrapper.eq(Holiday::getYear, year);
        }
        wrapper.orderByAsc(Holiday::getHolidayDate);
        return holidayMapper.selectList(wrapper);
    }

    public Holiday getById(Long id) {
        return holidayMapper.selectById(id);
    }

    public Holiday create(Holiday holiday) {
        if (holiday.getConfirmStatus() == null) {
            holiday.setConfirmStatus(HolidayConfirmStatus.CONFIRMED.name());
        }
        if (holiday.getYear() == null && holiday.getHolidayDate() != null) {
            holiday.setYear(holiday.getHolidayDate().getYear());
        }
        holidayMapper.insert(holiday);
        return holiday;
    }

    public void update(Holiday holiday) {
        if (holiday.getHolidayDate() != null) {
            holiday.setYear(holiday.getHolidayDate().getYear());
        }
        holidayMapper.updateById(holiday);
    }

    public void delete(Long id) {
        holidayMapper.deleteById(id);
    }

    public void confirm(Long id) {
        Holiday holiday = holidayMapper.selectById(id);
        if (holiday != null) {
            holiday.setConfirmStatus(HolidayConfirmStatus.CONFIRMED.name());
            holidayMapper.updateById(holiday);
        }
    }

    // ===== 小程序端 =====

    public List<Holiday> wxUpcoming() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LambdaQueryWrapper<Holiday> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(Holiday::getHolidayDate, today)
                .orderByAsc(Holiday::getHolidayDate)
                .last("LIMIT 5");
        return holidayMapper.selectList(wrapper);
    }

    public List<Holiday> wxListByYear(Integer year) {
        if (year == null) {
            year = LocalDate.now(ZoneOffset.UTC).getYear();
        }
        return listByYear(year);
    }

    public List<Holiday> getPendingMoonHolidays() {
        LambdaQueryWrapper<Holiday> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Holiday::getConfirmStatus, HolidayConfirmStatus.PENDING_MOON.name())
                .ge(Holiday::getHolidayDate, LocalDate.now(ZoneOffset.UTC))
                .orderByAsc(Holiday::getHolidayDate);
        return holidayMapper.selectList(wrapper);
    }
}
