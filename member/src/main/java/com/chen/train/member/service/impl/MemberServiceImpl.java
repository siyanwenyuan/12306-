package com.chen.train.member.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.chen.train.member.domain.Member;
import com.chen.train.member.domain.MemberExample;
import com.chen.train.member.mapper.MemberMapper;
import com.chen.train.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MemberServiceImpl implements MemberService {


    @Autowired
    private MemberMapper memberMapper;
    @Override
    public Integer count() {

       return Math.toIntExact(memberMapper.countByExample(null));
    }

    @Override
    public Long register(Long mobile)
    {


        MemberExample memberExample=new MemberExample();
        memberExample.createCriteria().andMemberNumberEqualTo(mobile);
        List<Member> list = memberMapper.selectByExample(memberExample);
        if(CollUtil.isNotEmpty(list))
        {
            throw  new RuntimeException("用户手机号码已经存在");

        }


        Member member=new Member();
        member.setMemberId((int) System.currentTimeMillis());
        member.setMemberNumber(mobile);

        memberMapper.insert(member);
        return Long.valueOf(member.getMemberId());

    }
}
