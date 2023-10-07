package com.chen.train.member.req;

public class MemberRegisterReq {

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
