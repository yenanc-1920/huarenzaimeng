package com.huarenzaimeng.module.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huarenzaimeng.common.result.Result;
import com.huarenzaimeng.infra.adapter.wxsecurity.WxSecurityAdapter;
import com.huarenzaimeng.module.content.entity.News;
import com.huarenzaimeng.module.content.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/news")
@RequiredArgsConstructor
public class AdminNewsController {

    private final NewsService newsService;
    private final WxSecurityAdapter wxSecurityAdapter;

    @GetMapping
    public Result<Page<News>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String auditStatus) {
        return Result.ok(newsService.adminList(page, size, keyword, status, auditStatus));
    }

    @GetMapping("/{id}")
    public Result<News> detail(@PathVariable Long id) {
        News news = newsService.getById(id);
        if (news == null) {
            return Result.fail(404, "资讯不存在");
        }
        return Result.ok(news);
    }

    @PostMapping
    public Result<News> create(@RequestBody News news) {
        if (news.getTitle() == null || news.getTitle().isBlank()) {
            return Result.fail(400, "标题不能为空");
        }
        boolean textSafe = wxSecurityAdapter.msgSecCheck(news.getTitle() + " " + (news.getSummary() != null ? news.getSummary() : ""), null);
        if (!textSafe) {
            return Result.fail(400, "内容包含违规信息，请修改后重试");
        }
        return Result.ok(newsService.create(news));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody News news) {
        News existing = newsService.getById(id);
        if (existing == null) {
            return Result.fail(404, "资讯不存在");
        }
        news.setId(id);
        if (news.getTitle() != null) {
            boolean textSafe = wxSecurityAdapter.msgSecCheck(news.getTitle(), null);
            if (!textSafe) {
                return Result.fail(400, "内容包含违规信息，请修改后重试");
            }
        }
        newsService.update(news);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        newsService.delete(id);
        return Result.ok();
    }

    @PostMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        newsService.publish(id);
        return Result.ok();
    }

    @PostMapping("/{id}/unpublish")
    public Result<Void> unpublish(@PathVariable Long id) {
        newsService.unpublish(id);
        return Result.ok();
    }
}
