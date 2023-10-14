package com.chen.train.common.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.chen.train.common.context.LoginMemberContext;
import com.chen.train.common.context.LoginMemberContext;
import com.chen.train.common.resp.MemberLoginResp;
import com.chen.train.common.resp.MemberLoginResp;
import com.chen.train.common.util.JwtUtil;
import com.chen.train.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 拦截器：Spring框架特有的，常用于登录校验，权限校验，请求日志打印
 */
@Component
public class MemberInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(MemberInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOG.info("MemberInterceptor开始");
        //获取header的token参数
        String token = request.getHeader("token");
        if (StrUtil.isNotBlank(token)) {
            LOG.info("获取会员登录token：{}", token);
            //解析token，得到其中的登录信息
            JSONObject loginMember = JwtUtil.getJSONObject(token);
            LOG.info("当前登录会员：{}", loginMember);
            //将得到的当前会员信息转换成memberLoginResp对象
            MemberLoginResp member = JSONUtil.toBean(loginMember, MemberLoginResp.class);
            //最后设置进入本地线程变量中
            LoginMemberContext.setMember(member);
        }
        LOG.info("MemberInterceptor结束");
        return true;
    }

}
