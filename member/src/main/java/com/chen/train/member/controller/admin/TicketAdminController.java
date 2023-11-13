package com.chen.train.member.controller.admin;

import com.chen.train.common.context.LoginMemberContext;
import com.chen.train.common.req.MemberTicketReq;
import com.chen.train.common.resp.CommonResp;
import com.chen.train.common.resp.PageResp;
import com.chen.train.member.req.TicketQueryReq;
import com.chen.train.member.req.TicketSaveReq;
import com.chen.train.member.resp.TicketQueryResp;
import com.chen.train.member.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/ticket")
public class TicketAdminController {

    @Autowired

    private TicketService ticketService;

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody MemberTicketReq req) {
        ticketService.save(req);
        return new CommonResp<>("添加乘客成功！");
    }

    /**
     * RequestBody 将json格式转化为Java对象所以此处不需要使用
     * @param ticketQueryReq
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp <TicketQueryResp>> queryList(TicketQueryReq ticketQueryReq){
        //直接从本地线程变量中获取memberId
        PageResp<TicketQueryResp> queryList = ticketService.queryList(ticketQueryReq);
        return new  CommonResp<>(queryList);
    }


    /**
     * 删除功能
     */
    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id)
    {

        ticketService.delete(id);
        return new CommonResp<>("删除成功");
    }


}