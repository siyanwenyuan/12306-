package com.chen.train.batch.controller;


import cn.hutool.log.Log;
import com.chen.train.batch.feign.businessFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class TestController {


    @Autowired
    private com.chen.train.batch.feign.businessFeign businessFeign;

    @GetMapping("/hello")
    public String hello(){

        String hello = businessFeign.hello();
        return "hello";

    }
}

