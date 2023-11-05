package com.chen.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyTrainTicketService {


    @Autowired
    private  DailyTrainTicketMapper dailyTrainTicketMapper;

    public void save(DailyTrainTicketSaveReq dailyTrainTicketSaveReq) {
        DateTime now = new DateTime().now();
        DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(dailyTrainTicketSaveReq, DailyTrainTicket.class);
        //如果id为空，则说明是新增
        if(ObjectUtil.isNull(dailyTrainTicket.getId()))
        {
            //获取本地线程变量中的businessId，而不再需要进行传参设置
            dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainTicket.setCreateTime(now);
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.insert(dailyTrainTicket);

        }else{
            //否则，则是修改，此时需要加上修改时间
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.updateByPrimaryKey(dailyTrainTicket);
        }

    }

    /**
     * 查询列表功能
     */

    public PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq dailyTrainTicketQueryReq) {

        DailyTrainTicketExample dailyTrainTicketExample=new DailyTrainTicketExample();
        //添加一个降序排列,后面的反而显示在前面
        dailyTrainTicketExample.setOrderByClause("id desc");

        DailyTrainTicketExample.Criteria criteria = dailyTrainTicketExample.createCriteria();
        //条件查询，必须使用createCriteria下面的方法,根据条件查询


        //直接使用pagehelper中的分页插件，其中查询一页中的两条数据，然后需要写在sql之前
        PageHelper.startPage(dailyTrainTicketQueryReq.getPage(), dailyTrainTicketQueryReq.getSize());
        List<DailyTrainTicket> dailyTrainTickets = dailyTrainTicketMapper.selectByExample(dailyTrainTicketExample);
        PageInfo<DailyTrainTicket> pageInfo=new PageInfo<>(dailyTrainTickets);

        List<DailyTrainTicketQueryResp> respList = BeanUtil.copyToList(dailyTrainTickets, DailyTrainTicketQueryResp.class);
        PageResp<DailyTrainTicketQueryResp> pageResp=new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;

    }

    /**
     * 删除功能
     */

    public void delete(Long id){
        dailyTrainTicketMapper.deleteByPrimaryKey(id);
    }


}


