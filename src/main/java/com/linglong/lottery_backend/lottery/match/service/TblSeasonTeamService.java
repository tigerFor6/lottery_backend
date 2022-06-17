package com.linglong.lottery_backend.lottery.match.service;


import com.linglong.lottery_backend.lottery.match.entity.TblSeasonTeam;

public interface TblSeasonTeamService {
    TblSeasonTeam findSeasonTeamBySeasonIdAndTeamId(Long seasonId, Long team_id);

    //redis
    TblSeasonTeam findSeasonTeamById(Long seasonTeamId);

//    TblSeasonTeam findById(Long d);

}
