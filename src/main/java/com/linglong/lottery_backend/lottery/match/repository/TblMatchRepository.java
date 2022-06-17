package com.linglong.lottery_backend.lottery.match.repository;

import com.linglong.lottery_backend.lottery.match.entity.TblMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TblMatchRepository extends JpaRepository<TblMatch, Long>, JpaSpecificationExecutor {

    @Query(value = "select * from tbl_match where match_type = :matchType and match_status = :matchStatus and  match_sn != '' order by match_time asc", nativeQuery = true)
    List<TblMatch> findMatchListByMatchTypeAndMatchStatusAndMatchSnNotEmpty(@Param("matchType") Integer matchType, @Param("matchStatus") Integer matchStatus);

    List<TblMatch> findMatchListByMatchTypeAndMatchStatus(Integer matchType, Integer matchStatus);

    @Query(value = "select min(match_time) from tbl_match where id in :ids ", nativeQuery = true)
    Date getMinMatchTime(@Param("ids") List<Long> ids);

    @Query(value = "select * from tbl_match where match_type = :matchType and match_status = :matchStatus and  match_sn != '' and hot_status = 1 order by match_time asc", nativeQuery = true)
    List<TblMatch> findMatchListByMatchTypeAndMatchStatusAndMatchSnNotEmptyAAndHotStatus(@Param("matchType") Integer matchType, @Param("matchStatus") Integer matchStatus);
}
