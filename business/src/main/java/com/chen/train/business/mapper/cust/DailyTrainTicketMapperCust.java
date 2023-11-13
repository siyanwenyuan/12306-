package com.chen.train.business.mapper.cust;

import org.apache.ibatis.annotations.Mapper;

import java.util.Date;


@Mapper
public interface DailyTrainTicketMapperCust {

    void updateCountBySell(String seatTypeCode,Date date, String trainCode, Integer minStartIndex
            , Integer maxStartIndex
            , Integer minEndIndex
            , Integer maxEndIndex);
}
