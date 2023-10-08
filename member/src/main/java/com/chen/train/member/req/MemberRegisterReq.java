package com.chen.train.member.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MemberRegisterReq {


    /**
     *添加spring中的校验框架
     * 此注解表示不能为空
     */
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    public MemberRegisterReq(String mobile) {
        this.mobile = mobile;
    }

    public MemberRegisterReq() {
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "MemberRegisterReq{" +
                "mobile='" + mobile + '\'' +
                '}';
    }
}
