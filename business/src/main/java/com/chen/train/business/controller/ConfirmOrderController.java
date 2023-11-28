package com.chen.train.business.controller;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.chen.train.business.config.BusinessApplication;
import com.chen.train.business.req.ConfirmOrderDoReq;
import com.chen.train.business.req.ConfirmOrderQueryReq;
import com.chen.train.business.resp.ConfirmOrderQueryResp;
import com.chen.train.business.service.BeforeConfirmOrderService;
import com.chen.train.business.service.ConfirmOrderService;
import com.chen.train.common.exception.BusinessException;
import com.chen.train.common.exception.BusinessExceptionEnum;
import com.chen.train.common.resp.CommonResp;
import com.chen.train.common.resp.PageResp;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessApplication.class);


    @Autowired
    private ConfirmOrderService confirmOrderService;

    @Autowired
    private     StringRedisTemplate redisTemplate;

    @Autowired
    BeforeConfirmOrderService beforeConfirmOrderService;

    @Value("${spring.profiles.active}")
    private String env;




    @SentinelResource(value = "confirmOrderDo", blockHandler = "doConfirmBlock")
    @PostMapping("/do")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq confirmOrderDoReq) {

        if(!env.equals("dev")){
            String imageCodeToken = confirmOrderDoReq.getImageCodeToken();
            String imageCode = confirmOrderDoReq.getImageCode();
            String imageCodeRedis = redisTemplate.opsForValue().get(imageCodeToken);
            LOG.info("从redis中获取到的验证码：{}", imageCodeRedis);
            if (ObjectUtils.isEmpty(imageCodeRedis)) {
                return new CommonResp<>(false, "验证码已过期", null);
            }

            // 验证码校验，大小写忽略，提升体验，比如Oo Vv Ww容易混
            if (!imageCodeRedis.equalsIgnoreCase(imageCode)) {
                return new CommonResp<>(false, "验证码不正确", null);
            } else {
                // 验证通过后，移除验证码
                redisTemplate.delete(imageCodeToken);
            }

        }

        Long id = beforeConfirmOrderService.beforeDoConfirm(confirmOrderDoReq);
        return new CommonResp<>(String.valueOf(id));
    }



    @GetMapping("/cancel/{id}")
    public CommonResp<Integer> cancel(@PathVariable Long id) {
        Integer count = confirmOrderService.cancel(id);
        return new CommonResp<>(count);
    }

    @GetMapping("/query-line-count/{id}")
    public CommonResp<Integer> queryLineCount(@PathVariable Long id) {
        Integer count = confirmOrderService.queryLineCount(id);
        return new CommonResp<>(count);
    }
    public CommonResp<Object> doConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
        LOG.info("购票请求被限流：{}", req);
      //  throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
        CommonResp<Object> commonResp=new CommonResp<>();
        commonResp.setSuccess(false);
        commonResp.setMessage(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION.getDesc());

        return commonResp;

    }



}