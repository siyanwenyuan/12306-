package com.chen.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chen.train.common.resp.PageResp;
import com.chen.train.common.util.SnowUtil;
import com.chen.train.business.domain.TrainSeat;
import com.chen.train.business.domain.TrainSeatExample;
import com.chen.train.business.mapper.TrainSeatMapper;
import com.chen.train.business.req.TrainSeatQueryReq;
import com.chen.train.business.req.TrainSeatSaveReq;
import com.chen.train.business.resp.TrainSeatQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainSeatService {


    @Autowired
    private  TrainSeatMapper trainSeatMapper;

    public void save(TrainSeatSaveReq trainSeatSaveReq) {
        DateTime now = new DateTime().now();
        TrainSeat trainSeat = BeanUtil.copyProperties(trainSeatSaveReq, TrainSeat.class);
        //如果id为空，则说明是新增
        if(ObjectUtil.isNull(trainSeat.getId()))
        {
            //获取本地线程变量中的businessId，而不再需要进行传参设置
            trainSeat.setId(SnowUtil.getSnowflakeNextId());
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);
            trainSeatMapper.insert(trainSeat);

        }else{
            //否则，则是修改，此时需要加上修改时间
            trainSeat.setUpdateTime(now);
            trainSeatMapper.updateByPrimaryKey(trainSeat);
        }

    }

    /**
     * 查询列表功能
     */

    public PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq trainSeatQueryReq) {

        TrainSeatExample trainSeatExample=new TrainSeatExample();
        //添加一个降序排列,后面的反而显示在前面
        trainSeatExample.setOrderByClause("id desc");

        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
        //条件查询，必须使用createCriteria下面的方法,根据条件查询


        //直接使用pagehelper中的分页插件，其中查询一页中的两条数据，然后需要写在sql之前
        PageHelper.startPage(trainSeatQueryReq.getPage(), trainSeatQueryReq.getSize());
        List<TrainSeat> trainSeats = trainSeatMapper.selectByExample(trainSeatExample);
        PageInfo<TrainSeat> pageInfo=new PageInfo<>(trainSeats);

        List<TrainSeatQueryResp> respList = BeanUtil.copyToList(trainSeats, TrainSeatQueryResp.class);
        PageResp<TrainSeatQueryResp> pageResp=new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;

    }

    /**
     * 删除功能
     */

    public void delete(Long id){
        trainSeatMapper.deleteByPrimaryKey(id);
    }


}


