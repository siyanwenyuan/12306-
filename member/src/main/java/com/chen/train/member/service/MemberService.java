package com.chen.train.member.service;


import cn.hutool.core.collection.CollUtil;
import com.chen.train.member.domain.Member;
import com.chen.train.member.domain.MemberExample;
import com.chen.train.member.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class MemberService {


    @Autowired

    private MemberMapper memberMapper;


    public long register(String mobile) {

        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> list = memberMapper.selectByExample(memberExample);
        if (CollUtil.isNotEmpty(list)) {
            throw new RuntimeException("此手机号已经被注册");

        }
        Member member = new Member();
        member.setId(System.currentTimeMillis());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();

    }
}
