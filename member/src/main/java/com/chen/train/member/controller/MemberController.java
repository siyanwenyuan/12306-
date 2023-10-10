package com.chen.train.member.controller;


import com.chen.train.common.resp.CommonResp;
import com.chen.train.member.req.MemberLoginReq;
import com.chen.train.member.req.MemberRegisterReq;
import com.chen.train.member.req.MemberSendCodeReq;
import com.chen.train.member.resp.MemberLoginResp;
import com.chen.train.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private MemberService memberService;


    /**
     * 注册
     * @param memberRegisterReq
     * @return
     */
    @PostMapping("/register")
    public CommonResp<Long> register(@Valid MemberRegisterReq memberRegisterReq){

       return new CommonResp<>(memberService.register(memberRegisterReq));


    }

    /**
     * 发送短信验证码
     * @param memberSendCodeReq
     * @return
     * RequestBody  此注解表示传入的参数是json格式
     */

    @PostMapping("/send-code")
    public CommonResp<Long> sendCode(@Valid @RequestBody MemberSendCodeReq memberSendCodeReq)

    {
        memberService.sendCode(memberSendCodeReq);
        return new CommonResp<>();

    }

    /**
     * 登录
     * @param memberLoginReq
     * @return
     */
    @PostMapping("/login")
    public CommonResp<MemberLoginResp> login(@Valid @RequestBody MemberLoginReq memberLoginReq)
    {
        MemberLoginResp resp = memberService.login(memberLoginReq);
        return new  CommonResp<>(resp);
    }




}
