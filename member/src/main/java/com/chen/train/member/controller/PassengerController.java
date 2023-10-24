package com.chen.train.member.controller;


import com.chen.train.common.context.LoginMemberContext;
import com.chen.train.common.resp.CommonResp;
import com.chen.train.common.resp.PageResp;
import com.chen.train.member.req.PassengerQueryReq;
import com.chen.train.member.req.PassengerSaveReq;
import com.chen.train.member.resp.PassengerQueryResp;
import com.chen.train.member.service.PassengerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/passenger")
public class PassengerController {

    @Autowired

    private PassengerService passengerService;

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody PassengerSaveReq passengerSaveReq) {
        passengerService.save(passengerSaveReq);
        return new CommonResp<>("添加乘客成功！");
    }

    /**
     * RequestBody 将json格式转化为Java对象所以此处不需要使用
     * @param passengerQueryReq
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp <PassengerQueryResp>> queryList(PassengerQueryReq passengerQueryReq){
        //直接从本地线程变量中获取memberId
        PageResp<PassengerQueryResp> queryList = passengerService.queryList(passengerQueryReq);
        return new  CommonResp<>(queryList);
    }


    /**
     * 删除功能
     */
    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id)
    {

        passengerService.delete(id);
        return new CommonResp<>("删除成功");
    }


}