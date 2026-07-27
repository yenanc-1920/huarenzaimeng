package com.huarenzaimeng.module.content.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huarenzaimeng.common.result.Result;
import com.huarenzaimeng.module.content.entity.News;
import com.huarenzaimeng.module.content.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wx/news")
@RequiredArgsConstructor
public class WxNewsController {

    private final NewsService newsService;

    @GetMapping
    public Result<Page<News>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(newsService.wxList(page, size));
    }

    @GetMapping("/{id}")
    public Result<News> detail(@PathVariable Long id) {
        News news = newsService.wxDetail(id);
        if (news == null) {
            return Result.fail(404, "资讯不存在或已下架");
        }
        return Result.ok(news);
    }
}
