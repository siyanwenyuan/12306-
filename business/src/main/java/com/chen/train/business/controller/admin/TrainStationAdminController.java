package com.chen.train.business.controller;


import com.chen.train.common.context.LoginMemberContext;
import com.chen.train.common.resp.CommonResp;
import com.chen.train.common.resp.PageResp;
import com.chen.train.business.req.TrainStationQueryReq;
import com.chen.train.business.req.TrainStationSaveReq;
import com.chen.train.business.resp.TrainStationQueryResp;
import com.chen.train.business.service.TrainStationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/train-station")
public class TrainStationAdminController {

    @Autowired

    private TrainStationService trainStationService;

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody TrainStationSaveReq trainStationSaveReq) {
        trainStationService.save(trainStationSaveReq);
        return new CommonResp<>("添加乘客成功！");
    }

    /**
     * RequestBody 将json格式转化为Java对象所以此处不需要使用
     * @param trainStationQueryReq
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp <TrainStationQueryResp>> queryList(TrainStationQueryReq trainStationQueryReq){
        //直接从本地线程变量中获取businessId
        PageResp<TrainStationQueryResp> queryList = trainStationService.queryList(trainStationQueryReq);
        return new  CommonResp<>(queryList);
    }


    /**
     * 删除功能
     */
    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id)
    {

        trainStationService.delete(id);
        return new CommonResp<>("删除成功");
    }


}