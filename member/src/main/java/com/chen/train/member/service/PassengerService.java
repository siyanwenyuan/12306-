package com.chen.train.member.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.chen.train.common.util.SnowUtil;
import com.chen.train.member.domain.Passenger;
import com.chen.train.member.mapper.PassengerMapper;
import com.chen.train.member.req.PassengerSaveReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PassengerService{


    @Autowired
    private PassengerMapper passengerMapper;

    public void save(PassengerSaveReq passengerSaveReq)
    {
        DateTime now=new DateTime().now();
        Passenger passenger= BeanUtil.copyProperties(passengerSaveReq,Passenger.class);
        passenger.setId(SnowUtil.getSnowflakeNextId());
        passenger.setCreateTime(now);
        passenger.setUpdateTime(now);

        passengerMapper.insert(passenger);
    }


}


