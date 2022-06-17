package com.linglong.lottery_backend.ticket.service;


import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TblBettingTicketService {

    //List<BettingTicket> findAllByIds(List<Long> ids);

    List<BettingTicket> findByOrderId(Long orderId);

    List<BettingTicket> findAllByTicketIds(List<Long> longs);

    void updateBatchTicket(List<BettingTicket> tickets);

    List<BettingTicket> findAllByOrderIds(List<Long> orderIds);

    List<BettingTicket> findByTicketStatus(Integer valueOf);

    List<BettingTicket> findByTicketStatusAndPrizeStatus(Integer valueOf, Integer valueOf1);

    BettingTicket findByTicketId(Long ticketId);

    boolean existsByOrderId(Long orderId);

    Page<BettingTicket> findAllByExample(Integer pageNum, Integer pageSize, BettingTicket ticket);

    void updateBettingTicket(BettingTicket bettingTicket);

    List<BettingTicket> findByPlatformIdAndTicketStatus(Integer platformId, Integer status);

    List<BettingTicket> findByPeriodAndGameType(Integer period, Integer lotteryType);

    List<BettingTicket> findByPeriodAndGameTypeAndTicketStatus(Integer period, Integer lotteryType, Integer ticketStatus);

    void saveAll(List<BettingTicket> tickets);
    // List<BettingTicket> findByTicketStatusAndPlatformId(Integer platformId, Integer status);

   // void testRocketMq();
}
