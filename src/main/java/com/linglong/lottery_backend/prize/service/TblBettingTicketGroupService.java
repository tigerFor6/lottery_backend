package com.linglong.lottery_backend.prize.service;


import com.linglong.lottery_backend.prize.entity.TblBettingTicketGroup;

import java.util.List;

public interface TblBettingTicketGroupService {

    List<TblBettingTicketGroup> findByTicketIds(List<Long> ticketIds);

    List<TblBettingTicketGroup> findByTicketId(Long ticketId);
}
