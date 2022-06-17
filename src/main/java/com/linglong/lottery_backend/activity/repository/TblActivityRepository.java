package com.linglong.lottery_backend.activity.repository;


import com.linglong.lottery_backend.activity.entity.TblActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TblActivityRepository extends JpaRepository<TblActivity, Long>{

    TblActivity findByActivityId(Long activityId);

    @Query(value = "select id,activity_id,name,img_url,activity_url,rules,type,activity_copy,start_time,end_time,enable,created_time,updated_time,remarks,level,version from tbl_activity where level != -1 and NOW() BETWEEN start_time AND end_time", nativeQuery = true)
    List<TblActivity> findParticipateList();
}
