package com.chen.train.business.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Redis")
public class RedisController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/setRedis")
    public String setRedis(){
        redisTemplate.opsForValue().set("chenwan",200409);
        return "success";
    }

    @GetMapping("/getRedis")
    public String getRedis(){
        Object chenwan = redisTemplate.opsForValue().get("chenwan");
        String s = String.valueOf(chenwan);
        return s;


    }
}
