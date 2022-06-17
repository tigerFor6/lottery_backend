package com.linglong.lottery_backend.activity.repository;

import com.linglong.lottery_backend.activity.entity.TblUserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TblUserActivityRepository extends JpaRepository<TblUserActivity, Long>{
    TblUserActivity findByUserIdAndActivityId(Long userId, Long activeId);

    int countByActivityIdAndUserId(Long activityId, Long userId);

    TblUserActivity findByUserIdAndActivityType(Long userId, Integer type);

    List<TblUserActivity> findUserActivityByUserIdAndActivityTypeAndStatus(Long userId, Integer type, Integer status);

    int countByUserIdAndActivityType(Long userId, Integer type);
}
