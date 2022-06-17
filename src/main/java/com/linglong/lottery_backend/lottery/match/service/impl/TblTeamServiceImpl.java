package com.linglong.lottery_backend.lottery.match.service.impl;

import com.linglong.lottery_backend.lottery.match.entity.TblTeam;
import com.linglong.lottery_backend.lottery.match.repository.TblTeamRepository;
import com.linglong.lottery_backend.lottery.match.service.MatchRelevantRedisService;
import com.linglong.lottery_backend.lottery.match.service.TblTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class TblTeamServiceImpl implements TblTeamService {

    @Autowired
    private TblTeamRepository repository;

    @Autowired
    private MatchRelevantRedisService matchRelevantRedisService;

    @Override
    public TblTeam findTeamById(Long team_id) {
        Optional<TblTeam> t = repository.findById(team_id);
        if (t.isPresent()) {
            return repository.findById(team_id).get();
        }
        return null;
    }

}
