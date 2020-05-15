package com.imooc.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.DistributeTarget;
import com.imooc.coupon.constant.PeriodType;
import com.imooc.coupon.constant.ProductLine;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IBuildTemplateService;
import com.imooc.coupon.service.ITemplateBaseService;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.TemplateRequest;
import com.imooc.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 优惠券模板相关的功能控制器
 */
@Slf4j
@RestController
public class CouponTemplateController {
    /** 构造优惠券模板服务 */
    private final IBuildTemplateService buildTemplateService;

    /** 优惠券模板基础服务 */
    private final ITemplateBaseService templateBaseService;

    @Autowired
    public CouponTemplateController(IBuildTemplateService buildTemplateService, ITemplateBaseService templateBaseService) {
        this.buildTemplateService = buildTemplateService;
        this.templateBaseService = templateBaseService;
    }

    /**
     * 构建优惠券模板
     * 127.0.0.1:7001/coupon-template/template/build
     * 127.0.0.1:9000/imooc/coupon-template/template/build   网关访问
     * @param request
     * @return
     * @throws CouponException
     */
    @PostMapping("/template/build")
    public CouponTemplate buildTemplate(@RequestBody TemplateRequest request) throws CouponException {
        log.info("Build Template: {}", JSON.toJSONString(request));
        return buildTemplateService.buildTemplate(request);
    }

    /**
     * 构造优惠券模板详情
     * 127.0.0.1:7001/coupon-template/template/info?id=1
     * @param id
     * @return
     * @throws CouponException
     */
    @GetMapping("/template/info")
    public CouponTemplate buildTemplateInfo(@RequestParam("id") Integer id) throws CouponException {
        log.info("Build Template Info For: {}", id);
        return templateBaseService.buildTemplateInfo(id);
    }

    /**
     * 查找所有可用的优惠券模板
     * 127.0.0.1:7001/coupon-template/template/sdk/all
     * @return所以
     */
    @GetMapping("/template/sdk/all")
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        log.info("Find All Usable Template.");
        return templateBaseService.findAllUsableTemplate();
    }

    /**
     * 获取模板 ids 到 CouponTemplateSDK 的映射
     * 127.0.0.1:7001/coupon-template/template/sdk/infos
     * @param ids
     * @return
     */
    @GetMapping("/template/sdk/infos")
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(@RequestParam("ids") Collection<Integer> ids){
        log.info("FindIds2TemplateSDK: {}", JSON.toJSONString(ids));
        return templateBaseService.findIds2TemplateSDK(ids);
    }

    /**
     * 获取模板 ids 到 CouponTemplateSDK 的映射
     * 127.0.0.1:7001/coupon-template/template/sdk/buildMany
     */
    @GetMapping("/template/sdk/buildMany")
    public boolean buildMany() throws CouponException{
        for (int i = 0; i < 10000; i++) {
            new Thread(() -> {
                //构建优惠券模板
                TemplateRequest request = new TemplateRequest();
                request.setName(RandomStringUtils.randomAlphabetic(20));
                request.setLogo(Thread.currentThread().getName());
                request.setDesc("优惠券" + Thread.currentThread().getName());
                request.setCategory(CouponCategory.MANJIAN.getCode());
                request.setProductLine(ProductLine.DAMAO.getCode());
                request.setCount(10);
                request.setUserId(10001L);
                request.setTarget(DistributeTarget.SINGLE.getCode());
                TemplateRule rule = new TemplateRule();
                rule.setExpiration(new TemplateRule.Expiration(
                        PeriodType.SHIFT.getCode(), 1, DateUtils.addDays(new Date(), 60).getTime()
                ));
                rule.setDiscount(new TemplateRule.Discount(5, 1));
                rule.setLimitation(1);
                rule.setUsage(new TemplateRule.Usage(Thread.currentThread().getName(),"beijing", JSON.toJSONString(Collections.singletonList(5))));
                rule.setWeight(JSON.toJSONString(Collections.EMPTY_LIST));
                request.setRule(rule);
                ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
                rwLock.readLock().lock();
                try {
                    buildTemplateService.buildTemplate(request);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    rwLock.readLock().unlock();
                }
            }, String.valueOf(i)).start();
        }
        return true;
    }
}


