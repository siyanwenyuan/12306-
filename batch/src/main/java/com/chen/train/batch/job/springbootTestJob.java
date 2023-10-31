package com.chen.train.batch.job;


import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 其中自带的定时任务具有缺点
 * 1 只是适合单体应用，不适集群
 * 2 没办法随时修改定时任务的状态
 *
 *
 */
//此注解表示这个类交给spring管理
@Component
//此注解表示开启定时springboot自带的定时任务
@EnableScheduling
public class springbootTestJob {

    //此注解表示该类使用什么定时策略，使用cron表达式
    @Scheduled(cron = "0/5 * * * * ?")
    private void Test(){
        /**
         * 可以加入redis分布式锁解决集群问题
         *    只有当前的拿到锁之后，才可以执行，没有拿到锁，则不可以执行
         *
         */
        System.out.println("springboot is jobing");
    }


}
