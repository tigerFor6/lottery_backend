package com.linglong.lottery_backend.lottery.match.repository;

import com.linglong.lottery_backend.lottery.match.entity.TblMatchOdds;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TblMatchOddsRepository extends JpaRepository<TblMatchOdds, Long> {
    List<TblMatchOdds> findByMatchId(Long matchId);

//    @Query(value = "select * from tbl_match_odds as a \n" +
//            "where id = (select max(b.id) \n" +
//            "from tbl_match_odds as b \n" +
//            "where b.match_id = :match_id and a.play_code = b.play_code)", nativeQuery = true)
//    List<TblMatchOdds> findByMatchIdGroupByPlayCode(@Param("match_id") Long match_id);
}
