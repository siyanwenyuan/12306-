package com.chen.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.log.Log;
import com.chen.train.business.domain.*;
import com.chen.train.common.resp.PageResp;
import com.chen.train.common.util.SnowUtil;
import com.chen.train.business.mapper.DailyTrainMapper;
import com.chen.train.business.req.DailyTrainQueryReq;
import com.chen.train.business.req.DailyTrainSaveReq;
import com.chen.train.business.resp.DailyTrainQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class DailyTrainService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainService.class);


    @Autowired
    private TrainService trainService;

    @Resource
    private DailyTrainMapper dailyTrainMapper;

    @Autowired
    private DailyTrainStationService dailyTrainStationService;
    @Autowired
    private DailyTrainCarriageService dailyTrainCarriageService;
    @Autowired
    private DailyTrainSeatService dailyTrainSeatService;

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;




    public void save(DailyTrainSaveReq dailyTrainSaveReq) {
        DateTime now = new DateTime().now();
        DailyTrain dailyTrain = BeanUtil.copyProperties(dailyTrainSaveReq, DailyTrain.class);
        //如果id为空，则说明是新增
        if (ObjectUtil.isNull(dailyTrain.getId())) {
            //获取本地线程变量中的businessId，而不再需要进行传参设置
            dailyTrain.setId(SnowUtil.getSnowflakeNextId());
            dailyTrain.setCreateTime(now);
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.insert(dailyTrain);

        } else {
            //否则，则是修改，此时需要加上修改时间
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.updateByPrimaryKey(dailyTrain);
        }

    }

    /**
     * 查询列表功能
     */

    public PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq dailyTrainQueryReq) {

        DailyTrainExample dailyTrainExample = new DailyTrainExample();
        //添加一个降序排列,后面的反而显示在前面
        dailyTrainExample.setOrderByClause("date desc,code asc");

        DailyTrainExample.Criteria criteria = dailyTrainExample.createCriteria();
        //条件查询，必须使用createCriteria下面的方法,根据条件查询

        if (ObjectUtil.isNotNull(dailyTrainQueryReq.getDate())) {
            criteria.andDateEqualTo(dailyTrainQueryReq.getDate());

        }
        if (ObjectUtil.isNotEmpty(dailyTrainQueryReq.getCode())) {
            criteria.andCodeEqualTo(dailyTrainQueryReq.getCode());

        }


        //直接使用pagehelper中的分页插件，其中查询一页中的两条数据，然后需要写在sql之前
        PageHelper.startPage(dailyTrainQueryReq.getPage(), dailyTrainQueryReq.getSize());
        List<DailyTrain> dailyTrains = dailyTrainMapper.selectByExample(dailyTrainExample);
        PageInfo<DailyTrain> pageInfo = new PageInfo<>(dailyTrains);

        List<DailyTrainQueryResp> respList = BeanUtil.copyToList(dailyTrains, DailyTrainQueryResp.class);
        PageResp<DailyTrainQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;

    }

    /**
     * 删除功能
     */

    public void delete(Long id) {
        dailyTrainMapper.deleteByPrimaryKey(id);
    }

    /**
     * 执行定时任务
     * 生成某日车次所有信息  包括车次，车站，车座，车厢
     */
    @Transactional
    public void genDaily(Date date) {
        //查询所有车次信息
        List<Train> trainList = trainService.selectAll();
        //先对查询结果进行判空，防止出现空指针异常
        /**
         * Java中的通常需要对查询结果进行判空，
         * 目的是为了防止出现空指针异常
         * 如果该结果为空，当对结果进行操作时，比如去长度，会出现异常
         */
        if (ObjectUtil.isEmpty(trainList)) {
            //入如果为空,则直接返回
            LOG.info("没有车次基础数据，任务结束");

            return;

        }

        //循环遍历每个车次，生成车次
        for (Train train : trainList) {

            //调用每日车次生成数据
            genDailyTrain(date,train);

        }


    }

    /**
     * 执行生成每日车次的代码
     *
     * @param date
     * @param train
     */
    @Transactional
    public void genDailyTrain(Date date, Train train) {
        LOG.info("生成日期【{}】车次【{}】的信息开始", DateUtil.formatDate(date), train.getCode());

        // 删除该车次已有的数据
        DailyTrainExample dailyTrainExample = new DailyTrainExample();
        dailyTrainExample.createCriteria()
                .andDateEqualTo(date)
                .andCodeEqualTo(train.getCode());
        dailyTrainMapper.deleteByExample(dailyTrainExample);

        // 生成该车次的数据
        DateTime now = DateTime.now();
        DailyTrain dailyTrain = BeanUtil.copyProperties(train, DailyTrain.class);
        dailyTrain.setId(SnowUtil.getSnowflakeNextId());
        dailyTrain.setCreateTime(now);
        dailyTrain.setUpdateTime(now);
        dailyTrain.setDate(date);
        dailyTrainMapper.insert(dailyTrain);

        /**
         * 生成该车次的车站的基础信息
         */

        dailyTrainStationService.genDaily(date,train.getCode());
        /**
         * 生成该车次的每日车厢数据
         */
        dailyTrainCarriageService.genDaily(date,train.getCode());


        // 生成该车次的座位数据
        dailyTrainSeatService.genDaily(date, train.getCode());

        //生成该车次的余票信息
        dailyTrainTicketService.genDaily(dailyTrain,date, train.getCode());


    }


}


