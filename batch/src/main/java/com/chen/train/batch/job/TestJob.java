package com.chen.train.batch.job;

import com.chen.train.batch.feign.BusinessFeign;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;


//关闭并发执行
@DisallowConcurrentExecution
public class TestJob implements Job {


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {


/**
         * 此如果不关闭任务的并发执行，会出现问题
         * 因此需要添加注解，关闭任务的并发执行
         */

        System.out.println("开始");




        System.out.println("结束");


    }
}
