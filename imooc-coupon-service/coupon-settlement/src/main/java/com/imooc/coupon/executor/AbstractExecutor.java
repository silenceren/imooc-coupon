package com.imooc.coupon.executor;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.vo.GoodsInfo;
import com.imooc.coupon.vo.SettlementInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: imooc-coupon
 * @description: 规则执行器抽象类，定义通用方法
 * @author: tianwei
 * @create: 2019-12-09 16:54
 */

public abstract class AbstractExecutor {

    /**
     * 校验商品类型与优惠券是否匹配
     * 需要注意
     * 1 这里实现的单品类优惠券的校验，多品类优惠券重载此方法
     * 2 商品只需要有一个优惠券要求的商品类型去匹配就可以
     * @param settlement
     * @return
     */
    @SuppressWarnings("all")
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement) {

        List<Integer> goodsType = settlement.getGoodsInfos()
                .stream().map(GoodsInfo::getType)
                .collect(Collectors.toList());
        List<Integer> templateGoodsType = JSON.parseObject(settlement.getCouponAndTemplateInfos().get(0).getTemplate().getRule().getUsage().getGoodsType(), List.class);

        //存在交集即可
        return CollectionUtils.isNotEmpty(CollectionUtils.intersection(goodsType, templateGoodsType));
    }

    /**
     * 处理商品类型与优惠券限制不匹配的情况
     * @param settlementInfo 用户传递的结算信息
     * @param goodsSum 商品总价
     * @return
     */
    protected SettlementInfo processGoodsTypeNotSatisfy(SettlementInfo settlementInfo, double goodsSum) {

        boolean isGoodsTypeSatisfy = isGoodsTypeSatisfy(settlementInfo);

        if (!isGoodsTypeSatisfy) {
            settlementInfo.setCost(goodsSum);
            settlementInfo.setCouponAndTemplateInfos(Collections.emptyList());
            return settlementInfo;
        }
        return null;
    }

    /**
     * 商品总价
     */
    protected double goodsCostSum(List<GoodsInfo> goodsInfos) {
        return goodsInfos.stream().mapToDouble(g -> g.getPrice() * g.getCount()).sum();
    }

    /**
     * 保留两位小数
     */
    protected double retain2Decimals(double value) {
        return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 最小支付费用
     */
    protected double minCost() {
        return 0.1;
    }
}
