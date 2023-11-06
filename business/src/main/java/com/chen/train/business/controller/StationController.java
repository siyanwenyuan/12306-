package com.chen.train.business.controller;

import com.chen.train.business.req.StationQueryReq;
import com.chen.train.business.req.StationSaveReq;
import com.chen.train.business.resp.StationQueryResp;
import com.chen.train.business.service.StationService;
import com.chen.train.common.resp.CommonResp;
import com.chen.train.common.resp.PageResp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/station")
public class StationController {

    @Autowired

    private StationService stationService;



    /**
     * 查询所有火车
     */

    @GetMapping("/query-all")
    public CommonResp<List<StationQueryResp>> queryAll(){

        List<StationQueryResp> stationQueryResps = stationService.queryAllStation();
        return new CommonResp<>(stationQueryResps);
    }



}