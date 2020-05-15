package com.imooc.coupon.dao;

import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponDao extends JpaRepository<Coupon, Integer> {

    /**
     * 根据 userId + 状态寻找优惠券记录
     * where userId = ... and status = ...
     *  */
    List<Coupon> findAllByUserIdAndStatus(Long userId, CouponStatus status);

    // 根据 userId 寻找优惠券记录
    List<Coupon> findAllByUserId(Long userId);

}
