package com.huarenzaimeng.module.content.controller;

import com.huarenzaimeng.common.result.Result;
import com.huarenzaimeng.module.content.entity.Holiday;
import com.huarenzaimeng.module.content.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wx/holidays")
@RequiredArgsConstructor
public class WxHolidayController {

    private final HolidayService holidayService;

    @GetMapping
    public Result<List<Holiday>> list(@RequestParam(required = false) Integer year) {
        return Result.ok(holidayService.wxListByYear(year));
    }

    @GetMapping("/upcoming")
    public Result<List<Holiday>> upcoming() {
        return Result.ok(holidayService.wxUpcoming());
    }
}
