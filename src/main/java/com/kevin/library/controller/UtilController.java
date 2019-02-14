package com.kevin.library.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UtilController implements ErrorController {
    /**
     * 配置404,500,401页面
     * @param request
     * @return
     */
    @RequestMapping("/error")
    public String handlerError(HttpServletRequest request){
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if(statusCode==404){
            return "404";
        }else if (statusCode==401){
            return "unanthorized";
        }
        else{
            return "500";
        }
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
