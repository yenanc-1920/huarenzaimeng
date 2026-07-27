package com.huarenzaimeng.module.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huarenzaimeng.common.result.Result;
import com.huarenzaimeng.infra.adapter.wxsecurity.WxSecurityAdapter;
import com.huarenzaimeng.module.content.entity.Company;
import com.huarenzaimeng.module.content.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/companies")
@RequiredArgsConstructor
public class AdminCompanyController {

    private final CompanyService companyService;
    private final WxSecurityAdapter wxSecurityAdapter;

    @GetMapping
    public Result<Page<Company>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        return Result.ok(companyService.adminList(page, size, keyword, category, status));
    }

    @GetMapping("/{id}")
    public Result<Company> detail(@PathVariable Long id) {
        Company company = companyService.getById(id);
        if (company == null) {
            return Result.fail(404, "企业不存在");
        }
        return Result.ok(company);
    }

    @PostMapping
    public Result<Company> create(@RequestBody Company company) {
        if (company.getName() == null || company.getName().isBlank()) {
            return Result.fail(400, "企业名称不能为空");
        }
        if (company.getCategory() == null || company.getCategory().isBlank()) {
            return Result.fail(400, "分类不能为空");
        }
        boolean textSafe = wxSecurityAdapter.msgSecCheck(company.getName() + " " + (company.getDescription() != null ? company.getDescription() : ""), null);
        if (!textSafe) {
            return Result.fail(400, "内容包含违规信息，请修改后重试");
        }
        return Result.ok(companyService.create(company));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Company company) {
        Company existing = companyService.getById(id);
        if (existing == null) {
            return Result.fail(404, "企业不存在");
        }
        company.setId(id);
        if (company.getName() != null) {
            boolean textSafe = wxSecurityAdapter.msgSecCheck(company.getName(), null);
            if (!textSafe) {
                return Result.fail(400, "内容包含违规信息，请修改后重试");
            }
        }
        companyService.update(company);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        companyService.delete(id);
        return Result.ok();
    }
}
