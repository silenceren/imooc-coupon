package com.imooc.coupon.service;

import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;

import java.util.List;

/**
 * Redis 相关的操作服务接口定义
 * 1. 用户的三个状态优惠券 Cache 相关操作
 * 2. 优惠券模板生成的优惠券码 Cache 操作
 */
public interface IRedisService {

    /**
     * 根据 userId 和状态找到缓存的优惠券列表数据
     * @param userId 用户 id
     * @param status 优惠券状态
     * @return 注意，可能会返回 null， 代表从没有过记录
     */
    List<Coupon> getCachedCoupons(Long userId, Integer status);

    /**
     * 保存空的优惠券列表到缓存中
     */
    void saveEmptyCouponListToCache(Long userId, List<Integer> status);

    /**
     * 尝试从 Cache 中获取一个优惠券码
     * @param templateId
     * @return
     */
    String tryToAcquireCouponCodeFromCache(Integer templateId);

    /**
     * 将优惠券保存到 Cache 中
     * @param userId
     * @param coupons
     * @param status
     * @return 保存成功的个数
     * @throws CouponException
     */
    Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException;
}
