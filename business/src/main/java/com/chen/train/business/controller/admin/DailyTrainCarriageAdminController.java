package com.chen.train.business.controller;


import com.chen.train.common.context.LoginMemberContext;
import com.chen.train.common.resp.CommonResp;
import com.chen.train.common.resp.PageResp;
import com.chen.train.business.req.DailyTrainCarriageQueryReq;
import com.chen.train.business.req.DailyTrainCarriageSaveReq;
import com.chen.train.business.resp.DailyTrainCarriageQueryResp;
import com.chen.train.business.service.DailyTrainCarriageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/daily-train-carriage")
public class DailyTrainCarriageAdminController {

    @Autowired

    private DailyTrainCarriageService dailyTrainCarriageService;

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody DailyTrainCarriageSaveReq dailyTrainCarriageSaveReq) {
        dailyTrainCarriageService.save(dailyTrainCarriageSaveReq);
        return new CommonResp<>("添加乘客成功！");
    }

    /**
     * RequestBody 将json格式转化为Java对象所以此处不需要使用
     * @param dailyTrainCarriageQueryReq
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp <DailyTrainCarriageQueryResp>> queryList(DailyTrainCarriageQueryReq dailyTrainCarriageQueryReq){
        //直接从本地线程变量中获取businessId
        PageResp<DailyTrainCarriageQueryResp> queryList = dailyTrainCarriageService.queryList(dailyTrainCarriageQueryReq);
        return new  CommonResp<>(queryList);
    }


    /**
     * 删除功能
     */
    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id)
    {

        dailyTrainCarriageService.delete(id);
        return new CommonResp<>("删除成功");
    }


}