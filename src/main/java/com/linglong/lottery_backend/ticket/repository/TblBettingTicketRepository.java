package com.linglong.lottery_backend.ticket.repository;


import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface TblBettingTicketRepository extends JpaRepository<BettingTicket, Long> {
    List<BettingTicket> findTicketsByOrderId(Long orderId);

    List<BettingTicket> findByOrderId(Long orderId);

    BettingTicket findTicketsByTicketId(Long ticketId);

    List<BettingTicket> findAllByTicketIdIn(List<Long> ticketIds);

    List<BettingTicket> findAllByOrderIdIn(List<Long> orderIds);

    List<BettingTicket> findByTicketStatus(Integer status);

    List<BettingTicket> findByTicketStatusAndPrizeStatus(Integer ticketStatus, Integer prizeStatus);

    boolean existsByOrderId(Long orderId);

    BettingTicket findByTicketId(Long ticketId);

    List<BettingTicket> findByPlatformIdAndTicketStatus(@Param("platformId") Integer platformId, @Param("status") Integer status);

    List<BettingTicket> findByPeriodAndGameTypeAndTicketStatus(@Param("period") Integer period, @Param("gameType")Integer lotteryType, @Param("ticketStatus") Integer status);

    List<BettingTicket> findByTicketStatusAndPlatformId(Integer status, Integer platformId);

    /**
     * 根据ticketId批量修改状态
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "update tbl_betting_ticket set ticket_status=:ticketStatus where ticket_id in (:ticketIds)")
    int updateTicketStatusByTicketId(@Param("ticketStatus") Integer ticketStatus,  @Param("ticketIds") Collection<Long> ticketIds);

    List<BettingTicket> findByPeriodAndGameType(Integer period, Integer lotteryType);

}
