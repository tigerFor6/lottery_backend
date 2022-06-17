package com.linglong.lottery_backend.lottery.match.repository;

import com.linglong.lottery_backend.lottery.match.entity.TblMatchHot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface TblMatchHotRepository extends JpaRepository<TblMatchHot, Long> {
    List<TblMatchHot> findByLevelNot(Integer level);

    //删除一周之前的比赛
    @Modifying
    @Transactional
    @Query(value = "delete from tbl_match_hot where  to_days(now()) - to_days(created_time) > 7", nativeQuery = true)
    Integer deleteTblMatchHotByCreatedTimeBefore();
}
