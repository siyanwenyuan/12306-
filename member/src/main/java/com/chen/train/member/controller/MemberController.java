package com.chen.train.member.controller;


import com.chen.train.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;
    @GetMapping("/count")
    public Integer count(){

        return memberService.count();
    }


    @PostMapping("/register")
    public Long register(Long mobile)
    {
      Long memberId=  memberService.register(mobile);
      return memberId;
    }


}
