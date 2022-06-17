package com.linglong.lottery_backend.activity.repository;


import com.linglong.lottery_backend.activity.entity.TblActivityRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TblActivityRelationRepository extends JpaRepository<TblActivityRelation, Long> {
    List<TblActivityRelation> findByActivityId(Long activityId);
}
