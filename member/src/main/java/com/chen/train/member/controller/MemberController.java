package com.chen.train.member.controller;


import com.chen.train.common.resp.CommonResp;
import com.chen.train.member.req.MemberRegisterReq;
import com.chen.train.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping("/register")
    public CommonResp<Long> register(MemberRegisterReq memberRegisterReq){

       return new CommonResp<>(memberService.register(memberRegisterReq));


    }


}
