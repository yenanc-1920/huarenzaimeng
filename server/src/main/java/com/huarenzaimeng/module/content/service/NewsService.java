package com.huarenzaimeng.module.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huarenzaimeng.common.constant.AuditStatus;
import com.huarenzaimeng.module.content.entity.News;
import com.huarenzaimeng.module.content.mapper.NewsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsMapper newsMapper;

    public Page<News> adminList(int page, int size, String keyword, Integer status, String auditStatus) {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(News::getTitle, keyword);
        }
        if (status != null) {
            wrapper.eq(News::getStatus, status);
        }
        if (StringUtils.hasText(auditStatus)) {
            wrapper.eq(News::getAuditStatus, auditStatus);
        }
        wrapper.orderByDesc(News::getIsTop).orderByDesc(News::getCreatedAt);
        return newsMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public News getById(Long id) {
        return newsMapper.selectById(id);
    }

    public News create(News news) {
        news.setAuditStatus(AuditStatus.PENDING.name());
        news.setViewCount(0);
        if (news.getStatus() == null) {
            news.setStatus(1);
        }
        if (news.getIsTop() == null) {
            news.setIsTop(0);
        }
        newsMapper.insert(news);
        return news;
    }

    public void update(News news) {
        newsMapper.updateById(news);
    }

    public void delete(Long id) {
        newsMapper.deleteById(id);
    }

    public void publish(Long id) {
        News news = newsMapper.selectById(id);
        if (news != null) {
            news.setStatus(1);
            news.setPublishedAt(LocalDateTime.now(ZoneOffset.UTC));
            newsMapper.updateById(news);
        }
    }

    public void unpublish(Long id) {
        News news = newsMapper.selectById(id);
        if (news != null) {
            news.setStatus(0);
            newsMapper.updateById(news);
        }
    }

    public void updateAuditStatus(Long id, String auditStatus) {
        News news = newsMapper.selectById(id);
        if (news != null) {
            news.setAuditStatus(auditStatus);
            newsMapper.updateById(news);
        }
    }

    // ===== 小程序端 =====

    public Page<News> wxList(int page, int size) {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(News::getStatus, 1)
                .eq(News::getAuditStatus, AuditStatus.PASS.name())
                .orderByDesc(News::getIsTop)
                .orderByDesc(News::getPublishedAt);
        return newsMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public News wxDetail(Long id) {
        News news = newsMapper.selectById(id);
        if (news != null && news.getStatus() == 1) {
            news.setViewCount(news.getViewCount() + 1);
            newsMapper.updateById(news);
            return news;
        }
        return null;
    }
}
