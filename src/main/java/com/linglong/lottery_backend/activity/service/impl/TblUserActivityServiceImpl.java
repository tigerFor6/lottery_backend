package com.linglong.lottery_backend.activity.service.impl;

import com.linglong.lottery_backend.activity.entity.TblUserActivity;
import com.linglong.lottery_backend.activity.repository.TblUserActivityRepository;
import com.linglong.lottery_backend.activity.service.TblUserActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TblUserActivityServiceImpl implements TblUserActivityService {

	@Autowired
	private TblUserActivityRepository repository;

	@Override
	public TblUserActivity findUserActivityByUserIdAndActivityId(Long userId, Long activeId) {
		return repository.findByUserIdAndActivityId(userId,activeId);
	}

	@Override
	public TblUserActivity findUserActivityByUserIdAndActivityType(Long userId, Integer type) {
		return repository.findByUserIdAndActivityType(userId,type);
	}

	@Override
	public void saveUserActivity(TblUserActivity userActivity) {
		repository.save(userActivity);
	}

	@Override
	public int countByActivityIdAndUserId(Long activityId, Long userId) {
		return repository.countByActivityIdAndUserId(activityId,userId);
	}

	@Override
	public List<TblUserActivity> findUserActivityByUserIdAndActivityTypeAndStatus(Long userId, Integer type, Integer status) {
		return repository.findUserActivityByUserIdAndActivityTypeAndStatus(userId,type,status);
	}

	@Override
	public int countByUserIdAndActivityType(Long userId, Integer type) {
		return repository.countByUserIdAndActivityType(userId,type);
	}


}
