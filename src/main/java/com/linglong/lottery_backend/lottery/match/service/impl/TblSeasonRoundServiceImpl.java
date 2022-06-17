package com.linglong.lottery_backend.lottery.match.service.impl;

import com.linglong.lottery_backend.lottery.match.repository.TblSeasonRoundRepository;
import com.linglong.lottery_backend.lottery.match.service.TblSeasonRoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class TblSeasonRoundServiceImpl implements TblSeasonRoundService {

	@Autowired
	private TblSeasonRoundRepository repository;
}
