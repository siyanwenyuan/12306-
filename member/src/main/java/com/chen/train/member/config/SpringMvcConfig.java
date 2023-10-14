package com.chen.train.member.config;

import com.chen.train.common.interceptor.LogInterceptor;
import com.chen.train.common.interceptor.MemberInterceptor;
import com.chen.train.common.interceptor.MemberInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 此处的拦截，为的是方便将token中的登录信息存入本地线程变量中
 * 由于并不是所有的模块都需要该信息，所以对于拦截器的配置类只需要在需要的模块中编写
 */
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {



    @Resource
    LogInterceptor logInterceptor;

   @Resource
   MemberInterceptor memberInterceptor;


    /**
     * 添加拦截器
     * @param registry
     */
   @Override
   public void addInterceptors(InterceptorRegistry registry) {

       registry.addInterceptor(logInterceptor);

       // 路径不要包含context-path
       //增加这个拦截器
       registry.addInterceptor(memberInterceptor)
                //拦截所有路径
               .addPathPatterns("/**")
               //排除这些路径不需要拦截
               .excludePathPatterns(
                       "/hello",
                       "/member/send-code",
                       "/member/login"
               );
   }
}
