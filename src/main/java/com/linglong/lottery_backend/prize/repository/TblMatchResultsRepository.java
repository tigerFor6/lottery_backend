package com.linglong.lottery_backend.prize.repository;

import com.linglong.lottery_backend.prize.entity.TblMatchResults;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TblMatchResultsRepository extends JpaRepository<TblMatchResults, Integer>{
    int countByMatchId(Long matchId);

    List<TblMatchResults> findByMatchId(Long matchId);

}
