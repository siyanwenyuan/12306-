package com.chen.train.business.controller;


import com.chen.train.business.req.ConfirmOrderDoReq;
import com.chen.train.business.req.ConfirmOrderQueryReq;
import com.chen.train.business.resp.ConfirmOrderQueryResp;
import com.chen.train.business.service.ConfirmOrderService;
import com.chen.train.common.resp.CommonResp;
import com.chen.train.common.resp.PageResp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {

    @Autowired
    private ConfirmOrderService confirmOrderService;

    @PostMapping("/do")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq confirmOrderDoReq) {
        confirmOrderService.doConfirm(confirmOrderDoReq);

        return new CommonResp<>();

    }



}