package com.chen.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.chen.train.business.domain.TrainStation;
import com.chen.train.common.resp.PageResp;
import com.chen.train.common.util.SnowUtil;
import com.chen.train.business.domain.DailyTrainStation;
import com.chen.train.business.domain.DailyTrainStationExample;
import com.chen.train.business.mapper.DailyTrainStationMapper;
import com.chen.train.business.req.DailyTrainStationQueryReq;
import com.chen.train.business.req.DailyTrainStationSaveReq;
import com.chen.train.business.resp.DailyTrainStationQueryResp;
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
public class DailyTrainStationService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainStationService.class);


    @Autowired
    private TrainStationService trainStationService;

    @Autowired
    private DailyTrainStationMapper dailyTrainStationMapper;


    public void save(DailyTrainStationSaveReq dailyTrainStationSaveReq) {
        DateTime now = new DateTime().now();
        DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(dailyTrainStationSaveReq, DailyTrainStation.class);
        //如果id为空，则说明是新增
        if (ObjectUtil.isNull(dailyTrainStation.getId())) {
            //获取本地线程变量中的businessId，而不再需要进行传参设置
            dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationMapper.insert(dailyTrainStation);

        } else {
            //否则，则是修改，此时需要加上修改时间
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationMapper.updateByPrimaryKey(dailyTrainStation);
        }

    }

    /**
     * 查询列表功能
     */

    public PageResp<DailyTrainStationQueryResp> queryList(DailyTrainStationQueryReq dailyTrainStationQueryReq) {

        DailyTrainStationExample dailyTrainStationExample = new DailyTrainStationExample();
        //添加一个降序排列,后面的反而显示在前面
        dailyTrainStationExample.setOrderByClause("date desc, train_code asc, `index` asc");

        DailyTrainStationExample.Criteria criteria = dailyTrainStationExample.createCriteria();
        //条件查询，必须使用createCriteria下面的方法,根据条件查询
        if (ObjUtil.isNotNull(dailyTrainStationQueryReq.getDate())) {
            criteria.andDateEqualTo(dailyTrainStationQueryReq.getDate());
        }
        if (ObjUtil.isNotEmpty(dailyTrainStationQueryReq.getTrainCode())) {
            criteria.andTrainCodeEqualTo(dailyTrainStationQueryReq.getTrainCode());
        }


        //直接使用pagehelper中的分页插件，其中查询一页中的两条数据，然后需要写在sql之前
        PageHelper.startPage(dailyTrainStationQueryReq.getPage(), dailyTrainStationQueryReq.getSize());
        List<DailyTrainStation> dailyTrainStations = dailyTrainStationMapper.selectByExample(dailyTrainStationExample);
        PageInfo<DailyTrainStation> pageInfo = new PageInfo<>(dailyTrainStations);

        List<DailyTrainStationQueryResp> respList = BeanUtil.copyToList(dailyTrainStations, DailyTrainStationQueryResp.class);
        PageResp<DailyTrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;

    }

    /**
     * 删除功能
     */

    public void delete(Long id) {
        dailyTrainStationMapper.deleteByPrimaryKey(id);
    }


    /**
     * 执行每日车次车站生成
     */
    @Transactional
    public void genDaily(Date date, String trainCode) {
        LOG.info("生成日期【{}】车次【{}】的车站信息开始", DateUtil.formatDate(date), trainCode);
        //首先需要删除已经存在的改车次的车站信息
        DailyTrainStationExample dailyTrainStationExample = new DailyTrainStationExample();
        dailyTrainStationExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        dailyTrainStationMapper.deleteByExample(dailyTrainStationExample);
        //查询某一个车次的所有车站信息
        List<TrainStation> trainStationList = trainStationService.selectByTrainCode(trainCode);
        System.out.println(trainStationList);

        if (CollUtil.isEmpty(trainStationList)) {
            LOG.info("查到的该车次的车站的基础信息为空,则生成失败");
            return;

        }
        for (TrainStation trainStation : trainStationList
        ) {
            DateTime now = new  DateTime().now();
            DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(trainStation, DailyTrainStation.class);
            dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setDate(date);
            dailyTrainStationMapper.insert(dailyTrainStation);


        }


    }

    /**
     * 按车次查询全部车站
     */
    public long countByTrainCode(Date date, String trainCode) {
        DailyTrainStationExample example = new DailyTrainStationExample();
        example.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        long stationCount = dailyTrainStationMapper.countByExample(example);
        return stationCount;
    }

    /**
     * 按车次日期查询车站列表，用于界面显示一列车经过的车站
     */
    public List<DailyTrainStationQueryResp> queryByTrain(Date date, String trainCode) {
        DailyTrainStationExample dailyTrainStationExample = new DailyTrainStationExample();
        dailyTrainStationExample.setOrderByClause("`index` asc");
        dailyTrainStationExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        List<DailyTrainStation> list = dailyTrainStationMapper.selectByExample(dailyTrainStationExample);
        return BeanUtil.copyToList(list, DailyTrainStationQueryResp.class);
    }


}


