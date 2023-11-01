package com.chen.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chen.train.common.resp.PageResp;
import com.chen.train.common.util.SnowUtil;
import com.chen.train.business.domain.DailyTrainSeat;
import com.chen.train.business.domain.DailyTrainSeatExample;
import com.chen.train.business.mapper.DailyTrainSeatMapper;
import com.chen.train.business.req.DailyTrainSeatQueryReq;
import com.chen.train.business.req.DailyTrainSeatSaveReq;
import com.chen.train.business.resp.DailyTrainSeatQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyTrainSeatService {


    @Autowired
    private  DailyTrainSeatMapper dailyTrainSeatMapper;

    public void save(DailyTrainSeatSaveReq dailyTrainSeatSaveReq) {
        DateTime now = new DateTime().now();
        DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(dailyTrainSeatSaveReq, DailyTrainSeat.class);
        //如果id为空，则说明是新增
        if(ObjectUtil.isNull(dailyTrainSeat.getId()))
        {
            //获取本地线程变量中的businessId，而不再需要进行传参设置
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.insert(dailyTrainSeat);

        }else{
            //否则，则是修改，此时需要加上修改时间
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.updateByPrimaryKey(dailyTrainSeat);
        }

    }

    /**
     * 查询列表功能
     */

    public PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq dailyTrainSeatQueryReq) {

        DailyTrainSeatExample dailyTrainSeatExample=new DailyTrainSeatExample();
        //添加一个降序排列,后面的反而显示在前面
        dailyTrainSeatExample.setOrderByClause("date desc, train_code asc, carriage_index asc, carriage_seat_index asc");

        DailyTrainSeatExample.Criteria criteria = dailyTrainSeatExample.createCriteria();
        //条件查询，必须使用createCriteria下面的方法,根据条件查询
        if (ObjectUtil.isNotEmpty(dailyTrainSeatQueryReq.getTrainCode())) {
            criteria.andTrainCodeEqualTo(dailyTrainSeatQueryReq.getTrainCode());
        }


        //直接使用pagehelper中的分页插件，其中查询一页中的两条数据，然后需要写在sql之前
        PageHelper.startPage(dailyTrainSeatQueryReq.getPage(), dailyTrainSeatQueryReq.getSize());
        List<DailyTrainSeat> dailyTrainSeats = dailyTrainSeatMapper.selectByExample(dailyTrainSeatExample);
        PageInfo<DailyTrainSeat> pageInfo=new PageInfo<>(dailyTrainSeats);

        List<DailyTrainSeatQueryResp> respList = BeanUtil.copyToList(dailyTrainSeats, DailyTrainSeatQueryResp.class);
        PageResp<DailyTrainSeatQueryResp> pageResp=new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;

    }

    /**
     * 删除功能
     */

    public void delete(Long id){
        dailyTrainSeatMapper.deleteByPrimaryKey(id);
    }


}


