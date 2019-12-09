package com.imooc.coupon;


import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Tests {

    @Test
    public void contextLoad() {

    }

    private Long fake = 20001L;

    @Autowired
    private IUserService userService;

    @Test
    public void test01() throws CouponException {

        System.out.println(JSON.toJSONString(userService.findCouponsByStatus(fake, CouponStatus.USABLE.getCode())));
    }

    @Test
    public void test02() throws CouponException {
        System.out.println(JSON.toJSONString(userService.findAvailableTemplate(fake)));
    }
}