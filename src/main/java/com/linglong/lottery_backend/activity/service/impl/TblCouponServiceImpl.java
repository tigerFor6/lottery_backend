package com.linglong.lottery_backend.activity.service.impl;

import com.linglong.lottery_backend.activity.constant.ActivityConstant;
import com.linglong.lottery_backend.activity.entity.TblActivityRelation;
import com.linglong.lottery_backend.activity.entity.TblCoupon;
import com.linglong.lottery_backend.activity.repository.TblActivityRelationRepository;
import com.linglong.lottery_backend.activity.repository.TblCouponRepository;
import com.linglong.lottery_backend.activity.service.TblCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TblCouponServiceImpl implements TblCouponService {

	@Autowired
	private TblCouponRepository repository;

	@Autowired
	private TblActivityRelationRepository activityRelationRepository;

	@Override
	public List<TblCoupon> findByActivityId(Long activityId) {
		List<TblCoupon> coupons = new LinkedList<>();
		List<TblActivityRelation> activityRelations = activityRelationRepository.findByActivityId(activityId);
		if (activityRelations.isEmpty()){
			return coupons;
		}
		Set<Long> couponId = new HashSet<>();
		activityRelations.forEach(e->{
			if (e.getRelationType().equals(ActivityConstant.activity_coupon_type)){
				couponId.add(e.getRelationId());
			}
		});
		if (couponId.size() == 0){
			return coupons;
		}
		coupons = repository.findByCouponIdIn(couponId.toArray(new Long[couponId.size()]));
		return coupons;
	}
}
