package com.chen.train.business.controller;


import com.chen.train.common.context.LoginMemberContext;
import com.chen.train.common.resp.CommonResp;
import com.chen.train.common.resp.PageResp;
import com.chen.train.business.req.SkTokenQueryReq;
import com.chen.train.business.req.SkTokenSaveReq;
import com.chen.train.business.resp.SkTokenQueryResp;
import com.chen.train.business.service.SkTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/sk-token")
public class SkTokenAdminController {

    @Autowired

    private SkTokenService skTokenService;

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody SkTokenSaveReq skTokenSaveReq) {
        skTokenService.save(skTokenSaveReq);
        return new CommonResp<>("添加乘客成功！");
    }

    /**
     * RequestBody 将json格式转化为Java对象所以此处不需要使用
     * @param skTokenQueryReq
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp <SkTokenQueryResp>> queryList(SkTokenQueryReq skTokenQueryReq){
        //直接从本地线程变量中获取businessId
        PageResp<SkTokenQueryResp> queryList = skTokenService.queryList(skTokenQueryReq);
        return new  CommonResp<>(queryList);
    }


    /**
     * 删除功能
     */
    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id)
    {

        skTokenService.delete(id);
        return new CommonResp<>("删除成功");
    }


}