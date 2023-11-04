package com.chen.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.chen.train.business.domain.*;
import com.chen.train.business.enums.SeatColEnum;
import com.chen.train.business.mapper.TrainCarriageMapper;
import com.chen.train.common.resp.PageResp;
import com.chen.train.common.util.SnowUtil;
import com.chen.train.business.mapper.TrainSeatMapper;
import com.chen.train.business.req.TrainSeatQueryReq;
import com.chen.train.business.req.TrainSeatSaveReq;
import com.chen.train.business.resp.TrainSeatQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.hibernate.validator.internal.constraintvalidators.hv.Mod11CheckValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainSeatService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainSeatService.class);


    @Autowired
    private TrainSeatMapper trainSeatMapper;

    @Resource
    private TrainCarriageService trainCarriageService;


    public void save(TrainSeatSaveReq trainSeatSaveReq) {
        DateTime now = new DateTime().now();

        TrainSeat trainSeat = BeanUtil.copyProperties(trainSeatSaveReq, TrainSeat.class);
        //如果id为空，则说明是新增
        if (ObjectUtil.isNull(trainSeat.getId())) {
            //获取本地线程变量中的businessId，而不再需要进行传参设置
            trainSeat.setId(SnowUtil.getSnowflakeNextId());
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);
            trainSeatMapper.insert(trainSeat);

        } else {
            //否则，则是修改，此时需要加上修改时间
            trainSeat.setUpdateTime(now);
            trainSeatMapper.updateByPrimaryKey(trainSeat);
        }

    }

    /**
     * 查询列表功能
     */

    public PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq trainSeatQueryReq) {

        TrainSeatExample trainSeatExample = new TrainSeatExample();
        //添加一个降序排列,后面的反而显示在前面
        trainSeatExample.setOrderByClause("id desc");

        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
        //条件查询，必须使用createCriteria下面的方法,根据条件查询


        //直接使用pagehelper中的分页插件，其中查询一页中的两条数据，然后需要写在sql之前
        PageHelper.startPage(trainSeatQueryReq.getPage(), trainSeatQueryReq.getSize());
        List<TrainSeat> trainSeats = trainSeatMapper.selectByExample(trainSeatExample);
        PageInfo<TrainSeat> pageInfo = new PageInfo<>(trainSeats);

        List<TrainSeatQueryResp> respList = BeanUtil.copyToList(trainSeats, TrainSeatQueryResp.class);
        PageResp<TrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;

    }

    /**
     * 删除功能
     */

    public void delete(Long id) {
        trainSeatMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据车次生成火车座位
     */


    //此处牵涉到批量插入因此需要考虑到数据库中事务的问题
    //不能出现生成到一半出现了异常或则错误，必须同时成功，同时失败
    @Transactional
    public void genTrainSeat(String trainCode) {
        DateTime now = new DateTime().now();

        //首先需要清空当前车次的座位记录
        //构造条件，与传入的trainCode进行比对
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        trainSeatMapper.deleteByExample(trainSeatExample);
        //查找当前车次下所有的车厢
        List<TrainCarriage> carriageList = trainCarriageService.selectByTrainCode(trainCode);
        //循环得到每个车厢
        for (TrainCarriage trainCarriage : carriageList) {
            //得到该节车厢行数和座位类型
            Integer rowCount = trainCarriage.getRowCount();
            String seatType = trainCarriage.getSeatType();
            //定义索引
            int seatIndex = 1;

            //根据车厢座位类型，筛选出列
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(seatType);
            LOG.info("根据车厢的座位类型，筛选出所有的列：{}", colEnumList);
            //循环行数
            //此处从1开始，因为如果是rowCount=1的话，则会循环两次，所以从1开始循环
            for (int row = 1; row <= rowCount; row++) {
                //循环列数
                for (SeatColEnum seatColEnum : colEnumList) {
                    //构造座位数据并保存数据库
                    TrainSeat trainSeat = new TrainSeat();
                    trainSeat.setId(SnowUtil.getSnowflakeNextId());
                    trainSeat.setTrainCode(trainCode);
                    trainSeat.setCarriageIndex(trainCarriage.getIndex());
                    //此处使用hutool工具类，如果是两位则不需要填充，如果是一位则需要填充到两位，前面加0
                    trainSeat.setRow(StrUtil.fillBefore(String.valueOf(row), '0', 2));
                    trainSeat.setCol(seatColEnum.getCode());
                    trainSeat.setSeatType(seatType);
                    trainSeat.setCarriageSeatIndex(seatIndex++);
                    trainSeat.setCreateTime(now);
                    trainSeat.setUpdateTime(now);
                    trainSeatMapper.insert(trainSeat);
                }

            }


        }


    }

    public List<TrainSeat> selectByTrainCode(String trainCode)
    {
        TrainSeatExample trainSeatExample=new TrainSeatExample();
        trainSeatExample.setOrderByClause("'id' asc");
        trainSeatExample.createCriteria().andTrainCodeEqualTo(trainCode);
        List<TrainSeat> trainSeatList = trainSeatMapper.selectByExample(trainSeatExample
        );

        return trainSeatList;

    }


}


