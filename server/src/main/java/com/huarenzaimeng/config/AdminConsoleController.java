package com.huarenzaimeng.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminConsoleController {

    @GetMapping({"/console", "/console/"})
    public String adminConsole() {
        return "forward:/console/index.html";
    }
}