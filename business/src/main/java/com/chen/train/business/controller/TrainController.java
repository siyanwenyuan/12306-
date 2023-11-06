package com.chen.train.business.controller;

import com.chen.train.business.req.TrainQueryReq;
import com.chen.train.business.req.TrainSaveReq;
import com.chen.train.business.resp.TrainQueryResp;
import com.chen.train.business.service.TrainSeatService;
import com.chen.train.business.service.TrainService;
import com.chen.train.common.resp.CommonResp;
import com.chen.train.common.resp.PageResp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/train")
public class TrainController {

    @Autowired

    private TrainService trainService;





    /**
     * 查询所有
     */

    @GetMapping("/query-all")
    public CommonResp<List<TrainQueryResp>> queryList()
    {

        List<TrainQueryResp> list = trainService.queryAll();
        return new CommonResp<>(list);

    }



}