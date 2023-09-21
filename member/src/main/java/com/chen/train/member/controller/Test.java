package com.chen.train.member.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class Test{

    @GetMapping("/user")
    public void test(){
        System.out.println("hello world");

    }

}
