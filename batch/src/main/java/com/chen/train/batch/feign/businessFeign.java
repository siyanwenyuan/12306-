package com.chen.train.batch.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="business",url = "http://127.0.0.1:8002/business")
public interface businessFeign {

@GetMapping("hello")
    String hello();


}
