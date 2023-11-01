package com.chen.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chen.train.common.resp.PageResp;
import com.chen.train.common.util.SnowUtil;
import com.chen.train.business.domain.DailyTrain;
import com.chen.train.business.domain.DailyTrainExample;
import com.chen.train.business.mapper.DailyTrainMapper;
import com.chen.train.business.req.DailyTrainQueryReq;
import com.chen.train.business.req.DailyTrainSaveReq;
import com.chen.train.business.resp.DailyTrainQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyTrainService {


    @Autowired
    private  DailyTrainMapper dailyTrainMapper;

    public void save(DailyTrainSaveReq dailyTrainSaveReq) {
        DateTime now = new DateTime().now();
        DailyTrain dailyTrain = BeanUtil.copyProperties(dailyTrainSaveReq, DailyTrain.class);
        //如果id为空，则说明是新增
        if(ObjectUtil.isNull(dailyTrain.getId()))
        {
            //获取本地线程变量中的businessId，而不再需要进行传参设置
            dailyTrain.setId(SnowUtil.getSnowflakeNextId());
            dailyTrain.setCreateTime(now);
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.insert(dailyTrain);

        }else{
            //否则，则是修改，此时需要加上修改时间
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.updateByPrimaryKey(dailyTrain);
        }

    }

    /**
     * 查询列表功能
     */

    public PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq dailyTrainQueryReq) {

        DailyTrainExample dailyTrainExample=new DailyTrainExample();
        //添加一个降序排列,后面的反而显示在前面
        dailyTrainExample.setOrderByClause("date desc,code asc");

        DailyTrainExample.Criteria criteria = dailyTrainExample.createCriteria();
        //条件查询，必须使用createCriteria下面的方法,根据条件查询

        if(ObjectUtil.isNotNull(dailyTrainQueryReq.getDate()))
        {
            criteria.andDateEqualTo(dailyTrainQueryReq.getDate());

        }
        if(ObjectUtil.isNotEmpty(dailyTrainQueryReq.getCode()))
        {
            criteria.andCodeEqualTo(dailyTrainQueryReq.getCode());

        }


        //直接使用pagehelper中的分页插件，其中查询一页中的两条数据，然后需要写在sql之前
        PageHelper.startPage(dailyTrainQueryReq.getPage(), dailyTrainQueryReq.getSize());
        List<DailyTrain> dailyTrains = dailyTrainMapper.selectByExample(dailyTrainExample);
        PageInfo<DailyTrain> pageInfo=new PageInfo<>(dailyTrains);

        List<DailyTrainQueryResp> respList = BeanUtil.copyToList(dailyTrains, DailyTrainQueryResp.class);
        PageResp<DailyTrainQueryResp> pageResp=new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;

    }

    /**
     * 删除功能
     */

    public void delete(Long id){
        dailyTrainMapper.deleteByPrimaryKey(id);
    }


}


