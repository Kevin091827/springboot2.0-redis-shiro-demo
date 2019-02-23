package com.kevin.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    /**
     * 入口controller  跳转至首页
     * @return
     */
    @RequestMapping("/toIndex")
    public String toIndex(){
        return "index";
    }
}
