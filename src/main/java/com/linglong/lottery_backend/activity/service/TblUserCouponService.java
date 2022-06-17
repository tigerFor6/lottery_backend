package com.linglong.lottery_backend.activity.service;

import com.linglong.lottery_backend.activity.entity.TblUserCoupon;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface TblUserCouponService  {

    Integer findDrawCouponCountByCouponId(Long couponId);

    Integer findUserCouponCountByCouponIdAndUserId(Long couponId,Long userId);

    void saveAll(List<TblUserCoupon> userCoupons);

    Page<TblUserCoupon> findListByParam(TblUserCoupon param, String status, int page, int size);

    int countExpireByUserId(Long userId);

    List<TblUserCoupon> findAllowedToUseCoupon(Long userId, BigDecimal amount, Integer gameType);

    int useCoupon(Long userId, Long userCouponId, Long orderId, BigDecimal amount, Integer gameType);

    Boolean compensateCoupon(Long userCouponId, BigDecimal amount, Long voucherNo) throws CloneNotSupportedException;

    int countByUserIdAndCouponStatus(Long userId, Integer status);

    void updateInvalidCoupon();

    List<TblUserCoupon> findByActivityIdAndUserId(Long activityId, Long userId);

    TblUserCoupon findFirstByUserCouponId(Long userCouponId);

    List<TblUserCoupon> findExpireCoupon();
}
