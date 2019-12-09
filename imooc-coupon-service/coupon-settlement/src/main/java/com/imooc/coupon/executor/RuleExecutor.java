package com.imooc.coupon.executor;

import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.vo.SettlementInfo;

/**
 * 优惠券模板规则处理器接口定义
 */
public interface RuleExecutor {

    /**
     * 规则类型标记
     * @return
     */
    RuleFlag ruleConfig();

    /**
     * 优惠券规则计算
     * @param settlement {@link SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的结算信息
     */
    SettlementInfo computeRule(SettlementInfo settlement);
}
