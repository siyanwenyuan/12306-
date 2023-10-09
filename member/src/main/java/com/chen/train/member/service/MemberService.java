package com.chen.train.member.service;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.chen.train.common.exception.BusinessException;
import com.chen.train.common.exception.BusinessExceptionEnum;
import com.chen.train.common.util.SnowUtil;
import com.chen.train.member.domain.Member;
import com.chen.train.member.domain.MemberExample;
import com.chen.train.member.mapper.MemberMapper;
import com.chen.train.member.req.MemberRegisterReq;
import com.chen.train.member.req.MemberSendCodeReq;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class MemberService {

    private static final Logger LOG = LoggerFactory.getLogger(MemberService.class);


    @Autowired
    private MemberMapper memberMapper;


    public long register(MemberRegisterReq memberRegisterReq) {

        String mobile= memberRegisterReq.getMobile();
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> list = memberMapper.selectByExample(memberExample);
        if (CollUtil.isNotEmpty(list)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);

        }
        Member member = new Member();
        member.setId(SnowUtil.getSnowflakeNextId());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();

    }

    public void sendCode(MemberSendCodeReq memberSendCodeReq) {

        String mobile= memberSendCodeReq.getMobile();
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> list = memberMapper.selectByExample(memberExample);
        //如果为空，则直接插入
        if (CollUtil.isEmpty(list)) {
            LOG.info("手机号不存在，插入一条记录");
            Member member = new Member();
            member.setId(SnowUtil.getSnowflakeNextId());
            member.setMobile(mobile);
            memberMapper.insert(member);
        }else{
            LOG.info("手机号存在，不插入");
        }
        //不为空，则生成随机验证码
        //  String code=RandomUtil.randomString(4);
        //为方便测试，直接定义验证码不再每次启动都修改
        String code="8888";
        LOG.info("生成的验证码：{}",code);

        //保存短信验证表
        LOG.info("保存短信验证表");

        //对接短息通道

        LOG.info("对接短信通道");

    }
}
