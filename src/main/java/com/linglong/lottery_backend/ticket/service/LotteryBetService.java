package com.linglong.lottery_backend.ticket.service;

import com.linglong.lottery_backend.ticket.bean.BetResult;
import com.linglong.lottery_backend.ticket.remote.bjgc.entity.part.Ticket;

import java.util.List;

/**
 * Created by ynght on 2019-04-26
 */
public interface LotteryBetService {
    void doBet(Long orderId);

    void sendTicket2Prize(Long orderId);

    void doBet(Integer gameType, Integer platformId, Long orderId, List<Long> ticketIds);

    List<Runnable> updateSuccessTickets(Long orderId, Integer platformId, List<BetResult> successList);

    void sendTicket2Prize(List<Ticket> tickets);
}
