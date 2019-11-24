package com.imooc.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis 相关操作服务接口实现
 */
@Slf4j
@Service
public class RedisServiceImpl implements IRedisService {

    /**  */
    private final StringRedisTemplate redisTemplate;

    public RedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<Coupon> getCachedCoupons(Long userId, Integer status) {

        log.info("Get Coupon From Cache: {}, {}", userId, status);
        String redisKey = status2RedisKey(status, userId);

        List<String> couponStrs = redisTemplate.opsForHash().values(redisKey)
                .stream()
                .map(o -> Objects.toString(o, null))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(couponStrs)) {
            saveEmptyCouponListToCache(userId, Collections.singletonList(status));
            return Collections.emptyList();
        }
        return couponStrs.stream()
                .map(cs -> JSON.parseObject(cs, Coupon.class))
                .collect(Collectors.toList());
    }

    /**
     * 保存空的优惠券列表到缓存中
     * 目的： 避免缓存穿透
     * @param userId
     * @param status
     */
    @Override
    public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {

        log.info("Save Empty List To Cache For User: {}, Status: {}", userId, JSON.toJSONString(status));

        //key 是 Coupon_id, value 是序列化的 Coupon
        Map<String, String> invalidCouponMap = new HashMap<>();
        invalidCouponMap.put("-1", JSON.toJSONString(Coupon.invalidCoupon()));

        //用户优惠券缓存信息
        //KV
        //K： status -> redisKey
        //V: {coupon_id: 序列化的 Coupon}

        // 使用 SessionCallback 把数据命令放入到 Redis 的 pipeline
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public  Object execute(RedisOperations operations) throws DataAccessException {

                status.forEach(s -> {
                    String redisKey = status2RedisKey(s, userId);
                    operations.opsForHash().putAll(redisKey, invalidCouponMap);
                });

                return null;
            }
        };
        log.info("Pipeline Exe Result: {}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));

    }

    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE, templateId.toString());
        // 因为优惠券码不存在顺序关系， 左边 pop 或 右边 pop， 没有影响
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);

        log.info("Acquire Coupon Code: {}, {}, {}",
                templateId, redisKey, couponCode);
        return couponCode;
    }

    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException {

        log.info("Add Coupon To Cache: {}, {}, {}", userId, JSON.toJSONString(coupons), status);

        Integer result = -1;

        CouponStatus couponStatus = CouponStatus.of(status);

        switch (couponStatus) {
            case USABLE:
                result = addCouponToCacheForUsable(userId, coupons);
                break;
            case USED:
                ;
                break;
            case EXPIRED:
                break;
        }
        return null;
    }

    /**
     * 新增加优惠券到 Cache 中
     * @param userId
     * @param coupons
     * @return
     */
    private Integer addCouponToCacheForUsable(Long userId, List<Coupon> coupons) {

        // 如果 status 是 USABLE， 代表是新增加的优惠券
        // 只会影响一个 Cache： USER_COUPON_USABLE
        log.debug("Add Coupon To Cache For Usable.");

        Map<String, String> needCachedObject = new HashMap<>();
        coupons.forEach(c -> needCachedObject.put(
                c.getId().toString(),JSON.toJSONString(c)
        ));

        String redisKey = status2RedisKey(
                CouponStatus.USABLE.getCode(), userId);
        redisTemplate.opsForHash().putAll(redisKey, needCachedObject);
        log.info("Add {} Coupon To Cache: {}, {}", needCachedObject.size(), userId, redisKey);

        redisTemplate.expire(redisKey, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);

        return needCachedObject.size();
    }

    /**
     * 根据 status 获取到对应的 Redis Key
     * @param status
     * @param userId
     * @return
     */
    private String status2RedisKey(Integer status, Long userId) {

        String redisKey = null;
        CouponStatus couponStatus = CouponStatus.of(status);

        switch (couponStatus) {
            case USABLE: redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USABLE, userId);
            break;
            case USED: redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USED, userId);
            break;
            case EXPIRED: redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_EXPIRED, userId);
            break;
        }
        return redisKey;
    }

    /**
     * 获取一个随机的过期时间
     * 缓存雪崩： key 在同一时间失效
     * @param min 最小的小时数
     * @param max 最大的小时数
     * @return 返回 [min, max] 之间的随机秒数
     */
    private Long getRandomExpirationTime(Integer min, Integer max) {

        return RandomUtils.nextLong(min * 60 *60 ,max * 60 * 60);
    }
}
