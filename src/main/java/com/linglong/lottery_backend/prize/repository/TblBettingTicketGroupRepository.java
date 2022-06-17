package com.linglong.lottery_backend.prize.repository;


import com.linglong.lottery_backend.prize.entity.TblBettingTicketGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TblBettingTicketGroupRepository extends JpaRepository<TblBettingTicketGroup, Long> {

    List<TblBettingTicketGroup> findByBettingTicketIdIn(List<Long> ticketIds);

    List<TblBettingTicketGroup> findByBettingTicketId(Long ticketId);
}
