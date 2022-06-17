package com.linglong.lottery_backend.activity.service;

import com.linglong.lottery_backend.activity.entity.TblActivity;
import com.linglong.lottery_backend.activity.entity.TblCoupon;

import java.util.List;

public interface TblActivityService  {

    TblActivity findTblActivityByActivityId(Long activityId);

    void participateInActivity(TblActivity activity, Long userId);

    List<TblActivity> findParticipateList();

    List<TblCoupon> findTblCouponsByActivityId(Long activityId);
}
