package com.chen.train.business.controller;


import com.chen.train.common.context.LoginMemberContext;
import com.chen.train.common.resp.CommonResp;
import com.chen.train.common.resp.PageResp;
import com.chen.train.business.req.TrainCarriageQueryReq;
import com.chen.train.business.req.TrainCarriageSaveReq;
import com.chen.train.business.resp.TrainCarriageQueryResp;
import com.chen.train.business.service.TrainCarriageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/train-carriage")
public class TrainCarriageAdminController {

    @Autowired

    private TrainCarriageService trainCarriageService;

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody TrainCarriageSaveReq trainCarriageSaveReq) {
        trainCarriageService.save(trainCarriageSaveReq);
        return new CommonResp<>("添加乘客成功！");
    }

    /**
     * RequestBody 将json格式转化为Java对象所以此处不需要使用
     * @param trainCarriageQueryReq
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp <TrainCarriageQueryResp>> queryList(TrainCarriageQueryReq trainCarriageQueryReq){
        //直接从本地线程变量中获取businessId
        PageResp<TrainCarriageQueryResp> queryList = trainCarriageService.queryList(trainCarriageQueryReq);
        return new  CommonResp<>(queryList);
    }


    /**
     * 删除功能
     */
    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id)
    {

        trainCarriageService.delete(id);
        return new CommonResp<>("删除成功");
    }


}