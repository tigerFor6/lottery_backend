package com.linglong.lottery_backend.lottery.digital.repository;

import com.linglong.lottery_backend.lottery.digital.entity.TblDigitalResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TblDigitalResultsRepository extends JpaRepository<TblDigitalResults, Long>{

    @Transactional(propagation = Propagation.SUPPORTS)
    @Query(value = "select * from tbl_digital_results where lottery_type = :lotteryType ORDER BY lottery_time DESC LIMIT 0,1 ", nativeQuery = true)
    TblDigitalResults findRecentTblDigitalResults(@Param("lotteryType")  Integer lotteryType);

    @Query(value = "select * from tbl_digital_results s where  s.lottery_type = :lotteryType and s.id in (select max(r.id) from tbl_digital_results r where r.lottery_type = :lotteryType GROUP BY r.period) and s.lottery_result <> '' ORDER BY s.lottery_time DESC LIMIT 0,10", nativeQuery = true)
    List<TblDigitalResults> findHistoryTblDigitalResults(@Param("lotteryType")  Integer lotteryType);

    TblDigitalResults findFirstByOrderByIdDesc();

    List<TblDigitalResults> findByPeriodAndLotteryType(Integer period, Integer lotteryType);

    List<TblDigitalResults> findByPeriod(Integer period);

    /**
     * 根据lottery_type查询最新的一条数据
     * @param lotteryType
     * @return
     */
    TblDigitalResults findFirstBylotteryTypeOrderByIdDesc(Integer lotteryType);

    /**
     * 根据ID修改result
     * @param lotteryResult
     * @param id
     * @return
     */
    @Transactional
    @Modifying
    @Query("UPDATE tbl_digital_results SET lottery_result = :lotteryResult WHERE id= :id")
    int updateLotteryResultById(@Param("lotteryResult") String lotteryResult, @Param("id")Long id);
}
