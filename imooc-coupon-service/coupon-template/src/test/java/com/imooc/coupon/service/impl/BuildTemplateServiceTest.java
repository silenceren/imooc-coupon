package com.imooc.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.DistributeTarget;
import com.imooc.coupon.constant.PeriodType;
import com.imooc.coupon.constant.ProductLine;
import com.imooc.coupon.service.IBuildTemplateService;
import com.imooc.coupon.vo.TemplateRequest;
import com.imooc.coupon.vo.TemplateRule;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BuildTemplateServiceTest {

    @Autowired
    private IBuildTemplateService buildTemplateService;

    @Test
    public void buildTemplate() throws Exception {

        System.out.println(JSON.toJSONString(buildTemplateService.buildTemplate(fakeTemplateRequest())));
        Thread.sleep(5000);

    }

    private TemplateRequest fakeTemplateRequest() {

        TemplateRequest request = new TemplateRequest();
        request.setName("优惠券模板-" + new Date().getTime());
        request.setLogo("hahahahaha");
        request.setDesc("优惠券");
        request.setCategory(CouponCategory.MANJIAN.getCode());
        request.setProductLine(ProductLine.DAMAO.getCode());
        request.setCount(10000);
        request.setUserId(10001L);
        request.setTarget(DistributeTarget.SINGLE.getCode());

        TemplateRule rule = new TemplateRule();
        rule.setExpiration(new TemplateRule.Expiration(
                PeriodType.SHIFT.getCode(), 1, DateUtils.addDays(new Date(), 60).getTime()
        ));
        rule.setDiscount(new TemplateRule.Discount(5, 1));
        rule.setLimitation(1);
        rule.setUsage(new TemplateRule.Usage("北京","北京市", JSON.toJSONString(Arrays.asList("文娱", "家居"))));
        rule.setWeight(JSON.toJSONString(Collections.EMPTY_LIST));

        request.setRule(rule);

        return request;
    }
}