package com.chen.train.common.context;

import com.chen.train.common.resp.MemberLoginResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginMemberContext {
    private static final Logger LOG = LoggerFactory.getLogger(LoginMemberContext.class);


    //创建一个本地线程
    private static ThreadLocal<MemberLoginResp> member = new ThreadLocal<>();

    //得到登录的member
    public static MemberLoginResp getMember() {
        return member.get();
    }

    //设置到本地线程变量中
    public static void setMember(MemberLoginResp member) {
        LoginMemberContext.member.set(member);
    }

    public static Long getId() {
        try {
            return member.get().getId();
        } catch (Exception e) {
            LOG.error("获取登录会员信息异常", e);
            throw e;
        }
    }

}
