package com.imooc.coupon.executor.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.executor.AbstractExecutor;
import com.imooc.coupon.executor.RuleExecutor;
import com.imooc.coupon.vo.GoodsInfo;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: imooc-coupon
 * @description: 满减 + 折扣优惠券结算规则执行器
 * @author: tianwei
 * @create: 2019-12-10 16:26
 */
@Slf4j
@Component
public class ManJianZheKouExecutor extends AbstractExecutor implements RuleExecutor {

    /**
     * 规则类型标记
     * @return
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN_ZHEKOU;
    }

    /**
     * 校验商品类型与优惠券是否匹配
     * 需要注意
     * 1 这里实现的满减 + 折扣优惠券的校验
     * 2 如果想要使用多类优惠券，则必须要所有的商品类型都包含在内，即差集为空
     * @param settlement {@link SettlementInfo} 用户传递的结算信息
     * @return
     */
    @Override
    @SuppressWarnings("all")
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement) {

        log.debug("Check ManJian And ZheKou Is Match Or Not!");
        List<Integer> goodsType = settlement.getGoodsInfos().stream().map(GoodsInfo::getType).collect(Collectors.toList());
        List<Integer> templateGoodsType = new ArrayList<>();
        settlement.getCouponAndTemplateInfos().forEach(ct -> {
            templateGoodsType.addAll(JSON.parseObject(ct.getTemplate().getRule().getUsage().getGoodType(), List.class));
        });

        // 如果想要使用多类优惠券，则必须要所有的商品类型都包含在内，即差集为空
        return CollectionUtils.isEmpty(CollectionUtils.subtract(goodsType, templateGoodsType));
    }

    /**
     * 优惠券规则计算
     * @param settlement {@link SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的结算信息
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        double goodsSum = retain2Decimals(goodsCostSum(settlement.getGoodsInfos()));
        //　商品类型校验
        SettlementInfo probability = processGoodsTypeNotSatisfy(settlement, goodsSum);
        if (null != probability) {
            log.debug("ManJian And ZheKou Template Is Not Match To GoodsType!");
            return probability;
        }

        SettlementInfo.CouponAndTemplateInfo manJian = null;
        SettlementInfo.CouponAndTemplateInfo zheKou = null;

        for (SettlementInfo.CouponAndTemplateInfo ct : settlement.getCouponAndTemplateInfos()) {
            if (CouponCategory.of(ct.getTemplate().getCategory()) == CouponCategory.MANJIAN) {
                manJian = ct;
            } else {
                zheKou = ct;
            }
        }

        assert null != manJian;
        assert null != zheKou;

        // 当前的优惠券和满减券如果不能共用（一起使用），清空优惠券，返回商品原价
        if (!isTemplateCanShared(manJian, zheKou)) {
            log.debug("Current ManJian And ZheKou Can Not Shared!");
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }

        List<SettlementInfo.CouponAndTemplateInfo> ctInfos = new ArrayList<>();
        double manJianBase = (double) manJian.getTemplate().getRule().getDiscount().getBase();
        double manJianQuota = (double) manJian.getTemplate().getRule().getDiscount().getQuota();

        //最终的价格 先计算满减
        double targetSum = goodsSum;
        if (targetSum >= manJianBase) {
            targetSum -= manJianQuota;
            ctInfos.add(manJian);
        }

        // 再计算折扣
        double zheKouQuota = (double) zheKou.getTemplate().getRule().getDiscount().getQuota();
        targetSum *= zheKouQuota * 1.0 / 100;
        ctInfos.add(zheKou);

        settlement.setCouponAndTemplateInfos(ctInfos);
        settlement.setCost(retain2Decimals(Math.max(targetSum, minCost())));

        log.debug("Use ManJian And ZheKou Coupon Make Goods Cost From {} To {}", goodsSum, settlement.getCost());

        return settlement;
    }

    @SuppressWarnings("all")
    private boolean isTemplateCanShared(SettlementInfo.CouponAndTemplateInfo manJian, SettlementInfo.CouponAndTemplateInfo zheKou) {

        String manjianKey = manJian.getTemplate().getKey() + String.format("%04d", manJian.getTemplate().getId());
        String zhekouKey = zheKou.getTemplate().getKey() + String.format("%04d", zheKou.getTemplate().getId());

        List<String> allSharedKeyKeysForManjian = new ArrayList<>();
        allSharedKeyKeysForManjian.add(manjianKey);
        allSharedKeyKeysForManjian.addAll(JSON.parseObject(manJian.getTemplate().getRule().getWeight(), List.class));

        List<String> allSharedKeyKeysForZhekou = new ArrayList<>();
        allSharedKeyKeysForZhekou.add(zhekouKey);
        allSharedKeyKeysForZhekou.addAll(JSON.parseObject(zheKou.getTemplate().getRule().getWeight(), List.class));

        return CollectionUtils.isSubCollection(Arrays.asList(manjianKey, zhekouKey), allSharedKeyKeysForManjian)
                || CollectionUtils.isSubCollection(Arrays.asList(manjianKey, zhekouKey), allSharedKeyKeysForZhekou);

    }
}
