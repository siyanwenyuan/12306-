package com.chen.train.common.config;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.chen")
public class CommonApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommonApplication.class,args);
    }
}
