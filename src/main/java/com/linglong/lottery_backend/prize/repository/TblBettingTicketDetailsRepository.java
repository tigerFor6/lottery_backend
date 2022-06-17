package com.linglong.lottery_backend.prize.repository;


import com.linglong.lottery_backend.order.entity.Order;
import com.linglong.lottery_backend.prize.entity.TblBettingTicketDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public interface TblBettingTicketDetailsRepository extends JpaRepository<TblBettingTicketDetails, Long> {
    List<TblBettingTicketDetails> findByMatchId(Long matchId);

    List<TblBettingTicketDetails> findByOrderIdIn(LinkedList<Long> orderIds);

    List<TblBettingTicketDetails> findByBettingTicketId(Long ticketId);

    List<TblBettingTicketDetails> findByBettingTicketIdIn(List<Long> ticketIds);

    @Query(value = "select distinct match_id from tbl_betting_ticket_details where order_id=?1 " +
            "and result='1'", nativeQuery = true)
   List<BigInteger> getSuccessMatchList(Long orderId);

    List<TblBettingTicketDetails> findByMatchIssue(String matchIssue);
}
