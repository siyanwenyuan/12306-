package com.chen.train.member.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.chen.train.common.context.LoginMemberContext;
import com.chen.train.common.resp.PageResp;
import com.chen.train.common.util.SnowUtil;
import com.chen.train.member.domain.${Domain};
import com.chen.train.member.domain.${Domain}Example;
import com.chen.train.member.mapper.${Domain}Mapper;
import com.chen.train.member.req.${Domain}QueryReq;
import com.chen.train.member.req.${Domain}SaveReq;
import com.chen.train.member.resp.${Domain}QueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ${Domain}Service {


    @Autowired
    private  ${Domain}Mapper ${domain}Mapper;

    public void save(${Domain}SaveReq ${domain}SaveReq) {
        DateTime now = new DateTime().now();
        ${Domain} ${domain} = BeanUtil.copyProperties(${domain}SaveReq, ${Domain}.class);
        //如果id为空，则说明是新增
        if(ObjectUtil.isNull(${domain}.getId()))
        {
            //获取本地线程变量中的memberId，而不再需要进行传参设置
            ${domain}.setMemberId(LoginMemberContext.getId());
            ${domain}.setId(SnowUtil.getSnowflakeNextId());
            ${domain}.setCreateTime(now);
            ${domain}.setUpdateTime(now);
            ${domain}Mapper.insert(${domain});

        }else{
            //否则，则是修改，此时需要加上修改时间
            ${domain}.setUpdateTime(now);
            ${domain}Mapper.updateByPrimaryKey(${domain});
        }

    }

    /**
     * 查询列表功能
     */

    public PageResp<${Domain}QueryResp> queryList(${Domain}QueryReq ${domain}QueryReq) {

        ${Domain}Example ${domain}Example=new ${Domain}Example();
        //添加一个降序排列,后面的反而显示在前面
        ${domain}Example.setOrderByClause("id desc");

        ${Domain}Example.Criteria criteria = ${domain}Example.createCriteria();
        //条件查询，必须使用createCriteria下面的方法,根据条件查询
        if(ObjectUtil.isNotEmpty(${domain}QueryReq.getMemberId())){
           criteria.andMemberIdEqualTo(${domain}QueryReq.getMemberId());
        }


        //直接使用pagehelper中的分页插件，其中查询一页中的两条数据，然后需要写在sql之前
        PageHelper.startPage(${domain}QueryReq.getPage(), ${domain}QueryReq.getSize());
        List<${Domain}> ${domain}s = ${domain}Mapper.selectByExample(${domain}Example);
        PageInfo<${Domain}> pageInfo=new PageInfo<>(${domain}s);

        List<${Domain}QueryResp> respList = BeanUtil.copyToList(${domain}s, ${Domain}QueryResp.class);
        PageResp<${Domain}QueryResp> pageResp=new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;

    }

    /**
     * 删除功能
     */

    public void delete(Long id){
        ${domain}Mapper.deleteByPrimaryKey(id);
    }


}


