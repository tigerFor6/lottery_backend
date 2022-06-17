package com.linglong.lottery_backend.prize.service;


import com.linglong.lottery_backend.prize.entity.TblBettingTicketDetails;

import java.util.LinkedList;
import java.util.List;

public interface TblBettingTicketDetailsService {

    List<TblBettingTicketDetails> findByMatchId(Long matchId);

    List<TblBettingTicketDetails> findByOrderIds(LinkedList<Long> ids);

    List<TblBettingTicketDetails> findByTicketIds(List<Long> ids);

    List<TblBettingTicketDetails> findByTicketId(Long k);

    List<TblBettingTicketDetails> findByMatchIssue(String matchIssue);
}
