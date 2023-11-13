package com.chen.train.member.feign;


import com.chen.train.common.req.MemberTicketReq;
import com.chen.train.common.resp.CommonResp;
import com.chen.train.member.req.TicketQueryReq;
import com.chen.train.member.req.TicketSaveReq;
import com.chen.train.member.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feign/ticket")
public class FeignTicketController {
    @Autowired
    private TicketService ticketService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody MemberTicketReq req)
    {
        ticketService.save(req);
        return new CommonResp<>();
    }
}
