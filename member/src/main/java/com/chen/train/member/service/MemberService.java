package com.chen.train.member.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.chen.train.common.exception.BusinessException;
import com.chen.train.common.exception.BusinessExceptionEnum;
import com.chen.train.common.util.JwtUtil;
import com.chen.train.common.util.SnowUtil;
import com.chen.train.member.domain.Member;
import com.chen.train.member.domain.MemberExample;
import com.chen.train.member.mapper.MemberMapper;
import com.chen.train.member.req.MemberLoginReq;
import com.chen.train.member.req.MemberRegisterReq;
import com.chen.train.member.req.MemberSendCodeReq;
import com.chen.train.member.resp.MemberLoginResp;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
public class MemberService {

    private static final Logger LOG = LoggerFactory.getLogger(MemberService.class);


    @Autowired
    private MemberMapper memberMapper;


    public long register(MemberRegisterReq memberRegisterReq) {

        String mobile = memberRegisterReq.getMobile();
        Member memberDB = selectByMobile(mobile);
        if (ObjectUtil.isNull(memberDB)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);

        }
        Member member = new Member();
        member.setId(SnowUtil.getSnowflakeNextId());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();

    }

    public void sendCode(MemberSendCodeReq memberSendCodeReq) {

        //首先查询数据库看是否存在
        String mobile = memberSendCodeReq.getMobile();
        Member memberDB = selectByMobile(mobile);
        //如果为空，则直接插入
        if (ObjectUtil.isNull(memberDB)) {
            LOG.info("手机号不存在，插入一条记录");
            Member member = new Member();
            member.setId(SnowUtil.getSnowflakeNextId());
            member.setMobile(mobile);
            memberMapper.insert(member);
        } else {
            LOG.info("手机号存在，不插入");
        }
        //不为空，则生成随机验证码
        //  String code=RandomUtil.randomString(4);
        //为方便测试，直接定义验证码不再每次启动都修改
        String code = "8888";
        LOG.info("生成的验证码：{}", code);

        //保存短信验证表
        LOG.info("保存短信验证表");

        //对接短息通道

        LOG.info("对接短信通道");

    }


    public MemberLoginResp login(MemberLoginReq memberLoginReq) {

        //首先查询数据库看是否存在
        String mobile = memberLoginReq.getMobile();
        String code = memberLoginReq.getCode();
        Member memberDB = selectByMobile(mobile);
        //判断电话号码是否已经存在
        if (ObjectUtil.isNull(memberDB)) {

            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);

        }
        if (!"8888".equals(code)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);
        }

        //

        //将查询得到的数据复制到返回对象中
        MemberLoginResp memberLoginResp = BeanUtil.copyProperties(memberDB, MemberLoginResp.class);

        //使用token
        String token = JwtUtil.createToken(memberLoginResp.getId(),memberLoginResp.getMobile());
        //将token设置到返回结果中，返回给前端
        memberLoginResp.setToken(token);
        return memberLoginResp;


    }

    //封装查询方法
    private Member selectByMobile(String mobile) {
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> list = memberMapper.selectByExample(memberExample);
        //如果查询到的手机号为空，则直接返回空值
        if (CollUtil.isEmpty(list)) {
            return null;

        } else {
            //如果不为空，则直接返回第一个数据
            return list.get(0);

        }
    }
}
