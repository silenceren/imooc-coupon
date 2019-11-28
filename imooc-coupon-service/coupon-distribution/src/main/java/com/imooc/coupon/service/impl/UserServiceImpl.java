package com.imooc.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.dao.CouponDao;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.feign.SettlementClient;
import com.imooc.coupon.feign.TemplateClient;
import com.imooc.coupon.service.IRedisService;
import com.imooc.coupon.service.IUserService;
import com.imooc.coupon.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: imooc-coupon
 * @description: 用户服务相关的接口实现
 * 所有的操作过程，状态都保存在 Redis 中， 并通过 Kafka 把消息传递到 MySQL 中
 * @author: tianwei
 * @create: 2019-11-26 16:03
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    private final CouponDao couponDao;

    private final IRedisService redisService;

    /** 模板微服务客户端 */
    private final TemplateClient templateClient;

    /** 结算微服务客户端 */
    private final SettlementClient settlementClient;

    /** Kafka 客户端 */
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public UserServiceImpl(CouponDao couponDao, IRedisService redisService, TemplateClient templateClient, SettlementClient settlementClient, KafkaTemplate<String, String> kafkaTemplate) {
        this.couponDao = couponDao;
        this.redisService = redisService;
        this.templateClient = templateClient;
        this.settlementClient = settlementClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 根据用户 id 和状态查询优惠券记录
     * @param userId
     * @param status
     * @return
     * @throws CouponException
     */
    @Override
    public List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException {

        List<Coupon> curCached = redisService.getCachedCoupons(userId, status);
        List<Coupon> preTarget;

        if (CollectionUtils.isNotEmpty(curCached)) {
            log.debug("coupon cache is not empty: {}, {}", userId, status);
            preTarget = curCached;
        } else {
            log.debug("coupon cache is empty, get coupon from db: {}, {}", userId, status);
            List<Coupon> dbCoupons = couponDao.findAllByUserIdAndStatus(userId, CouponStatus.of(status));

            //如果数据库中没有记录，直接返回就可以，Cache 中已经加入了一张无效的优惠券
            if (CollectionUtils.isEmpty(dbCoupons)) {
                log.debug("current user do not have coupon: {}, {}", userId, status);
                return dbCoupons;
            }
            // 填充 dbCoupons 的 templateSDK 字段
            Map<Integer, CouponTemplateSDK> id2TemplateSDK = templateClient.findIds2TemplateSDK(dbCoupons.stream()
                    .map(Coupon::getTemplateId)
                    .collect(Collectors.toList())).getData();

            dbCoupons.forEach( dc -> dc.setTemplateSDK(id2TemplateSDK.get(dc.getTemplateId())));

            // 数据库中存在记录
            preTarget = dbCoupons;
            // 将记录写入 Cache
            redisService.addCouponToCache(userId, preTarget, status);
        }

        // 将无效优惠券剔除
        preTarget = preTarget.stream()
                .filter(c -> c.getId() != -1)
                .collect(Collectors.toList());
        // 如果当前获取的是可用优惠券，还需要对已过期的优惠券做延迟处理
        if (CouponStatus.of(status) == CouponStatus.USABLE) {
            CouponClassify classify = CouponClassify.classify(preTarget);
            // 如果已过期状态不为空，需要做延迟处理
            if (CollectionUtils.isNotEmpty(classify.getExpired())) {
                log.info("Add Expired Coupon To Cache From FindCouponsByStatus: {}, {}", userId, status);
                redisService.addCouponToCache(userId, classify.getExpired(), CouponStatus.EXPIRED.getCode());

                // 发送到 kafka 中做异步处理
                kafkaTemplate.send(Constant.TOPIC, JSON.toJSONString(new CouponKafkaMessage(CouponStatus.EXPIRED.getCode(),
                        classify.getExpired().stream()
                                .map(Coupon::getId)
                                .collect(Collectors.toList()))));
            }
            return classify.getUsable();
        }
        return preTarget;
    }

    /** 根据用户 id 查找当前可以领取的优惠券模板
     *  @param userId
     *  @return
     *  @throws CouponException */
    @Override
    public List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException {

        long curTime = new Date().getTime();
        List<CouponTemplateSDK> templateSDKS = templateClient.findAllUsableTemplate().getData();

        log.debug("Find All Template(From TemplateClient) Count: {}", templateSDKS.size());

        // 过滤过期的优惠券模板
        templateSDKS = templateSDKS.stream()
                .filter(t -> t.getRule().getExpiration().getDeadline() > curTime)
                .collect(Collectors.toList());

        log.info("Find Usable Template Count: {}", templateSDKS.size());

        // key 是 TemplateId
        // value 中的 left 是 Template limitation， right 是优惠券模板
        Map<Integer, Pair<Integer, CouponTemplateSDK>> limit2Template = new HashMap<>(templateSDKS.size());
        templateSDKS.forEach(t -> limit2Template.put(t.getId(), Pair.of(t.getRule().getLimitation(), t)));

        List<CouponTemplateSDK> result = new ArrayList<>(limit2Template.size());
        List<Coupon> userUsableCoupons = findCouponsByStatus(userId, CouponStatus.USABLE.getCode());

        log.debug("Current User Has Usable Coupons: {}, {}", userId, userUsableCoupons.size());
        Map<Integer, List<Coupon>> templateId2Coupons = userUsableCoupons
                .stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));

        // 根据 Template 的 Rule 判断是否可以领取优惠券模板
        limit2Template.forEach((k, v) -> {

            int limitation = v.getLeft();
            CouponTemplateSDK templateSDK = v.getRight();

            if (templateId2Coupons.containsKey(k) && templateId2Coupons.get(k).size() >= limitation) {
                return ;
            }

            result.add(templateSDK);
        });

        return result;

    }

    /**
     * 用户领取优惠券
     * @param request
     * @return
     * @throws CouponException
     */
    @Override
    public Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException {
        return null;
    }

    /**
     * 结算（核销）优惠券
     * @param info
     * @return
     * @throws CouponException
     */
    @Override
    public SettlementInfo settlement(SettlementInfo info) throws CouponException {
        return null;
    }
}
