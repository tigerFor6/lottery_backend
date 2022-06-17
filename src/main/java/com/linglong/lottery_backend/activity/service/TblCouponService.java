package com.linglong.lottery_backend.activity.service;

import com.linglong.lottery_backend.activity.entity.TblCoupon;

import java.util.List;

public interface TblCouponService  {

    List<TblCoupon> findByActivityId(Long activityId);
}
