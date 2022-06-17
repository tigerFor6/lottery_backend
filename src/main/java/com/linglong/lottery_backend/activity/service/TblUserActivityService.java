package com.linglong.lottery_backend.activity.service;

import com.linglong.lottery_backend.activity.entity.TblUserActivity;

import java.util.List;

public interface TblUserActivityService  {

    TblUserActivity findUserActivityByUserIdAndActivityId(Long userId, Long activeId);

    TblUserActivity findUserActivityByUserIdAndActivityType(Long userId, Integer type);

    void saveUserActivity(TblUserActivity userActivity);

    int countByActivityIdAndUserId(Long activityId, Long userId);

    List<TblUserActivity> findUserActivityByUserIdAndActivityTypeAndStatus(Long userId, Integer type, Integer status);

    int countByUserIdAndActivityType(Long userId, Integer type);
}
