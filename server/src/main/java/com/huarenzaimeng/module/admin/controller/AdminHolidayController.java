package com.huarenzaimeng.module.admin.controller;

import com.huarenzaimeng.common.result.Result;
import com.huarenzaimeng.module.content.entity.Holiday;
import com.huarenzaimeng.module.content.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/holidays")
@RequiredArgsConstructor
public class AdminHolidayController {

    private final HolidayService holidayService;

    @GetMapping
    public Result<List<Holiday>> list(@RequestParam(required = false) Integer year) {
        return Result.ok(holidayService.listByYear(year));
    }

    @GetMapping("/{id}")
    public Result<Holiday> detail(@PathVariable Long id) {
        Holiday holiday = holidayService.getById(id);
        if (holiday == null) {
            return Result.fail(404, "节假日不存在");
        }
        return Result.ok(holiday);
    }

    @PostMapping
    public Result<Holiday> create(@RequestBody Holiday holiday) {
        if (holiday.getName() == null || holiday.getName().isBlank()) {
            return Result.fail(400, "名称不能为空");
        }
        if (holiday.getHolidayDate() == null) {
            return Result.fail(400, "日期不能为空");
        }
        return Result.ok(holidayService.create(holiday));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Holiday holiday) {
        Holiday existing = holidayService.getById(id);
        if (existing == null) {
            return Result.fail(404, "节假日不存在");
        }
        holiday.setId(id);
        holidayService.update(holiday);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        holidayService.delete(id);
        return Result.ok();
    }

    @PostMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        holidayService.confirm(id);
        return Result.ok();
    }
}
