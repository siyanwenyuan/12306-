package com.chen.train.business.controller;


import com.chen.train.common.context.LoginMemberContext;
import com.chen.train.common.resp.CommonResp;
import com.chen.train.common.resp.PageResp;
import com.chen.train.business.req.TrainSeatQueryReq;
import com.chen.train.business.req.TrainSeatSaveReq;
import com.chen.train.business.resp.TrainSeatQueryResp;
import com.chen.train.business.service.TrainSeatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/train-seat")
public class TrainSeatAdminController {

    @Autowired

    private TrainSeatService trainSeatService;

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody TrainSeatSaveReq trainSeatSaveReq) {
        trainSeatService.save(trainSeatSaveReq);
        return new CommonResp<>("添加乘客成功！");
    }

    /**
     * RequestBody 将json格式转化为Java对象所以此处不需要使用
     * @param trainSeatQueryReq
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp <TrainSeatQueryResp>> queryList(TrainSeatQueryReq trainSeatQueryReq){
        //直接从本地线程变量中获取businessId
        PageResp<TrainSeatQueryResp> queryList = trainSeatService.queryList(trainSeatQueryReq);
        return new  CommonResp<>(queryList);
    }


    /**
     * 删除功能
     */
    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id)
    {

        trainSeatService.delete(id);
        return new CommonResp<>("删除成功");
    }


}