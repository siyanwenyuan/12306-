package com.chen.train.business.controller.admin;

import com.chen.train.common.context.LoginMemberContext;
import com.chen.train.common.resp.CommonResp;
import com.chen.train.common.resp.PageResp;
import com.chen.train.business.req.StationQueryReq;
import com.chen.train.business.req.StationSaveReq;
import com.chen.train.business.resp.StationQueryResp;
import com.chen.train.business.service.StationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/station")
public class StationAdminController {

    @Autowired

    private StationService stationService;

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody StationSaveReq stationSaveReq) {
        stationService.save(stationSaveReq);
        return new CommonResp<>("添加乘客成功！");
    }

    /**
     * RequestBody 将json格式转化为Java对象所以此处不需要使用
     * @param stationQueryReq
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp <StationQueryResp>> queryList(StationQueryReq stationQueryReq){
        //直接从本地线程变量中获取businessId
        PageResp<StationQueryResp> queryList = stationService.queryList(stationQueryReq);
        return new  CommonResp<>(queryList);
    }


    /**
     * 删除功能
     */
    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id)
    {

        stationService.delete(id);
        return new CommonResp<>("删除成功");
    }

    /**
     * 查询所有火车
     */

    @GetMapping("/query-all")
    public CommonResp<List<StationQueryResp>> queryAll(){

        List<StationQueryResp> stationQueryResps = stationService.queryAllStation();
        return new CommonResp<>(stationQueryResps);
    }



}