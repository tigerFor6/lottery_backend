package com.linglong.lottery_backend.prize.service.impl;

import com.linglong.lottery_backend.prize.entity.TblMatchResults;
import com.linglong.lottery_backend.prize.repository.TblMatchResultsRepository;
import com.linglong.lottery_backend.prize.service.TblMatchResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TblMatchResultsServiceImpl implements TblMatchResultsService {

	@Autowired
	private TblMatchResultsRepository repository;

	@Override
	public int countByMatchId(Long matchId) {
		return repository.countByMatchId(matchId);
	}

	@Override
	public List<TblMatchResults> findByMatchId(Long matchId) {
		return repository.findByMatchId(matchId);
	}
}
