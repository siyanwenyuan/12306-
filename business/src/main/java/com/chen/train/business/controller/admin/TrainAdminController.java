package com.chen.train.business.controller.admin;

import com.chen.train.business.service.TrainSeatService;
import com.chen.train.common.context.LoginMemberContext;
import com.chen.train.common.resp.CommonResp;
import com.chen.train.common.resp.PageResp;
import com.chen.train.business.req.TrainQueryReq;
import com.chen.train.business.req.TrainSaveReq;
import com.chen.train.business.resp.TrainQueryResp;
import com.chen.train.business.service.TrainService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/train")
public class TrainAdminController {

    @Autowired

    private TrainService trainService;

    @Autowired
    private TrainSeatService trainSeatService;

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody TrainSaveReq trainSaveReq) {
        trainService.save(trainSaveReq);
        return new CommonResp<>("添加乘客成功！");
    }

    /**
     * RequestBody 将json格式转化为Java对象所以此处不需要使用
     * @param trainQueryReq
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp <TrainQueryResp>> queryList(TrainQueryReq trainQueryReq){
        //直接从本地线程变量中获取businessId
        PageResp<TrainQueryResp> queryList = trainService.queryList(trainQueryReq);
        return new  CommonResp<>(queryList);
    }


    /**
     * 删除功能
     */
    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id)
    {

        trainService.delete(id);
        return new CommonResp<>("删除成功");
    }

    /**
     * 查询所有
     */

    @GetMapping("/query-all")
    public CommonResp<List<TrainQueryResp>> queryList()
    {

        List<TrainQueryResp> list = trainService.queryAll();
        return new CommonResp<>(list);

    }

    /**
     * 根据车次生成座位
     * @param
     * @return
     */

    @GetMapping("/gen-seat/{trainCode}")
    public CommonResp<Object> genSeat(@PathVariable String trainCode){

        trainSeatService.genTrainSeat(trainCode);
        return new CommonResp<>();
    }




}