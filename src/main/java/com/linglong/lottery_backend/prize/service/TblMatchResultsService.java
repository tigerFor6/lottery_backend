package com.linglong.lottery_backend.prize.service;

import com.linglong.lottery_backend.prize.entity.TblMatchResults;

import java.util.List;

public interface TblMatchResultsService {

    int countByMatchId(Long matchId);

    List<TblMatchResults> findByMatchId(Long matchId);
}
