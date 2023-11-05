package com.chen.train.business.controller;


import com.chen.train.common.context.LoginMemberContext;
import com.chen.train.common.resp.CommonResp;
import com.chen.train.common.resp.PageResp;
import com.chen.train.business.req.DailyTrainTicketQueryReq;
import com.chen.train.business.req.DailyTrainTicketSaveReq;
import com.chen.train.business.resp.DailyTrainTicketQueryResp;
import com.chen.train.business.service.DailyTrainTicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/daily-train-ticket")
public class DailyTrainTicketAdminController {

    @Autowired

    private DailyTrainTicketService dailyTrainTicketService;

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody DailyTrainTicketSaveReq dailyTrainTicketSaveReq) {
        dailyTrainTicketService.save(dailyTrainTicketSaveReq);
        return new CommonResp<>("添加乘客成功！");
    }

    /**
     * RequestBody 将json格式转化为Java对象所以此处不需要使用
     * @param dailyTrainTicketQueryReq
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp <DailyTrainTicketQueryResp>> queryList(DailyTrainTicketQueryReq dailyTrainTicketQueryReq){
        //直接从本地线程变量中获取businessId
        PageResp<DailyTrainTicketQueryResp> queryList = dailyTrainTicketService.queryList(dailyTrainTicketQueryReq);
        return new  CommonResp<>(queryList);
    }


    /**
     * 删除功能
     */
    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id)
    {

        dailyTrainTicketService.delete(id);
        return new CommonResp<>("删除成功");
    }


}