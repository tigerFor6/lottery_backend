package com.linglong.lottery_backend.lottery.match.service.impl;

import com.linglong.lottery_backend.lottery.match.entity.TblMatchHot;
import com.linglong.lottery_backend.lottery.match.repository.TblMatchHotRepository;
import com.linglong.lottery_backend.lottery.match.service.TblMatchHotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TblMatchHotServiceImpl implements TblMatchHotService {

	@Autowired
	private TblMatchHotRepository repository;

	@Override
	public List<TblMatchHot> tblMatchHot() {
		return repository.findByLevelNot(-1);
	}

	@Override
	public Integer deleteMatchHot() {
		return repository.deleteTblMatchHotByCreatedTimeBefore();
	}
}
