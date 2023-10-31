package com.chen.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chen.train.business.domain.Station;
import com.chen.train.common.exception.BusinessException;
import com.chen.train.common.exception.BusinessExceptionEnum;
import com.chen.train.common.resp.PageResp;
import com.chen.train.common.util.SnowUtil;
import com.chen.train.business.domain.Train;
import com.chen.train.business.domain.TrainExample;
import com.chen.train.business.mapper.TrainMapper;
import com.chen.train.business.req.TrainQueryReq;
import com.chen.train.business.req.TrainSaveReq;
import com.chen.train.business.resp.TrainQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainService {


    @Autowired
    private  TrainMapper trainMapper;

    public void save(TrainSaveReq trainSaveReq) {
        DateTime now = new DateTime().now();
        Train train = BeanUtil.copyProperties(trainSaveReq, Train.class);
        //如果id为空，则说明是新增
        if(ObjectUtil.isNull(train.getId()))
        {
            //保存之前需要校验唯一键是否存在
            Train trainDB = selectByUnique(trainSaveReq.getCode());

            if(ObjectUtil.isNotEmpty(trainDB)){
                //如果不是空，则不需要添加，则需要向前端抛出一个异常
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CODE_UNIQUE_ERROR);
            }
            //获取本地线程变量中的businessId，而不再需要进行传参设置
            train.setId(SnowUtil.getSnowflakeNextId());
            train.setCreateTime(now);
            train.setUpdateTime(now);
            trainMapper.insert(train);

        }else{
            //否则，则是修改，此时需要加上修改时间
            train.setUpdateTime(now);
            trainMapper.updateByPrimaryKey(train);
        }

    }

    /**
     * 此处的唯一键是code
     */

    private Train selectByUnique(String Code){
        TrainExample trainExample=new TrainExample();
        trainExample.createCriteria().andCodeEqualTo(Code);
        List<Train> trainList = trainMapper.selectByExample(trainExample);
        if(ObjectUtil.isNotEmpty(trainList)){
            return trainList.get(0);
        }else{


            return null;
        }
    }

    /**
     * 查询列表功能
     */

    public PageResp<TrainQueryResp> queryList(TrainQueryReq trainQueryReq) {

        TrainExample trainExample=new TrainExample();
        //添加一个降序排列,后面的反而显示在前面
        trainExample.setOrderByClause("id desc");

        TrainExample.Criteria criteria = trainExample.createCriteria();
        //条件查询，必须使用createCriteria下面的方法,根据条件查询


        //直接使用pagehelper中的分页插件，其中查询一页中的两条数据，然后需要写在sql之前
        PageHelper.startPage(trainQueryReq.getPage(), trainQueryReq.getSize());
        List<Train> trains = trainMapper.selectByExample(trainExample);
        PageInfo<Train> pageInfo=new PageInfo<>(trains);

        List<TrainQueryResp> respList = BeanUtil.copyToList(trains, TrainQueryResp.class);
        PageResp<TrainQueryResp> pageResp=new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;

    }


    /**
     *实现查询车次所有编号：
     */
    public List<TrainQueryResp> queryAll(){

        TrainExample trainExample=new TrainExample();
        trainExample.setOrderByClause("code asc");
        List<Train> trainList = trainMapper.selectByExample(trainExample);
        return BeanUtil.copyToList(trainList,TrainQueryResp.class);
    }




    /**
     * 删除功能
     */

    public void delete(Long id){
        trainMapper.deleteByPrimaryKey(id);
    }


}


