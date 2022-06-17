package com.linglong.lottery_backend.ticket.repository;


import com.linglong.lottery_backend.prize.entity.BettingTicketDigital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TblBettingTicketDigitalRepository extends JpaRepository<BettingTicketDigital, Long> {

    List<BettingTicketDigital> findByPeriodAndBettingType(Integer period, Integer lotteryType);

    List<BettingTicketDigital> findByBettingTicketId(@Param("bettingTicketId") Long bettingTicketId);

    List<BettingTicketDigital> findByBettingTicketIdIn(List<Long> ticketIds);
}
