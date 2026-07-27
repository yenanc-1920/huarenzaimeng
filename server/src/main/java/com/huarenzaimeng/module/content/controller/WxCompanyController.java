package com.huarenzaimeng.module.content.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huarenzaimeng.common.result.Result;
import com.huarenzaimeng.module.content.entity.Company;
import com.huarenzaimeng.module.content.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wx/companies")
@RequiredArgsConstructor
public class WxCompanyController {

    private final CompanyService companyService;

    @GetMapping
    public Result<Page<Company>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        return Result.ok(companyService.wxList(page, size, category, keyword));
    }

    @GetMapping("/{id}")
    public Result<Company> detail(@PathVariable Long id) {
        Company company = companyService.wxDetail(id);
        if (company == null) {
            return Result.fail(404, "企业不存在或已下架");
        }
        return Result.ok(company);
    }
}
