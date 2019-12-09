package com.imooc.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IUserService;
import com.imooc.coupon.vo.AcquireTemplateRequest;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: imooc-coupon
 * @description: 用户服务 Controller
 * @author: tianwei
 * @create: 2019-12-09 10:23
 */
@Slf4j
@RestController
public class UserServiceController {

    private final IUserService userService;

    public UserServiceController(IUserService userService) {
        this.userService = userService;
    }

    /**
     * 根据用户 id 和状态查询优惠券记录
     * @param userId
     * @param status
     * @return
     * @throws CouponException
     */
    @GetMapping("/coupons")
    public List<Coupon> findCouponsByStatus(@RequestParam("userId") Long userId,
                                            @RequestParam("status") Integer status) throws CouponException{
        log.info("Find Coupons By Status: {}, {}", userId, status);
        return userService.findCouponsByStatus(userId, status);
    }

    /**
     * 根据用户 id 查找当前可以领取的优惠券模板
     * @param userId
     * @return
     */
    @GetMapping("/template")
    public List<CouponTemplateSDK> findAvailableTemplate(@RequestParam("userId") Long userId) throws CouponException {
        log.info("Find Available Template: {}", userId);
        return userService.findAvailableTemplate(userId);
    }

    /**
     * 用户领取优惠券
     * @param request
     * @return
     * @throws CouponException
     */
    @PostMapping("/acquire/template")
    public Coupon acquireTemplate(@RequestBody AcquireTemplateRequest request) throws CouponException {
        log.info("Acquire Template: {}", JSON.toJSONString(request));
        return userService.acquireTemplate(request);
    }

    /**
     * 结算（核销）优惠券
     * @param info
     * @return
     * @throws CouponException
     */
    @PostMapping("/settlement")
    public SettlementInfo settlement(@RequestBody SettlementInfo info) throws CouponException {
        log.info("Settlement: {}", JSON.toJSONString(info));
        return userService.settlement(info);
    }
}
