package com.chen.train.business.controller;


import com.chen.train.common.resp.CommonResp;
import com.chen.train.common.resp.PageResp;
import com.chen.train.business.req.DailyTrainTicketQueryReq;
import com.chen.train.business.req.DailyTrainTicketSaveReq;
import com.chen.train.business.resp.DailyTrainTicketQueryResp;
import com.chen.train.business.service.DailyTrainTicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/daily-train-ticket")
public class DailyTrainTicketController {

    @Autowired

    private DailyTrainTicketService dailyTrainTicketService;


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




}