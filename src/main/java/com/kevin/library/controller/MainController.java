package com.kevin.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    /**
     * 入口controller
     * @return
     */
    //跳转至首页
    @RequestMapping("/toIndex")
    public String toIndex(){
        return "index";
    }
}
