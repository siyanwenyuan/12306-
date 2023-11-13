package com.chen.train.member.controller;


import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
@RefreshScope//此注解表示nacos中的配置文件修改的时候，可以及时刷新.可以做到实时刷新

public class TestController {


    @Value("${test.nacos}")
    private String nacos;

    @GetMapping("/test")
    public String test(){


        return nacos;

    }
}
