package com.huarenzaimeng.module.content.service;

import com.huarenzaimeng.common.constant.HolidayConfirmStatus;
import com.huarenzaimeng.module.content.entity.Holiday;
import com.huarenzaimeng.module.content.mapper.HolidayMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HolidayService 单元测试")
class HolidayServiceTest {

    @Mock
    private HolidayMapper holidayMapper;

    private HolidayService holidayService;

    @BeforeEach
    void setUp() {
        holidayService = new HolidayService(holidayMapper);
    }

    @Test
    @DisplayName("创建节假日 - 默认确认状态为CONFIRMED")
    void create_defaultConfirmStatus() {
        Holiday holiday = new Holiday();
        holiday.setName("开斋节");
        holiday.setHolidayDate(LocalDate.of(2026, 3, 20));

        when(holidayMapper.insert(any(Holiday.class))).thenReturn(1);

        Holiday result = holidayService.create(holiday);

        assertEquals(HolidayConfirmStatus.CONFIRMED.name(), result.getConfirmStatus());
        assertEquals(2026, result.getYear());
    }

    @Test
    @DisplayName("创建节假日 - 自动从日期推导年份")
    void create_infersYearFromDate() {
        Holiday holiday = new Holiday();
        holiday.setName("古尔邦节");
        holiday.setHolidayDate(LocalDate.of(2026, 5, 27));

        when(holidayMapper.insert(any(Holiday.class))).thenReturn(1);

        Holiday result = holidayService.create(holiday);

        assertEquals(2026, result.getYear());
    }

    @Test
    @DisplayName("确认节假日 - 待观月变为已确认")
    void confirm_pendingMoonToConfirmed() {
        Holiday holiday = new Holiday();
        holiday.setId(1L);
        holiday.setConfirmStatus(HolidayConfirmStatus.PENDING_MOON.name());

        when(holidayMapper.selectById(1L)).thenReturn(holiday);
        when(holidayMapper.updateById(any())).thenReturn(1);

        holidayService.confirm(1L);

        assertEquals(HolidayConfirmStatus.CONFIRMED.name(), holiday.getConfirmStatus());
    }

    @Test
    @DisplayName("更新节假日 - 日期变更时同步更新年份")
    void update_syncsYear() {
        Holiday holiday = new Holiday();
        holiday.setId(1L);
        holiday.setHolidayDate(LocalDate.of(2027, 1, 1));
        holiday.setYear(2026);

        when(holidayMapper.updateById(any())).thenReturn(1);

        holidayService.update(holiday);

        assertEquals(2027, holiday.getYear());
    }
}
