package com.linglong.lottery_backend.lottery.match.service.impl;

import com.linglong.lottery_backend.lottery.match.entity.TblSeason;
import com.linglong.lottery_backend.lottery.match.repository.TblSeasonRepository;
import com.linglong.lottery_backend.lottery.match.service.TblSeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TblSeasonServiceImpl implements TblSeasonService {

    @Autowired
    private TblSeasonRepository repository;

    @Override
    public TblSeason findSeasonById(Long seasonId) {
        Optional<TblSeason> t = repository.findById(seasonId);
        if (t.isPresent()) {
            return repository.findById(seasonId).get();
        }
        return null;
    }

}
