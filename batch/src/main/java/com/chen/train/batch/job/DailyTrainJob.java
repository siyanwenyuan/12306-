package com.chen.train.batch.job;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.chen.train.batch.feign.BusinessFeign;
import com.chen.train.batch.utils.SpringUtil;
import com.chen.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

@DisallowConcurrentExecution
@Component
public class DailyTrainJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainJob.class);

   /* @Resource
    BusinessFeign businessFeign;*/

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        // 增加日志流水号
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        LOG.info("生成15天后的车次数据开始");
        /**
         * 通过spring上下文的方式获取bean
         * 此处使用注解会出现bean注入失败的问题
         */
        BusinessFeign businessFeign =  SpringUtil.getBean(BusinessFeign.class);
   /*     businessFeign.hello();*/

     /*   String hello = bean.hello();
        System.out.println(hello);*/
        Date date = new Date();
        DateTime dateTime = DateUtil.offsetDay(date, 15);
        Date offsetDate = dateTime.toJdkDate();
        CommonResp<Object> commonResp = businessFeign.genDaily(offsetDate);
        LOG.info("生成15天后的车次数据结束，结果：{}", commonResp);


    }
}
