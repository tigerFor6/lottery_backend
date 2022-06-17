package com.linglong.lottery_backend.lottery.match.service;

import com.linglong.lottery_backend.lottery.match.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface MatchRelevantRedisService {


    void putTblMatchById(TblMatch tblMatch);

    void putTblMatchHotById(TblMatch tblMatch);

    void putTblSeasonById(TblSeason tblSeason);

    void putTblSeasonRoundById(TblSeasonRound tblSeasonRound);

    void putTblSeasonTeamById(TblSeasonTeam tblSeasonTeam);

    void putTblMatchOddsById(TblMatchOdds tblMatchOdds);


    TblMatch getTblMatchById(Long id);

    TblSeason getTblSeasonById(Long id);

    TblSeasonRound getTblSeasonRoundById(Long id);

    TblSeasonTeam getTblSeasonTeamById(Long id);

    TblMatchOdds getTblMatchOddsById(Long id);

    TblTeam getTblTeamById(Long teamId);

    void putTblTeamById(TblTeam tblTeam);

    Set<String> getTblMatchOddsByMatchId(Long matchId);

    void putTblMatchOddsByMatchId(List<TblMatchOdds> result);

    Set<String> getIndexTblMatchListByMatchType(Integer matchType);

    Set<String> getIndexTblMatchHotListByMatchType(Integer matchType);

    void putIndexTblMatchListByMatchType(List<TblMatch> result);

    void putIndexTblMatchHotListByMatchType(List<TblMatch> result);

//    void putTxTblMatchOddsByMatchId(Long matchId);
//
//    boolean getTxTblMatchOddsByMatchId(Long matchId);

    boolean getTxKey(String s);

    void putTxKey(String s);

    void updateMatchRedis(Integer matchType, Integer matchStatus);

    void updateHotMatchRedis(Integer matchType, Integer matchStatus);

}
