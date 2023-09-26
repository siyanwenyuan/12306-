package com.chen.train.member.service.impl;

import com.chen.train.member.mapper.MemberMapper;
import com.chen.train.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MemberServiceImpl implements MemberService {


    @Autowired
    private MemberMapper memberMapper;
    @Override
    public Integer count() {
        return memberMapper.count();
    }
}
