package com.chen.train.batch.config;


import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan("com.chen")
@MapperScan("com.chen.train.batch.mapper")
@MapperScan("com.chen.train")
//@EnableFeignClients("com.chen.train.batch.feign")//开启feign组件，加上调用的接口的路径
@EnableFeignClients
public class BatchApplication {

    private static final Logger LOG = LoggerFactory.getLogger(BatchApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BatchApplication.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("启动成功！！");
        LOG.info("测试地址: \thttp://127.0.0.1:{}{}", env.getProperty("server.port"), env.getProperty("server.servlet.context-path"));

    }


}
