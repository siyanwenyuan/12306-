package com.chen.train.business.controller;


import com.chen.train.common.resp.CommonResp;
import com.chen.train.common.resp.PageResp;
import com.chen.train.business.req.ConfirmOrderQueryReq;
import com.chen.train.business.req.ConfirmOrderDoReq;
import com.chen.train.business.resp.ConfirmOrderQueryResp;
import com.chen.train.business.service.ConfirmOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/confirm-order")
public class ConfirmOrderAdminController {

    @Autowired

    private ConfirmOrderService confirmOrderService;

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody ConfirmOrderDoReq confirmOrderSaveReq) {
        confirmOrderService.save(confirmOrderSaveReq);
        return new CommonResp<>("添加乘客成功！");
    }

    /**
     * RequestBody 将json格式转化为Java对象所以此处不需要使用
     * @param confirmOrderQueryReq
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp <ConfirmOrderQueryResp>> queryList(ConfirmOrderQueryReq confirmOrderQueryReq){
        //直接从本地线程变量中获取businessId
        PageResp<ConfirmOrderQueryResp> queryList = confirmOrderService.queryList(confirmOrderQueryReq);
        return new  CommonResp<>(queryList);
    }


    /**
     * 删除功能
     */
    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id)
    {

        confirmOrderService.delete(id);
        return new CommonResp<>("删除成功");
    }


}