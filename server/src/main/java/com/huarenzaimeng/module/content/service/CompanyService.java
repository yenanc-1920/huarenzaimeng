package com.huarenzaimeng.module.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huarenzaimeng.common.constant.AuditStatus;
import com.huarenzaimeng.module.content.entity.Company;
import com.huarenzaimeng.module.content.mapper.CompanyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyMapper companyMapper;

    public Page<Company> adminList(int page, int size, String keyword, String category, Integer status) {
        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Company::getName, keyword);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(Company::getCategory, category);
        }
        if (status != null) {
            wrapper.eq(Company::getStatus, status);
        }
        wrapper.orderByAsc(Company::getSortOrder).orderByDesc(Company::getCreatedAt);
        return companyMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public Company getById(Long id) {
        return companyMapper.selectById(id);
    }

    public Company create(Company company) {
        company.setLogoAuditStatus(AuditStatus.PENDING.name());
        company.setViewCount(0);
        if (company.getStatus() == null) {
            company.setStatus(1);
        }
        if (company.getSortOrder() == null) {
            company.setSortOrder(0);
        }
        companyMapper.insert(company);
        return company;
    }

    public void update(Company company) {
        companyMapper.updateById(company);
    }

    public void delete(Long id) {
        companyMapper.deleteById(id);
    }

    public void updateLogoAuditStatus(Long id, String auditStatus) {
        Company company = companyMapper.selectById(id);
        if (company != null) {
            company.setLogoAuditStatus(auditStatus);
            companyMapper.updateById(company);
        }
    }

    // ===== 小程序端 =====

    public Page<Company> wxList(int page, int size, String category, String keyword) {
        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Company::getStatus, 1);
        if (StringUtils.hasText(category)) {
            wrapper.eq(Company::getCategory, category);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Company::getName, keyword)
                    .or().like(Company::getDescription, keyword));
        }
        wrapper.orderByAsc(Company::getSortOrder).orderByDesc(Company::getCreatedAt);
        return companyMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public Company wxDetail(Long id) {
        Company company = companyMapper.selectById(id);
        if (company != null && company.getStatus() == 1) {
            company.setViewCount(company.getViewCount() + 1);
            companyMapper.updateById(company);
            return company;
        }
        return null;
    }
}
