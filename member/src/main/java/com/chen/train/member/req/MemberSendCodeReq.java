package com.chen.train.member.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class MemberSendCodeReq {

    /**
     *添加spring中的校验框架
     * 此注解表示不能为空
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1\\d{10}$",message = "手机号码格式错误")
    private String mobile;

    public MemberSendCodeReq(String mobile) {
        this.mobile = mobile;
    }

    public MemberSendCodeReq() {
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "MemberSendCodeReq{" +
                "mobile='" + mobile + '\'' +
                '}';
    }
}
