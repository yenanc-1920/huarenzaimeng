package com.huarenzaimeng.module.content.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huarenzaimeng.common.constant.AuditStatus;
import com.huarenzaimeng.module.content.entity.News;
import com.huarenzaimeng.module.content.mapper.NewsMapper;
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
@DisplayName("NewsService 单元测试")
class NewsServiceTest {

    @Mock
    private NewsMapper newsMapper;

    private NewsService newsService;

    @BeforeEach
    void setUp() {
        newsService = new NewsService(newsMapper);
    }

    @Test
    @DisplayName("创建资讯 - 默认审核状态为PENDING")
    void create_defaultAuditStatus() {
        News news = new News();
        news.setTitle("测试资讯");

        when(newsMapper.insert(any(News.class))).thenReturn(1);

        News result = newsService.create(news);

        assertEquals(AuditStatus.PENDING.name(), result.getAuditStatus());
        assertEquals(0, result.getViewCount());
        assertEquals(1, result.getStatus());
        assertEquals(0, result.getIsTop());
    }

    @Test
    @DisplayName("发布资讯 - 设置发布时间")
    void publish_setsPublishedAt() {
        News news = new News();
        news.setId(1L);
        news.setStatus(0);

        when(newsMapper.selectById(1L)).thenReturn(news);
        when(newsMapper.updateById(any())).thenReturn(1);

        newsService.publish(1L);

        assertEquals(1, news.getStatus());
        assertNotNull(news.getPublishedAt());
    }

    @Test
    @DisplayName("下架资讯")
    void unpublish_setsStatusZero() {
        News news = new News();
        news.setId(1L);
        news.setStatus(1);

        when(newsMapper.selectById(1L)).thenReturn(news);
        when(newsMapper.updateById(any())).thenReturn(1);

        newsService.unpublish(1L);

        assertEquals(0, news.getStatus());
    }

    @Test
    @DisplayName("小程序详情 - 浏览量+1")
    void wxDetail_incrementsViewCount() {
        News news = new News();
        news.setId(1L);
        news.setStatus(1);
        news.setViewCount(10);

        when(newsMapper.selectById(1L)).thenReturn(news);
        when(newsMapper.updateById(any())).thenReturn(1);

        News result = newsService.wxDetail(1L);

        assertNotNull(result);
        assertEquals(11, result.getViewCount());
    }

    @Test
    @DisplayName("小程序详情 - 已下架返回null")
    void wxDetail_unpublishedReturnsNull() {
        News news = new News();
        news.setId(1L);
        news.setStatus(0);

        when(newsMapper.selectById(1L)).thenReturn(news);

        News result = newsService.wxDetail(1L);

        assertNull(result);
    }

    @Test
    @DisplayName("更新审核状态")
    void updateAuditStatus_success() {
        News news = new News();
        news.setId(1L);
        news.setAuditStatus(AuditStatus.PENDING.name());

        when(newsMapper.selectById(1L)).thenReturn(news);
        when(newsMapper.updateById(any())).thenReturn(1);

        newsService.updateAuditStatus(1L, AuditStatus.PASS.name());

        assertEquals(AuditStatus.PASS.name(), news.getAuditStatus());
    }
}
