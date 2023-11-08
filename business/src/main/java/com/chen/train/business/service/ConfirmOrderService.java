package com.chen.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.chen.train.business.domain.DailyTrainTicket;
import com.chen.train.business.enums.ConfirmOrderStatusEnum;
import com.chen.train.common.resp.PageResp;
import com.chen.train.common.util.SnowUtil;
import com.chen.train.business.domain.ConfirmOrder;
import com.chen.train.business.domain.ConfirmOrderExample;
import com.chen.train.business.mapper.ConfirmOrderMapper;
import com.chen.train.business.req.ConfirmOrderQueryReq;
import com.chen.train.business.req.ConfirmOrderDoReq;
import com.chen.train.business.resp.ConfirmOrderQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Service
public class ConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainStationService.class);


    @Autowired
    private ConfirmOrderMapper confirmOrderMapper;

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;


    public void save(ConfirmOrderDoReq confirmOrderSaveReq) {
        DateTime now = new DateTime().now();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(confirmOrderSaveReq, ConfirmOrder.class);
        //如果id为空，则说明是新增
        if (ObjectUtil.isNull(confirmOrder.getId())) {
            //获取本地线程变量中的businessId，而不再需要进行传参设置
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);

        } else {
            //否则，则是修改，此时需要加上修改时间
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateByPrimaryKey(confirmOrder);
        }

    }

    /**
     * 查询列表功能
     */

    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq confirmOrderQueryReq) {

        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        //添加一个降序排列,后面的反而显示在前面
        confirmOrderExample.setOrderByClause("id desc");

        ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();
        //条件查询，必须使用createCriteria下面的方法,根据条件查询


        //直接使用pagehelper中的分页插件，其中查询一页中的两条数据，然后需要写在sql之前
        PageHelper.startPage(confirmOrderQueryReq.getPage(), confirmOrderQueryReq.getSize());
        List<ConfirmOrder> confirmOrders = confirmOrderMapper.selectByExample(confirmOrderExample);
        PageInfo<ConfirmOrder> pageInfo = new PageInfo<>(confirmOrders);

        List<ConfirmOrderQueryResp> respList = BeanUtil.copyToList(confirmOrders, ConfirmOrderQueryResp.class);
        PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;

    }

    /**
     * 删除功能
     */

    public void delete(Long id) {
        confirmOrderMapper.deleteByPrimaryKey(id);
    }


    /**
     * 订单确认服务实现
     *
     * @param confirmOrderDoReq
     */
    public void doConfirm(ConfirmOrderDoReq confirmOrderDoReq) {

        //省略业务数据校验


        //保存确认订单表，初始化状态

        DateTime now = DateTime.now();
        Date date = confirmOrderDoReq.getDate();
        String trainCode = confirmOrderDoReq.getTrainCode();
        String start = confirmOrderDoReq.getStart();
        String end = confirmOrderDoReq.getEnd();
        ConfirmOrder confirmOrder=new ConfirmOrder();
        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        confirmOrder.setMemberId(confirmOrderDoReq.getMemberId());

        confirmOrder.setDate(date);
        confirmOrder.setTrainCode(trainCode);
        confirmOrder.setStart(start);
        confirmOrder.setEnd(end);
        confirmOrder.setDailyTrainTicketId(confirmOrderDoReq.getDailyTrainTicketId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        confirmOrder.setTickets(JSON.toJSONString(confirmOrderDoReq.getTickets()));


        confirmOrderMapper.insert(confirmOrder);


        //查询余票记录，得到真实的余票信息
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        LOG.info("真实的余票记录： {}",dailyTrainTicket);





        //进行余票扣减业务

        //进行选座
             //一个车厢一个车厢的获取座位数据

             //知道选取到适合的座位，如果一个车厢没有，则选取下一个车厢（选取两个座位的必须在同一个车厢）
        //选座完成之和的事务处理
              //对座位表的售卖情况进行修改

              //对余票的情况进行修改

              //为会员增加购买记录

              //更新确认订单为成功


    }


}


