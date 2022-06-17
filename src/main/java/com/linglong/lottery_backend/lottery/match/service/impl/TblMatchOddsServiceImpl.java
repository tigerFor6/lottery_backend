package com.linglong.lottery_backend.lottery.match.service.impl;

import com.linglong.lottery_backend.lottery.match.entity.TblMatchOdds;
import com.linglong.lottery_backend.lottery.match.repository.TblMatchOddsRepository;
import com.linglong.lottery_backend.lottery.match.service.TblMatchOddsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TblMatchOddsServiceImpl implements TblMatchOddsService {

    @Autowired
    private TblMatchOddsRepository repository;

//    @Autowired
//    private MatchRelevantRedisService matchRelevantRedisService;

    @Override
    public List<TblMatchOdds> findMatchOddsByMatchId(Long matchId) {
        return repository.findByMatchId(matchId);
    }


    @Override
    public TblMatchOdds findMatchOddsById(Long id) {
        Optional<TblMatchOdds> matchOdds = repository.findById(id);
        if (matchOdds.isPresent()){
            return matchOdds.get();
        }
        return null;
    }
}
