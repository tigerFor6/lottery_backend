package com.linglong.lottery_backend.lottery.match.service;

import com.linglong.lottery_backend.lottery.match.entity.TblMatchOdds;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface TblMatchOddsService {
    List<TblMatchOdds> findMatchOddsByMatchId(@Param("match_id") Long match_id);

//    TblMatchOdds findById(Long valueOf);

    TblMatchOdds findMatchOddsById(Long id);
}
