package com.linglong.lottery_backend.lottery.match.service;

import com.linglong.lottery_backend.lottery.match.entity.TblMatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface TblMatchService {

    List<TblMatch> findMatchListByMatchTypeAndMatchStatus(Integer matchType, Integer matchStatus);

    List<TblMatch> findMatchListByMatchTypeAndMatchStatusUpdateRedis(Integer matchType, Integer matchStatus);

    List<TblMatch> findMatchHotListByMatchTypeAndMatchStatusUpdateRedis(Integer matchType, Integer matchStatus);

    List<TblMatch> findBasketballMatchListByMatchTypeAndMatchStatus(Integer matchType, Integer matchStatus);

    TblMatch findByIdUpdateRedis(Long id);

    TblMatch findMatchById(Long matchId);

    List<TblMatch> findMatchByIds(ArrayList<Long> longs);

    void testLock(TblMatch tblMatch);

    Date getMinMatchTime(List<Long> ids);

    Date getMatchEndTime(Integer matchType,Date matchTime);

    List<TblMatch> findMatchListByMatchTypeAndMatchStatusAndHotStatus(Integer matchType, Integer matchStatus);
}
