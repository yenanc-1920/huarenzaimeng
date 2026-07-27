package com.huarenzaimeng.module.content.service;

import com.huarenzaimeng.common.constant.AuditStatus;
import com.huarenzaimeng.module.content.entity.Company;
import com.huarenzaimeng.module.content.mapper.CompanyMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompanyService 单元测试")
class CompanyServiceTest {

    @Mock
    private CompanyMapper companyMapper;

    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        companyService = new CompanyService(companyMapper);
    }

    @Test
    @DisplayName("创建企业 - 默认Logo审核为PENDING")
    void create_defaultLogoAuditStatus() {
        Company company = new Company();
        company.setName("测试企业");
        company.setCategory("FOOD");

        when(companyMapper.insert(any(Company.class))).thenReturn(1);

        Company result = companyService.create(company);

        assertEquals(AuditStatus.PENDING.name(), result.getLogoAuditStatus());
        assertEquals(0, result.getViewCount());
        assertEquals(1, result.getStatus());
        assertEquals(0, result.getSortOrder());
    }

    @Test
    @DisplayName("小程序详情 - 浏览量+1")
    void wxDetail_incrementsViewCount() {
        Company company = new Company();
        company.setId(1L);
        company.setStatus(1);
        company.setViewCount(5);

        when(companyMapper.selectById(1L)).thenReturn(company);
        when(companyMapper.updateById(any())).thenReturn(1);

        Company result = companyService.wxDetail(1L);

        assertNotNull(result);
        assertEquals(6, result.getViewCount());
    }

    @Test
    @DisplayName("小程序详情 - 已下架返回null")
    void wxDetail_unpublishedReturnsNull() {
        Company company = new Company();
        company.setId(1L);
        company.setStatus(0);

        when(companyMapper.selectById(1L)).thenReturn(company);

        Company result = companyService.wxDetail(1L);

        assertNull(result);
    }

    @Test
    @DisplayName("更新Logo审核状态")
    void updateLogoAuditStatus_success() {
        Company company = new Company();
        company.setId(1L);
        company.setLogoAuditStatus(AuditStatus.PENDING.name());

        when(companyMapper.selectById(1L)).thenReturn(company);
        when(companyMapper.updateById(any())).thenReturn(1);

        companyService.updateLogoAuditStatus(1L, AuditStatus.REJECT.name());

        assertEquals(AuditStatus.REJECT.name(), company.getLogoAuditStatus());
    }
}
