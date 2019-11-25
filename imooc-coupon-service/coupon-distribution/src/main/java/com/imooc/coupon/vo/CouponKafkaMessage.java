package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: imooc-coupon
 * @description: 优惠券 kafka 消息对象定义
 * @author: tianwei
 * @create: 2019-11-25 17:19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponKafkaMessage {

    /** 优惠券状态 */
    private Integer status;

    /** Coupon 主键 */
    private List<Integer> ids;
}
