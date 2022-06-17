package com.linglong.lottery_backend.activity.service.impl;

import com.linglong.lottery_backend.activity.repository.TblActivityRelationRepository;
import com.linglong.lottery_backend.activity.service.TblActivityRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TblActivityRelationServiceImpl implements TblActivityRelationService {

	@Autowired
	private TblActivityRelationRepository repository;
}
