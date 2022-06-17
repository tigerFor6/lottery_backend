package com.linglong.lottery_backend.prize.service.impl;


import com.linglong.lottery_backend.prize.entity.TblBettingTicketDetails;
import com.linglong.lottery_backend.prize.repository.TblBettingTicketDetailsRepository;
import com.linglong.lottery_backend.prize.service.TblBettingTicketDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class TblBettingTicketDetailsServiceImpl implements TblBettingTicketDetailsService {

	@Autowired
	private TblBettingTicketDetailsRepository repository;

	@Override
	public List<TblBettingTicketDetails> findByMatchId(Long matchId) {
		return repository.findByMatchId(matchId);
	}

	@Override
	public List<TblBettingTicketDetails> findByOrderIds(LinkedList<Long> orderIds) {
		return repository.findByOrderIdIn(orderIds);
	}

	@Override
	public List<TblBettingTicketDetails> findByTicketIds(List<Long> ticketIds) {
		return repository.findByBettingTicketIdIn(ticketIds);
	}

	@Override
	public List<TblBettingTicketDetails> findByTicketId(Long ticketId) {
		return repository.findByBettingTicketId(ticketId);
	}

	@Override
	public List<TblBettingTicketDetails> findByMatchIssue(String matchIssue) {
		return repository.findByMatchIssue(matchIssue);
	}
}
