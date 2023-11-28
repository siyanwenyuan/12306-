package com.chen.train.member.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chen.train.common.req.MemberTicketReq;
import com.chen.train.common.resp.PageResp;
import com.chen.train.common.util.SnowUtil;
import com.chen.train.member.domain.Ticket;
import com.chen.train.member.domain.TicketExample;
import com.chen.train.member.mapper.TicketMapper;
import com.chen.train.member.req.TicketQueryReq;
import com.chen.train.member.req.TicketSaveReq;
import com.chen.train.member.resp.TicketQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.bouncycastle.cms.bc.BcRSAKeyTransEnvelopedRecipient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {


    private static final Logger LOG = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private  TicketMapper ticketMapper;



    public void save(MemberTicketReq req) {

      //  LOG.info("seata全局事务ID save: {}", RootContext.getXID());
        DateTime now = new DateTime().now();
        Ticket ticket = BeanUtil.copyProperties(req, Ticket.class);

            //获取本地线程变量中的memberId，而不再需要进行传参设置
            ticket.setId(SnowUtil.getSnowflakeNextId());
            ticket.setCreateTime(now);
            ticket.setUpdateTime(now);
            ticketMapper.insert(ticket);
    }

    /**
     * 查询列表功能
     */

    public PageResp<TicketQueryResp> queryList(TicketQueryReq ticketQueryReq) {

        TicketExample ticketExample=new TicketExample();
        //添加一个降序排列,后面的反而显示在前面
        ticketExample.setOrderByClause("id desc");

        TicketExample.Criteria criteria = ticketExample.createCriteria();
        if(ObjectUtil.isNotNull(ticketQueryReq.getMemberId()))
        {
            criteria.andMemberIdEqualTo(ticketQueryReq.getMemberId());


        }
        //条件查询，必须使用createCriteria下面的方法,根据条件查询


        //直接使用pagehelper中的分页插件，其中查询一页中的两条数据，然后需要写在sql之前
        PageHelper.startPage(ticketQueryReq.getPage(), ticketQueryReq.getSize());
        List<Ticket> tickets = ticketMapper.selectByExample(ticketExample);
        PageInfo<Ticket> pageInfo=new PageInfo<>(tickets);

        List<TicketQueryResp> respList = BeanUtil.copyToList(tickets, TicketQueryResp.class);
        PageResp<TicketQueryResp> pageResp=new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;

    }

    /**
     * 删除功能
     */

    public void delete(Long id){
        ticketMapper.deleteByPrimaryKey(id);
    }


}


