package com.linglong.lottery_backend.prize.service.impl;

import com.linglong.lottery_backend.prize.entity.TblBettingTicketGroup;
import com.linglong.lottery_backend.prize.repository.TblBettingTicketGroupRepository;
import com.linglong.lottery_backend.prize.service.TblBettingTicketGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TblBettingTicketGroupServiceImpl implements TblBettingTicketGroupService {

	@Autowired
	private TblBettingTicketGroupRepository repository;

	@Override
	public List<TblBettingTicketGroup> findByTicketIds(List<Long> ticketIds) {
		return repository.findByBettingTicketIdIn(ticketIds);
	}

	@Override
	public List<TblBettingTicketGroup> findByTicketId(Long ticketId) {
		return repository.findByBettingTicketId(ticketId);
	}
}
