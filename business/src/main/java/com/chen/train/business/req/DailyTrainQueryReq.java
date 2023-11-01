package com.chen.train.business.req;

import com.chen.train.common.req.PageReq;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class DailyTrainQueryReq extends PageReq {


    /**
     * 此注解解决前后端交互中日期格式问题
     * 前端传入的是2002-01-10 这种格式后端无法解析
     * 需要这个注解进行日期格式的解析
     * 注意这是同于post请求中
     *     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
     *
     * 但是在get请求中需要使用这个注解
     *     @DateTimeFormat(pattern = "yyyy-MM-dd")
     *
     */

    @DateTimeFormat(pattern = "yyyy-MM-dd")
   private Date date;
   private String code;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DailyTrainQueryReq{");
        sb.append("date=").append(date);
        sb.append(", code='").append(code).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
