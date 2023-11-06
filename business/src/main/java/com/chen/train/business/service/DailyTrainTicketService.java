package com.chen.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.chen.train.business.domain.DailyTrain;
import com.chen.train.business.domain.TrainStation;
import com.chen.train.business.enums.SeatTypeEnum;
import com.chen.train.business.enums.TrainTypeEnum;
import com.chen.train.common.resp.PageResp;
import com.chen.train.common.util.SnowUtil;
import com.chen.train.business.domain.DailyTrainTicket;
import com.chen.train.business.domain.DailyTrainTicketExample;
import com.chen.train.business.mapper.DailyTrainTicketMapper;
import com.chen.train.business.req.DailyTrainTicketQueryReq;
import com.chen.train.business.req.DailyTrainTicketSaveReq;
import com.chen.train.business.resp.DailyTrainTicketQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Service
public class DailyTrainTicketService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainStationService.class);


    @Resource
    private DailyTrainTicketMapper dailyTrainTicketMapper;
    @Resource
    private TrainStationService trainStationService;
    @Resource
    private DailyTrainSeatService dailyTrainSeatService;



    public void save(DailyTrainTicketSaveReq dailyTrainTicketSaveReq) {
        DateTime now = new DateTime().now();
        DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(dailyTrainTicketSaveReq, DailyTrainTicket.class);
        //如果id为空，则说明是新增
        if (ObjectUtil.isNull(dailyTrainTicket.getId())) {
            //获取本地线程变量中的businessId，而不再需要进行传参设置
            dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainTicket.setCreateTime(now);
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.insert(dailyTrainTicket);

        } else {
            //否则，则是修改，此时需要加上修改时间
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.updateByPrimaryKey(dailyTrainTicket);
        }

    }

    /**
     * 查询列表功能
     */

    public PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq dailyTrainTicketQueryReq) {

        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        //添加一个降序排列,后面的反而显示在前面
        dailyTrainTicketExample.setOrderByClause("id desc");

        DailyTrainTicketExample.Criteria criteria = dailyTrainTicketExample.createCriteria();
        //条件查询，必须使用createCriteria下面的方法,根据条件查询

        if (ObjUtil.isNotNull(dailyTrainTicketQueryReq.getDate())) {
            criteria.andDateEqualTo(dailyTrainTicketQueryReq.getDate());
        }
        if (ObjUtil.isNotEmpty(dailyTrainTicketQueryReq.getTrainCode())) {
            criteria.andTrainCodeEqualTo(dailyTrainTicketQueryReq.getTrainCode());
        }
        if (ObjUtil.isNotEmpty(dailyTrainTicketQueryReq.getStart())) {
            criteria.andStartEqualTo(dailyTrainTicketQueryReq.getStart());
        }
        if (ObjUtil.isNotEmpty(dailyTrainTicketQueryReq.getEnd())) {
            criteria.andEndEqualTo(dailyTrainTicketQueryReq.getEnd());
        }


        //直接使用pagehelper中的分页插件，其中查询一页中的两条数据，然后需要写在sql之前
        PageHelper.startPage(dailyTrainTicketQueryReq.getPage(), dailyTrainTicketQueryReq.getSize());
        List<DailyTrainTicket> dailyTrainTickets = dailyTrainTicketMapper.selectByExample(dailyTrainTicketExample);
        PageInfo<DailyTrainTicket> pageInfo = new PageInfo<>(dailyTrainTickets);

        List<DailyTrainTicketQueryResp> respList = BeanUtil.copyToList(dailyTrainTickets, DailyTrainTicketQueryResp.class);
        PageResp<DailyTrainTicketQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;

    }

    /**
     * 删除功能
     */

    public void delete(Long id) {
        dailyTrainTicketMapper.deleteByPrimaryKey(id);
    }

    /**
     * 查询每日余票信息
     */

    @Transactional
    public void genDaily(DailyTrain dailyTrain,Date date, String trainCode) {
        /**
         * 先删除已经存在的每日余票信息
         */

        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        dailyTrainTicketExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        dailyTrainTicketMapper.deleteByExample(dailyTrainTicketExample);

        /**
         * 查询途径的车站信息
         * ABCDE
         * A-B A-C A-D A-E
         * B-C B-D B-E
         * C-D C-E
         * 此处使用嵌套循环进行查询
         */

        List<TrainStation> trainStations = trainStationService.selectByTrainCode(trainCode);
        LOG.info("生成日期{}的该车次{}的余票信息开始", DateUtil.formatDate(date),trainCode);

        if (ObjectUtil.isEmpty(trainStations)) {
            LOG.info("该车次没有车站的基础数据，生成该车次的车站基础信息结束");
            return;
        }


        DateTime now= DateTime.now();


        //此处需要的是单向嵌套循环，则需要通过索引的方式进行
        for (int i = 0; i < trainStations.size(); i++) {

                 //得到起始站
            TrainStation trainStationStart = trainStations.get(i);
            BigDecimal sumKM=BigDecimal.ZERO;
            for (int j = (i + 1); j < trainStations.size(); j++) {
                //得到终点站
                TrainStation trainStationEnd = trainStations.get(j);
                //计算里程之和
               sumKM= sumKM.add(trainStationEnd.getKm());


                DailyTrainTicket dailyTrainTicket=new DailyTrainTicket();

                dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
                dailyTrainTicket.setDate(date);
                dailyTrainTicket.setTrainCode(trainCode);
                dailyTrainTicket.setStart(trainStationStart.getName());
                dailyTrainTicket.setStartPinyin(trainStationStart.getNamePinyin());
                dailyTrainTicket.setStartTime(trainStationStart.getOutTime());
                dailyTrainTicket.setStartIndex(trainStationStart.getIndex());
                dailyTrainTicket.setEnd(trainStationEnd.getName());
                dailyTrainTicket.setEndPinyin(trainStationEnd.getNamePinyin());
                dailyTrainTicket.setEndTime(trainStationEnd.getInTime());
                dailyTrainTicket.setEndIndex(trainStationEnd.getIndex());
                int ydz =dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.YDZ.getCode());
                int edz =dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.EDZ.getCode());
                int rw =dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.RW.getCode());
                int yw =dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.YW.getCode());
                // 票价 = 里程之和 * 座位单价 * 车次类型系数
                String trainType = dailyTrain.getType();
                // 计算票价系数：TrainTypeEnum.priceRate
                BigDecimal priceRate = EnumUtil.getFieldBy(TrainTypeEnum::getPriceRate, TrainTypeEnum::getCode, trainType);
                BigDecimal ydzPrice = sumKM.multiply(SeatTypeEnum.YDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal edzPrice = sumKM.multiply(SeatTypeEnum.EDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal rwPrice = sumKM.multiply(SeatTypeEnum.RW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal ywPrice = sumKM.multiply(SeatTypeEnum.YW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                dailyTrainTicket.setYdz(ydz);
                dailyTrainTicket.setYdzPrice(ydzPrice);
                dailyTrainTicket.setEdz(edz);
                dailyTrainTicket.setEdzPrice(edzPrice);
                dailyTrainTicket.setRw(rw);
                dailyTrainTicket.setRwPrice(rwPrice);
                dailyTrainTicket.setYw(yw);
                dailyTrainTicket.setYwPrice(ywPrice);
                dailyTrainTicket.setCreateTime(now);
                dailyTrainTicket.setUpdateTime(now);
                dailyTrainTicketMapper.insert(dailyTrainTicket);
            }

        }

        LOG.info("生成日期{}该车次{}的余票信息结束",DateUtil.formatDate(date),trainCode);



    }


}


