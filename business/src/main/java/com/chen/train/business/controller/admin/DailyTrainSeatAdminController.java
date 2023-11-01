package com.chen.train.business.controller;


import com.chen.train.common.context.LoginMemberContext;
import com.chen.train.common.resp.CommonResp;
import com.chen.train.common.resp.PageResp;
import com.chen.train.business.req.DailyTrainSeatQueryReq;
import com.chen.train.business.req.DailyTrainSeatSaveReq;
import com.chen.train.business.resp.DailyTrainSeatQueryResp;
import com.chen.train.business.service.DailyTrainSeatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/daily-train-seat")
public class DailyTrainSeatAdminController {

    @Autowired

    private DailyTrainSeatService dailyTrainSeatService;

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody DailyTrainSeatSaveReq dailyTrainSeatSaveReq) {
        dailyTrainSeatService.save(dailyTrainSeatSaveReq);
        return new CommonResp<>("添加乘客成功！");
    }

    /**
     * RequestBody 将json格式转化为Java对象所以此处不需要使用
     * @param dailyTrainSeatQueryReq
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp <DailyTrainSeatQueryResp>> queryList(DailyTrainSeatQueryReq dailyTrainSeatQueryReq){
        //直接从本地线程变量中获取businessId
        PageResp<DailyTrainSeatQueryResp> queryList = dailyTrainSeatService.queryList(dailyTrainSeatQueryReq);
        return new  CommonResp<>(queryList);
    }


    /**
     * 删除功能
     */
    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id)
    {

        dailyTrainSeatService.delete(id);
        return new CommonResp<>("删除成功");
    }


}