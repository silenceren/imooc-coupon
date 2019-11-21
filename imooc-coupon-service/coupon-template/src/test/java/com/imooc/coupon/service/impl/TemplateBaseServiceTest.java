package com.imooc.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.ITemplateBaseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TemplateBaseServiceTest {

    @Autowired
    private ITemplateBaseService baseService;

    @Test
    public void buildTemplateInfo() throws CouponException {

        System.out.println(JSON.toJSONString(baseService.buildTemplateInfo(10)));

        System.out.println(JSON.toJSONString(baseService.buildTemplateInfo(2)));
    }

    @Test
    public void findAllUsableTemplate() {

        System.out.println(JSON.toJSONString(baseService.findAllUsableTemplate()));
    }

    @Test
    public void findIds2TemplateSDK() {

        System.out.println(JSON.toJSONString(baseService.findIds2TemplateSDK(Arrays.asList(10,2,1))));
    }
}