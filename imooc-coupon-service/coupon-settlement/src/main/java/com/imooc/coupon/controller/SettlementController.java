package com.imooc.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.executor.ExecuteManager;
import com.imooc.coupon.vo.SettlementInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: imooc-coupon
 * @description: 结算服务 Controller
 * @author: tianwei
 * @create: 2019-12-12 16:27
 */
@Slf4j
@RestController
@RequestMapping("/settlement")
@Api(tags = "优惠券结算",description = "SettlementController")
public class SettlementController {


    private final ExecuteManager executeManager;

    @Autowired
    public SettlementController(ExecuteManager executeManager) {
        this.executeManager = executeManager;
    }

    /**
     * 优惠券结算
     * @param settlement
     * @return
     * @throws CouponException
     */
    @ApiOperation(value="优惠券结算", notes="优惠券结算")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "settlement", value = "公司信息", required = true, dataType = "SettlementInfo")
    })
    @RequestMapping(value = "/settlement/compute", method = {RequestMethod.POST})
    public SettlementInfo computeRule(@RequestBody SettlementInfo settlement) throws CouponException {
        log.info("settlement: {}", JSON.toJSONString(settlement));
        return executeManager.computeRule(settlement);
    }
}
