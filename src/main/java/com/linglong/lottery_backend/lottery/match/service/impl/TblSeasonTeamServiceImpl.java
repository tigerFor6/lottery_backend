package com.linglong.lottery_backend.lottery.match.service.impl;

import com.linglong.lottery_backend.lottery.match.entity.TblSeasonTeam;
import com.linglong.lottery_backend.lottery.match.repository.TblSeasonTeamRepository;
import com.linglong.lottery_backend.lottery.match.service.MatchRelevantRedisService;
import com.linglong.lottery_backend.lottery.match.service.TblSeasonTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TblSeasonTeamServiceImpl implements TblSeasonTeamService {

    @Autowired
    private TblSeasonTeamRepository repository;

    @Autowired
    private MatchRelevantRedisService matchRelevantRedisService;

    @Override
    public TblSeasonTeam findSeasonTeamBySeasonIdAndTeamId(Long seasonId, Long teamId) {
        return repository.findSeasonTeamBySeasonIdAndTeamId(seasonId, teamId);
    }

    @Override
    public TblSeasonTeam findSeasonTeamById(Long seasonTeamId) {
        Optional<TblSeasonTeam> t = repository.findById(seasonTeamId);
        if (t.isPresent()) {
            return repository.findById(seasonTeamId).get();
        }
        return null;
    }

//    @Override
//    public TblSeasonTeam findById(Long id) {
//        Optional<TblSeasonTeam> t = repository.findById(id);
//        if (t.isPresent()) {
//            return repository.findById(id).get();
//        }
//        return null;
//    }

}
