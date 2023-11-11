package com.chen.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.chen.train.business.domain.*;
import com.chen.train.business.enums.ConfirmOrderStatusEnum;
import com.chen.train.business.enums.SeatColEnum;
import com.chen.train.business.enums.SeatTypeEnum;
import com.chen.train.business.req.ConfirmOrderTicketReq;
import com.chen.train.common.exception.BusinessException;
import com.chen.train.common.exception.BusinessExceptionEnum;
import com.chen.train.common.resp.PageResp;
import com.chen.train.common.util.SnowUtil;
import com.chen.train.business.mapper.ConfirmOrderMapper;
import com.chen.train.business.req.ConfirmOrderQueryReq;
import com.chen.train.business.req.ConfirmOrderDoReq;
import com.chen.train.business.resp.ConfirmOrderQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.java.Log;
import org.bouncycastle.asn1.x509.qualified.RFC3739QCObjectIdentifiers;
import org.hibernate.validator.constraints.ru.INN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainStationService.class);


    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;
    @Resource
    private DailyTrainSeatService dailyTrainSeatService;


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
        List<ConfirmOrderTicketReq> tickets = confirmOrderDoReq.getTickets();
        ConfirmOrder confirmOrder = new ConfirmOrder();
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
        confirmOrder.setTickets(JSON.toJSONString(tickets));
        confirmOrderMapper.insert(confirmOrder);


        //查询余票记录，得到真实的余票信息
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        LOG.info("真实的余票记录： {}", dailyTrainTicket);

        //进行余票扣减业务,并判断余票数量是否足够
        reduceTickets(confirmOrderDoReq, dailyTrainTicket);

        /**
         * 选座：
         *  通过座位偏移量进行计算，目的是减少循环次数
         *  例子： A B 偏移量： [0,5]
         *  例子： A1 B2 C3：  [0,1,2]
         *
         */

        //进行选座
        //先进性偏移值的计算，其中偏移值起始数字是0 则都是从第一位开始计算
        //先查询第一个起始位是否被选中
        ConfirmOrderTicketReq ticketReq0 = tickets.get(0);
        String seat = ticketReq0.getSeat();
        if (StrUtil.isNotBlank(seat)) {
            LOG.info("本次购票有选座");

            //本次选座的座位都有哪些列，方便计算偏移值
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(ticketReq0.getSeatTypeCode());
            LOG.info("本次选座的座位类型：{}", colEnumList);
            //构造和前端一样的两排座位 {A1 B1 D1 E1 F1}
            //通过两次循环进行创建
            List<String> referSeatList = new ArrayList<>();
            for (int i = 1; i <= 2; i++) {
                for (SeatColEnum seatColEnum : colEnumList) {
                    //其中的code就是ABC i 就是1 2
                    referSeatList.add(seatColEnum.getCode() + i);

                }

            }
            LOG.info("用于参照的两排座位是： {}", referSeatList);
            /**
             * 计算偏移值：
             * 绝对偏移值=索引号
             * 相对偏移值=绝对偏移值-1；
             *
             */
            //先创建额一个绝对偏移值
            List<Integer> aboluteOffsetList = new ArrayList<>();
            //创建一个相对偏移值
            List<Integer> offSetList = new ArrayList<>();

            for (ConfirmOrderTicketReq confirmOrderTicketReq : tickets
            ) {
                int index = referSeatList.indexOf(confirmOrderTicketReq.getSeat());
                aboluteOffsetList.add(index);
            }
            LOG.info("绝对偏移值是 {}", aboluteOffsetList);

            for (Integer index : aboluteOffsetList
            ) {
                //通过绝对偏移值-第一位得到相对偏移值
                int offIndex = index - aboluteOffsetList.get(0);
                offSetList.add(offIndex);
            }
            LOG.info("计算的相对偏移值是： {}", offSetList);

            getSeat(date, trainCode,
                    ticketReq0.getSeatTypeCode(),
                    ticketReq0.getSeat().split("")[0],//得到A1中的A
                    offSetList,
                    dailyTrainTicket.getStartIndex(),
                    dailyTrainTicket.getEndIndex());


        } else {
            LOG.info("本次购票没有选座");
            for (ConfirmOrderTicketReq confirmOrderTicketReq : tickets) {
                getSeat(date, trainCode,
                        confirmOrderTicketReq.getSeatTypeCode(),
                        null,
                        null,
                        dailyTrainTicket.getStartIndex(),
                        dailyTrainTicket.getEndIndex());
            }
        }


        //一个车厢一个车厢的获取座位数据


        //知道选取到适合的座位，如果一个车厢没有，则选取下一个车厢（选取两个座位的必须在同一个车厢）
        //选座完成之和的事务处理
        //对座位表的售卖情况进行修改

        //对余票的情况进行修改

        //为会员增加购买记录

        //更新确认订单为成功
    }


    /**
     * 查询车厢座位数
     *
     * @param date
     * @param trainCode
     * @param seatType
     */
    private void getSeat(Date date, String trainCode, String seatType,
                         String column, List<Integer> offSetList,
                         Integer startIndex,
                         Integer endIndex) {
        //首先通过座位类型得到是属于哪个车厢的座位
        List<DailyTrainCarriage> dailyTrainCarriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        LOG.info("共查出{}个符合条件的车厢", dailyTrainCarriageList.size());
        //一个车厢一个车厢进行座位的获取
        //通过for循环得到座位列表
        for (DailyTrainCarriage dailyTrainCarriage : dailyTrainCarriageList) {
            List<DailyTrainSeat> trainSeatList = dailyTrainSeatService.selectByCarriage(date,
                    trainCode,
                    dailyTrainCarriage.getIndex());
            LOG.info("车厢{}的座位数{}", dailyTrainCarriage.getIndex(), trainSeatList.size());

            for (DailyTrainSeat dailyTrainSeat : trainSeatList
            ) {

                //对比column 如果存在column则和列号进行对比

                Integer seatIndex = dailyTrainSeat.getCarriageSeatIndex();
                String col = dailyTrainSeat.getCol();
                if (StrUtil.isBlank(column)) {
                    //如果存在，则对比序列号
                    if (!column.equals(col)) {
                        LOG.info("座位{}列值不对，继续判断下一个列值，当前列值{}，目标列值", seatIndex,
                                col, column);
                        //继续比对
                        continue;
                    }
                }

                //计算某座位在区间内是否可卖
                Boolean isChoose = calSell(dailyTrainSeat, startIndex, endIndex);
                if (isChoose) {
                    //如果为true则表示已经被选中，可以直接退回
                    LOG.info("选中座位");


                } else {

                }
                //根据offSet选剩下的座位
              if(CollUtil.isNotEmpty(offSetList))
              {
                  //如果不为空，则表示已经有座位被选，则需要计算偏移值
                  LOG.info("有偏移值{}，计算偏移值是否可选",offSetList);
                  for (int i = 1; i <offSetList.size() ; i++) {
                      //得到偏移值
                      Integer offset = offSetList.get(i);
                      //计算偏移值=当前座位索引+偏移值
                      int nextIndex= seatIndex+offset;
                      //通过偏移值得到下一个座位
                      DailyTrainSeat nextDailyTrainSeat = trainSeatList.get(nextIndex);
                      //计算偏移值后的座位是否可选
                      Boolean isChooseNext = calSell(nextDailyTrainSeat, startIndex, endIndex);
                      if (isChooseNext) {
                          LOG.info("座位{}被选中",nextDailyTrainSeat.getCarriageSeatIndex());
                      } else {
                          LOG.info("座位{}未被选中",nextDailyTrainSeat.getCarriageSeatIndex());

                      }




                  }

              }


            }

        }


    }


    /**
     * 计算某座位在区间内是否可卖
     * 其实是对sell字段进行计算
     * sell=10001;
     * 例如本次购票是sell=10001  则区间票是000
     * 其中如果是1的话则说明是有人购票
     * 其中如果是0则表示没有人购票
     * 当这个区间有1的话，也是代表有人购票
     */
    private boolean calSell(DailyTrainSeat dailyTrainSeat, Integer startIndex, Integer endIndex) {
        //先得到对应的座位售卖信息sell
        String sell = dailyTrainSeat.getSell();
        //在对字段sell进行截取
        String sellPart = sell.substring(startIndex, endIndex);
        if (Integer.parseInt(sellPart) > 0) {
            //如果区间有>0则说明被人选中，则无法购票
            LOG.info("座位{}在本次车间{}~{}已被选中，不可购票", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            return false;
        } else {
            LOG.info("座位{}在本次车间{}~{}未被选中，可以购票", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            //此处表示已经被购票，则需要将其中的0进行替换为1
            //变完前： 000
            //变完后： 111
            String curSell = sellPart.replace('0', '1');
            //在前面填充： 0111  也即是在前面补0  其中最后一个参数代表的是最终补充完后的位数
            curSell = StrUtil.fillBefore(curSell, '0', endIndex);
            //在后面补充0  变 01110  其中最后一个参数代表的是最终补充后的参数
            curSell = StrUtil.fillAfter(curSell, '0', sell.length());
            //这下售票信息sell字段已经被补充修改完毕

            // 当前区间售票信息curSell 01110与库里的已售信息sell 00001按位与，即可得到该座位卖出此票后的售票详情
            // 15(01111), 14(01110 = 01110|00000)

            //此处进行的是按位与的运算
            int newSellInt = NumberUtil.binaryToInt(curSell) | NumberUtil.binaryToInt(sell);
            //  1111,  1110
            String newSell = NumberUtil.getBinaryStr(newSellInt);
            // 01111, 01110
            newSell = StrUtil.fillBefore(newSell, '0', sell.length());
            LOG.info("座位{}被选中，原售票信息：{}，车站区间：{}~{}，即：{}，最终售票信息：{}"
                    , dailyTrainSeat.getCarriageSeatIndex(), sell, startIndex, endIndex, curSell, newSell);

            dailyTrainSeat.setSell(newSell);
            return true;


        }


    }


    /**
     * 余票扣减方法
     */

    private static void reduceTickets(ConfirmOrderDoReq req, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketReq ticketReq : req.getTickets()) {
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
            switch (seatTypeEnum) {
                case YDZ -> {
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ -> {
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setEdz(countLeft);
                    int count = dailyTrainTicket.getEdz();
                }
                case RW -> {
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setRw(countLeft);
                }
                case YW -> {
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYw(countLeft);
                }
            }
        }
    }
}





