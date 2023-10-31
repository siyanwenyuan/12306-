package com.chen.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chen.train.business.domain.Station;
import com.chen.train.common.exception.BusinessException;
import com.chen.train.common.exception.BusinessExceptionEnum;
import com.chen.train.common.resp.PageResp;
import com.chen.train.common.util.SnowUtil;
import com.chen.train.business.domain.TrainStation;
import com.chen.train.business.domain.TrainStationExample;
import com.chen.train.business.mapper.TrainStationMapper;
import com.chen.train.business.req.TrainStationQueryReq;
import com.chen.train.business.req.TrainStationSaveReq;
import com.chen.train.business.resp.TrainStationQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainStationService {


    @Autowired
    private  TrainStationMapper trainStationMapper;

    public void save(TrainStationSaveReq trainStationSaveReq) {
        DateTime now = new DateTime().now();
        TrainStation trainStation = BeanUtil.copyProperties(trainStationSaveReq, TrainStation.class);
        //如果id为空，则说明是新增
        if(ObjectUtil.isNull(trainStation.getId()))
        {

            //保存之前需要校验唯一键是否存在
            TrainStation trainStationDB = selectByUnique(trainStationSaveReq.getTrainCode(), trainStationSaveReq.getIndex());

            if(ObjectUtil.isNotEmpty(trainStationDB)){
                //如果不是空，则不需要添加，则需要向前端抛出一个异常
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_INDEX_UNIQUE_ERROR);
            }

            //保存之前需要校验唯一键是否存在
            TrainStation trainStationDB1 = selectByUnique(trainStationSaveReq.getTrainCode(), trainStationSaveReq.getName());

            if(ObjectUtil.isNotEmpty(trainStationDB1)){
                //如果不是空，则不需要添加，则需要向前端抛出一个异常
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_NAME_UNIQUE_ERROR);
            }
            //获取本地线程变量中的businessId，而不再需要进行传参设置
            trainStation.setId(SnowUtil.getSnowflakeNextId());
            trainStation.setCreateTime(now);
            trainStation.setUpdateTime(now);
            trainStationMapper.insert(trainStation);

        }else{
            //否则，则是修改，此时需要加上修改时间
            trainStation.setUpdateTime(now);
            trainStationMapper.updateByPrimaryKey(trainStation);
        }

    }

    /**
     * 编写唯一键的生成方法
     * 按照code生成
     */

    private TrainStation selectByUnique(String trainCode,Integer index)
    {
        TrainStationExample trainStationExample=new TrainStationExample();
        trainStationExample.createCriteria().andTrainCodeEqualTo(trainCode).andIndexEqualTo(index);
        List<TrainStation> trainStationList = trainStationMapper.selectByExample(trainStationExample);
        if(ObjectUtil.isNotEmpty(trainStationList))
        {
            return trainStationList.get(0);
        }else{
            return null;

        }
    }

    /**
     * 编写唯一键
     * 按照code 和 name
     */

    private TrainStation selectByUnique(String trainCode,String name)
    {

        TrainStationExample trainStationExample=new TrainStationExample();
        trainStationExample.createCriteria().andTrainCodeEqualTo(trainCode).andNameEqualTo(name);
        List<TrainStation> trainStationList = trainStationMapper.selectByExample(trainStationExample);
        if(ObjectUtil.isNotEmpty(trainStationList))
        {
            return trainStationList.get(0);

        }else{
            return null;
        }
    }



    /**
     * 查询列表功能
     */

    public PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq trainStationQueryReq) {

        TrainStationExample trainStationExample=new TrainStationExample();
        //添加一个降序排列,后面的反而显示在前面
        trainStationExample.setOrderByClause("id desc");

        TrainStationExample.Criteria criteria = trainStationExample.createCriteria();
        //条件查询，必须使用createCriteria下面的方法,根据条件查询


        //直接使用pagehelper中的分页插件，其中查询一页中的两条数据，然后需要写在sql之前
        PageHelper.startPage(trainStationQueryReq.getPage(), trainStationQueryReq.getSize());
        List<TrainStation> trainStations = trainStationMapper.selectByExample(trainStationExample);
        PageInfo<TrainStation> pageInfo=new PageInfo<>(trainStations);

        List<TrainStationQueryResp> respList = BeanUtil.copyToList(trainStations, TrainStationQueryResp.class);
        PageResp<TrainStationQueryResp> pageResp=new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;

    }

    /**
     * 删除功能
     */

    public void delete(Long id){
        trainStationMapper.deleteByPrimaryKey(id);
    }


}


