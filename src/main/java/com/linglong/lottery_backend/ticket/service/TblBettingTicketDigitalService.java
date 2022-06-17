package com.linglong.lottery_backend.ticket.service;


import com.linglong.lottery_backend.prize.entity.BettingTicketDigital;

import java.util.List;

public interface TblBettingTicketDigitalService {

    List<BettingTicketDigital> findByPeriodAndBettingType(Integer period, Integer lotteryType);

    List<BettingTicketDigital> findByBettingTicketIds(List<Long>bettingTicketIds );
}
