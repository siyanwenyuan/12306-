package com.chen.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chen.train.common.resp.PageResp;
import com.chen.train.common.util.SnowUtil;
import com.chen.train.business.domain.TrainCarriage;
import com.chen.train.business.domain.TrainCarriageExample;
import com.chen.train.business.mapper.TrainCarriageMapper;
import com.chen.train.business.req.TrainCarriageQueryReq;
import com.chen.train.business.req.TrainCarriageSaveReq;
import com.chen.train.business.resp.TrainCarriageQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainCarriageService {


    @Autowired
    private  TrainCarriageMapper trainCarriageMapper;

    public void save(TrainCarriageSaveReq trainCarriageSaveReq) {
        DateTime now = new DateTime().now();
        TrainCarriage trainCarriage = BeanUtil.copyProperties(trainCarriageSaveReq, TrainCarriage.class);
        //如果id为空，则说明是新增
        if(ObjectUtil.isNull(trainCarriage.getId()))
        {
            //获取本地线程变量中的businessId，而不再需要进行传参设置
            trainCarriage.setId(SnowUtil.getSnowflakeNextId());
            trainCarriage.setCreateTime(now);
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.insert(trainCarriage);

        }else{
            //否则，则是修改，此时需要加上修改时间
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.updateByPrimaryKey(trainCarriage);
        }

    }

    /**
     * 查询列表功能
     */

    public PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq trainCarriageQueryReq) {

        TrainCarriageExample trainCarriageExample=new TrainCarriageExample();
        //添加一个降序排列,后面的反而显示在前面
        trainCarriageExample.setOrderByClause("id desc");

        TrainCarriageExample.Criteria criteria = trainCarriageExample.createCriteria();
        //条件查询，必须使用createCriteria下面的方法,根据条件查询


        //直接使用pagehelper中的分页插件，其中查询一页中的两条数据，然后需要写在sql之前
        PageHelper.startPage(trainCarriageQueryReq.getPage(), trainCarriageQueryReq.getSize());
        List<TrainCarriage> trainCarriages = trainCarriageMapper.selectByExample(trainCarriageExample);
        PageInfo<TrainCarriage> pageInfo=new PageInfo<>(trainCarriages);

        List<TrainCarriageQueryResp> respList = BeanUtil.copyToList(trainCarriages, TrainCarriageQueryResp.class);
        PageResp<TrainCarriageQueryResp> pageResp=new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;

    }

    /**
     * 删除功能
     */

    public void delete(Long id){
        trainCarriageMapper.deleteByPrimaryKey(id);
    }

    //查找当前车次下所有的车厢
    public List<TrainCarriage> selectByTrainCode(String trainCode){
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        trainCarriageExample.setOrderByClause("'index' asc");
        TrainCarriageExample.Criteria trainCarriageExampleCriteria = trainCarriageExample.createCriteria();
        trainCarriageExampleCriteria.andTrainCodeEqualTo(trainCode);
        return trainCarriageMapper.selectByExample(trainCarriageExample);
    }




}


