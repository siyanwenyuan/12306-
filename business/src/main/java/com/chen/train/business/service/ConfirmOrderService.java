package com.chen.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
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
import com.chen.train.business.utils.SpringUtil;
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
import org.bouncycastle.asn1.x509.qualified.RFC3739QCObjectIdentifiers;
import org.hibernate.validator.constraints.ru.INN;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Service
public class ConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainStationService.class);


    @Resource
    private AfterConfirmOrderService afterConfirmOrderService;

/*
    AfterConfirmOrderService afterConfirmOrderService  =  SpringUtil.getBean(AfterConfirmOrderService.class);
*/


    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;
    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;


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

        /**
         * 超卖：多个用户竞争同一个资源
         * 例如：100个用户买用一个座位，可能会存在这个座位被同时售卖多次
         *
         * 加锁：
         *   对每一个用户进程加锁，当一个用户拿到锁之后，只能由这个用户继续访问
         *   对于后面的用户，要等待第一个用户锁释放，拿到锁之后，才可以继续访问
         *   后面的用户处于阻塞状态
         *
         *
         * 使用synchronized单机锁：
         *  这个只是适用于单机版本，如果是多个节点，这个锁则无法实现共享
         *  这样导致多节点情况下存在加锁失败，也就是依然出现超卖的情况
         *  也就是不同节点会拿到不一样的锁
         *
         *
         * 使用redis分布式锁解决超卖问题
         *   redis实现了共享锁，即使多个节点竞争同一个资源，每个资源拿到的是同一把锁
         *
         */

        //生成一个key
        String lockKey = DateUtil.formatDate(confirmOrderDoReq.getDate()) + "-" + confirmOrderDoReq.getTrainCode();


          /*  //定义一个锁
            Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);
            if (ifAbsent) {
                LOG.info("恭喜！ 抢到锁了");
            } else {
                //只是没有抢到锁，并不是抢票失败，
                LOG.info("很遗憾，没有抢到锁");
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
            }*/




      /*  try {
            lock = redissonClient.getLock(lockKey);
            boolean tryLock = lock.tryLock(0, TimeUnit.SECONDS);
            if(tryLock){
                LOG.info("恭喜你，加锁成功");
            }else{
                LOG.info("加锁失败");
                throw  new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);

            }


        } catch (Exception e) {


        }*/

        //省略业务数据校验


        //保存确认订单表，初始化状态

        /**
         * 自带看门狗的锁，
         * 其中自动增加时间，
         * 也就是一直等到当前线程执行完毕才会自动解锁
         * 但是如果目前线程未执行完毕，其中会自动增加时间
         *
         *
         */

        RLock lock = null;
        try {

            lock = redissonClient.getLock(lockKey);

            //看门狗默认的超时时间是30秒

            boolean tryLock = lock.tryLock(0, TimeUnit.SECONDS);
            if (tryLock) {
                LOG.info("恭喜你，加锁成功");
            } else {
                LOG.info("加锁失败");
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);

            }

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

            //保存最终选座结果
            //先用临时变量进行保存
            List<DailyTrainSeat> finalSeatList = new ArrayList<>();


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

                for (ConfirmOrderTicketReq confirmOrderTicketReq : tickets) {
                    int index = referSeatList.indexOf(confirmOrderTicketReq.getSeat());
                    aboluteOffsetList.add(index);
                }
                LOG.info("绝对偏移值是 {}", aboluteOffsetList);

                for (Integer index : aboluteOffsetList) {
                    //通过绝对偏移值-第一位得到相对偏移值
                    int offIndex = index - aboluteOffsetList.get(0);
                    offSetList.add(offIndex);
                }
                LOG.info("计算的相对偏移值是： {}", offSetList);

                getSeat(finalSeatList, date, trainCode, ticketReq0.getSeatTypeCode(), ticketReq0.getSeat().split("")[0],//得到A1中的A
                        offSetList, dailyTrainTicket.getStartIndex(), dailyTrainTicket.getEndIndex());


            } else {
                LOG.info("本次购票没有选座");
                for (ConfirmOrderTicketReq confirmOrderTicketReq : tickets) {
                    getSeat(finalSeatList, date, trainCode, confirmOrderTicketReq.getSeatTypeCode(), null, null, dailyTrainTicket.getStartIndex(), dailyTrainTicket.getEndIndex());
                }
            }

            LOG.info("最终的选座{}", finalSeatList);
            //选座完成之后的事务处理
            /**
             * 这里因为牵涉到批量数据库操作，因此需要进行事务的处理
             * 但是在设计原则中最好是使用短事务，优于长事务，因为长事务会增加数据库资源
             * 所以在这里写了另外一个类，调用另外类中的事务处理
             */

            //对座位表的售卖情况进行修改
            //对余票的情况进行修改
            //为会员增加购买记录
            //更新确认订单为成
            afterConfirmOrderService.afterDoConfirm(dailyTrainTicket, finalSeatList, tickets, confirmOrder);
        } catch (RuntimeException e) {
            LOG.info("购票异常");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //释放锁，判断如果不为空且为当前线程的锁，则释放锁,解决了误删别人的锁的问题

            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }

        }


       /* try{

        }catch (Exception e){
            LOG.info("保存购票信息失败");
            throw  new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION);

        }*/            //


        //删除分布式锁,因为购票结束，需要释放锁
        //    redisTemplate.delete(lockKey);


    }


    /**
     * 选座功能
     *
     * @param date
     * @param trainCode
     * @param seatType
     */
    private void getSeat(List<DailyTrainSeat> finalSeatList, Date date, String trainCode, String seatType, String column, List<Integer> offsetList, Integer startIndex, Integer endIndex) {
        List<DailyTrainSeat> getSeatList = new ArrayList<>();
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        LOG.info("共查出{}个符合条件的车厢", carriageList.size());

        // 一个车箱一个车箱的获取座位数据
        for (DailyTrainCarriage dailyTrainCarriage : carriageList) {
            LOG.info("开始从车厢{}选座", dailyTrainCarriage.getIndex());
            getSeatList = new ArrayList<>();
            List<DailyTrainSeat> seatList = dailyTrainSeatService.selectByCarriage(date, trainCode, dailyTrainCarriage.getIndex());
            LOG.info("车厢{}的座位数：{}", dailyTrainCarriage.getIndex(), seatList.size());
            for (int i = 0; i < seatList.size(); i++) {
                DailyTrainSeat dailyTrainSeat = seatList.get(i);
                Integer seatIndex = dailyTrainSeat.getCarriageSeatIndex();
                String col = dailyTrainSeat.getCol();

                // 判断当前座位不能被选中过
                boolean alreadyChooseFlag = false;
                for (DailyTrainSeat finalSeat : finalSeatList) {
                    if (finalSeat.getId().equals(dailyTrainSeat.getId())) {
                        alreadyChooseFlag = true;
                        break;
                    }
                }
                if (alreadyChooseFlag) {
                    LOG.info("座位{}被选中过，不能重复选中，继续判断下一个座位", seatIndex);
                    continue;
                }

                // 判断column，有值的话要比对列号
                if (StrUtil.isBlank(column)) {
                    LOG.info("无选座");
                } else {
                    if (!column.equals(col)) {
                        LOG.info("座位{}列值不对，继续判断下一个座位，当前列值：{}，目标列值：{}", seatIndex, col, column);
                        continue;
                    }
                }

                boolean isChoose = calSell(dailyTrainSeat, startIndex, endIndex);
                if (isChoose) {
                    LOG.info("选中座位");
                    getSeatList.add(dailyTrainSeat);
                } else {
                    continue;
                }

                // 根据offset选剩下的座位
                boolean isGetAllOffsetSeat = true;
                if (CollUtil.isNotEmpty(offsetList)) {
                    LOG.info("有偏移值：{}，校验偏移的座位是否可选", offsetList);
                    // 从索引1开始，索引0就是当前已选中的票
                    for (int j = 1; j < offsetList.size(); j++) {
                        Integer offset = offsetList.get(j);
                        // 座位在库的索引是从1开始
                        // int nextIndex = seatIndex + offset - 1;
                        int nextIndex = i + offset;

                        // 有选座时，一定是在同一个车箱
                        if (nextIndex >= seatList.size()) {
                            LOG.info("座位{}不可选，偏移后的索引超出了这个车箱的座位数", nextIndex);
                            isGetAllOffsetSeat = false;
                            break;
                        }

                        DailyTrainSeat nextDailyTrainSeat = seatList.get(nextIndex);
                        boolean isChooseNext = calSell(nextDailyTrainSeat, startIndex, endIndex);
                        if (isChooseNext) {
                            LOG.info("座位{}被选中", nextDailyTrainSeat.getCarriageSeatIndex());
                            getSeatList.add(nextDailyTrainSeat);
                        } else {
                            LOG.info("座位{}不可选", nextDailyTrainSeat.getCarriageSeatIndex());
                            isGetAllOffsetSeat = false;
                            break;
                        }
                    }
                }
                if (!isGetAllOffsetSeat) {
                    getSeatList = new ArrayList<>();
                    continue;
                }

                // 保存选好的座位
                finalSeatList.addAll(getSeatList);
                return;
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
        // 00001, 00000
        String sell = dailyTrainSeat.getSell();
        //  000, 000
        String sellPart = sell.substring(startIndex, endIndex);
        if (Integer.parseInt(sellPart) > 0) {
            LOG.info("座位{}在本次车站区间{}~{}已售过票，不可选中该座位", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            return false;
        } else {
            LOG.info("座位{}在本次车站区间{}~{}未售过票，可选中该座位", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            //  111,   111
            String curSell = sellPart.replace('0', '1');
            // 0111,  0111
            curSell = StrUtil.fillBefore(curSell, '0', endIndex);
            // 01110, 01110
            curSell = StrUtil.fillAfter(curSell, '0', sell.length());

            // 当前区间售票信息curSell 01110与库里的已售信息sell 00001按位与，即可得到该座位卖出此票后的售票详情
            // 15(01111), 14(01110 = 01110|00000)
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





