package com.chen.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.chen.train.business.enums.RedisKeyPreEnum;
import com.chen.train.business.mapper.cust.SkTokenMapperCust;
import com.chen.train.common.resp.PageResp;
import com.chen.train.common.util.SnowUtil;
import com.chen.train.business.domain.SkToken;
import com.chen.train.business.domain.SkTokenExample;
import com.chen.train.business.mapper.SkTokenMapper;
import com.chen.train.business.req.SkTokenQueryReq;
import com.chen.train.business.req.SkTokenSaveReq;
import com.chen.train.business.resp.SkTokenQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SkTokenService {


    private static final Logger LOG = LoggerFactory.getLogger(SkTokenService.class);


    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    private SkTokenMapper skTokenMapper;

    @Autowired
    DailyTrainSeatService dailyTrainSeatService;

    @Autowired
    DailyTrainStationService dailyTrainStationService;

    @Autowired
    SkTokenMapperCust skTokenMapperCust;


    /**
     * 初始化
     */
    public void genDaily(Date date, String trainCode) {
        LOG.info("删除日期【{}】车次【{}】的令牌记录", DateUtil.formatDate(date), trainCode);
        SkTokenExample skTokenExample = new SkTokenExample();
        skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        skTokenMapper.deleteByExample(skTokenExample);

        DateTime now = DateTime.now();
        SkToken skToken = new SkToken();
        skToken.setDate(date);
        skToken.setTrainCode(trainCode);
        skToken.setId(SnowUtil.getSnowflakeNextId());
        skToken.setCreateTime(now);
        skToken.setUpdateTime(now);

        int seatCount = dailyTrainSeatService.countSeat(date, trainCode);
        LOG.info("车次【{}】座位数：{}", trainCode, seatCount);

        long stationCount = dailyTrainStationService.countByTrainCode(date, trainCode);
        LOG.info("车次【{}】到站数：{}", trainCode, stationCount);

        // 3/4需要根据实际卖票比例来定，一趟火车最多可以卖（seatCount * stationCount）张火车票
        int count = (int) (seatCount * stationCount); // * 3/4);
        LOG.info("车次【{}】初始生成令牌数：{}", trainCode, count);
        skToken.setCount(count);

        skTokenMapper.insert(skToken);
    }

    public void save(SkTokenSaveReq skTokenSaveReq) {
        DateTime now = new DateTime().now();
        SkToken skToken = BeanUtil.copyProperties(skTokenSaveReq, SkToken.class);
        //如果id为空，则说明是新增
        if (ObjectUtil.isNull(skToken.getId())) {
            //获取本地线程变量中的businessId，而不再需要进行传参设置
            skToken.setId(SnowUtil.getSnowflakeNextId());
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);
            skTokenMapper.insert(skToken);

        } else {
            //否则，则是修改，此时需要加上修改时间
            skToken.setUpdateTime(now);
            skTokenMapper.updateByPrimaryKey(skToken);
        }

    }

    /**
     * 查询列表功能
     */

    public PageResp<SkTokenQueryResp> queryList(SkTokenQueryReq skTokenQueryReq) {

        SkTokenExample skTokenExample = new SkTokenExample();
        //添加一个降序排列,后面的反而显示在前面
        skTokenExample.setOrderByClause("id desc");

        SkTokenExample.Criteria criteria = skTokenExample.createCriteria();
        //条件查询，必须使用createCriteria下面的方法,根据条件查询


        //直接使用pagehelper中的分页插件，其中查询一页中的两条数据，然后需要写在sql之前
        PageHelper.startPage(skTokenQueryReq.getPage(), skTokenQueryReq.getSize());
        List<SkToken> skTokens = skTokenMapper.selectByExample(skTokenExample);
        PageInfo<SkToken> pageInfo = new PageInfo<>(skTokens);

        List<SkTokenQueryResp> respList = BeanUtil.copyToList(skTokens, SkTokenQueryResp.class);
        PageResp<SkTokenQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;

    }

    /**
     * 删除功能
     */

    public void delete(Long id) {
        skTokenMapper.deleteByPrimaryKey(id);
    }


    /**
     * 校验令牌
     */
    public boolean validSkToken(Date date, String trainCode, Long memberId) {
        LOG.info("会员【{}】获取日期【{}】车次【{}】的令牌开始", memberId, DateUtil.formatDate(date), trainCode);

        // 需要去掉这段，否则发布生产后，体验多人排队功能时，会因拿不到锁而返回：等待5秒，加入20人时，只有第1次循环能拿到锁
        // if (!env.equals("dev")) {
        //     // 先获取令牌锁，再校验令牌余量，防止机器人抢票，lockKey就是令牌，用来表示【谁能做什么】的一个凭证
        //     String lockKey = RedisKeyPreEnum.SK_TOKEN + "-" + DateUtil.formatDate(date) + "-" + trainCode + "-" + memberId;
        //     Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);
        //     if (Boolean.TRUE.equals(setIfAbsent)) {
        //         LOG.info("恭喜，抢到令牌锁了！lockKey：{}", lockKey);
        //     } else {
        //         LOG.info("很遗憾，没抢到令牌锁！lockKey：{}", lockKey);
        //         return false;
        //     }
        // }
        /**
         * 解决数据库实时访问的问题
         *   使用缓存来减少数据库访问的压力
         *
         * 缓存常用设计方案如下：
         *     先在缓存中查询，如果没有再到数据库中查询
         *     然后将数据库中的结果再次设计到缓存中
         *
         */

        String skTokenCountKey = RedisKeyPreEnum.SK_TOKEN_COUNT + "-" + DateUtil.formatDate(date) + "-" + trainCode;
        Object skTokenCount = redisTemplate.opsForValue().get(skTokenCountKey);
        if (skTokenCount != null) {
            LOG.info("缓存中有该车次令牌大闸的key：{}", skTokenCountKey);
            Long count = redisTemplate.opsForValue().decrement(skTokenCountKey, 1);
            if (count < 0L) {
                LOG.error("获取令牌失败：{}", skTokenCountKey);
                return false;
            } else {
                LOG.info("获取令牌后，令牌余数：{}", count);
                redisTemplate.expire(skTokenCountKey, 60, TimeUnit.SECONDS);
                // 每获取5个令牌更新一次数据库
                if (count % 5 == 0) {
                    skTokenMapperCust.decrease(date, trainCode, 5);
                }
                return true;
            }
        } else {
            LOG.info("缓存中没有该车次令牌大闸的key：{}", skTokenCountKey);
            // 检查是否还有令牌
            SkTokenExample skTokenExample = new SkTokenExample();
            skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
            List<SkToken> tokenCountList = skTokenMapper.selectByExample(skTokenExample);
            if (CollUtil.isEmpty(tokenCountList)) {
                LOG.info("找不到日期【{}】车次【{}】的令牌记录", DateUtil.formatDate(date), trainCode);
                return false;
            }

            SkToken skToken = tokenCountList.get(0);
            if (skToken.getCount() <= 0) {
                LOG.info("日期【{}】车次【{}】的令牌余量为0", DateUtil.formatDate(date), trainCode);
                return false;
            }

            // 令牌还有余量
            // 令牌余数-1
            Integer count = skToken.getCount() - 1;
            skToken.setCount(count);
            LOG.info("将该车次令牌大闸放入缓存中，key: {}， count: {}", skTokenCountKey, count);
            // 不需要更新数据库，只要放缓存即可
            redisTemplate.opsForValue().set(skTokenCountKey, String.valueOf(count), 60, TimeUnit.SECONDS);
            // skTokenMapper.updateByPrimaryKey(skToken);
            return true;
        }

        // 令牌约等于库存，令牌没有了，就不再卖票，不需要再进入购票主流程去判断库存，判断令牌肯定比判断库存效率高
        // int updateCount = skTokenMapperCust.decrease(date, trainCode, 1);
        // if (updateCount > 0) {
        //     return true;
        // } else {
        //     return false;
        // }
    }
}


