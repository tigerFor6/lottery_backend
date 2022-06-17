package com.linglong.lottery_backend.activity.service.impl;

import com.google.common.base.Splitter;
import com.linglong.lottery_backend.activity.entity.TblUserCoupon;
import com.linglong.lottery_backend.activity.entity.TblUserCouponRecord;
import com.linglong.lottery_backend.activity.repository.TblUserCouponRepository;
import com.linglong.lottery_backend.activity.rules.CouponRules;
import com.linglong.lottery_backend.activity.service.TblUserCouponRecordService;
import com.linglong.lottery_backend.activity.service.TblUserCouponService;
import com.linglong.lottery_backend.activity.util.DateUtils;
import com.linglong.lottery_backend.common.bean.LockKey;
import com.linglong.lottery_backend.utils.IdWorker;
import com.linglong.redisson.configure.annotations.LockAction;
import com.linglong.redisson.configure.util.LockType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TblUserCouponServiceImpl implements TblUserCouponService {

	@Autowired
	TblUserCouponRepository repository;

	@Autowired
	IdWorker idWorker;

	@Autowired
	TblUserCouponRecordService userCouponRecordService;

	@Override
	public Integer findDrawCouponCountByCouponId(Long couponId) {
		return repository.countByCouponId(couponId);
	}

	@Override
	public Integer findUserCouponCountByCouponIdAndUserId(Long couponId,Long userId) {
		return repository.countByCouponIdAndUserId(couponId,userId);
	}

	@Override
	public void saveAll(List<TblUserCoupon> userCoupons) {
		repository.saveAll(userCoupons);
	}

	@Override
	public Page<TblUserCoupon> findListByParam(TblUserCoupon param, String status, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "id"));
//		ExampleMatcher matcher = ExampleMatcher.matching()
//				.withIgnoreNullValues()
//				.withIgnorePaths("id");
//		Example<TblUserCoupon> example = Example.of(param,matcher);
//		return repository.findAll(example,pageable);

		return repository.findAll(new Specification<TblUserCoupon>(){
			@Override
			public Predicate toPredicate(Root<TblUserCoupon> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> list = new LinkedList<>();
				if (null != param.getUserId()){
					list.add(criteriaBuilder.equal(root.get("userId").as(Long.class), param.getUserId()));
				}
				if(null != param.getActivityId()){
					list.add(criteriaBuilder.equal(root.get("activityId").as(Long.class), param.getActivityId()));
				}
				if (StringUtils.isNotBlank(status)){
					List<Predicate> orPredicates = new LinkedList<>();
					for (String sta : Splitter.on(",").split(status)){
						orPredicates.add(criteriaBuilder.equal(root.get("couponStatus").as(Integer.class), Integer.parseInt(sta)));
					}
					list.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()])));
				}
				Predicate[] p = new Predicate[list.size()];
				return criteriaBuilder.and(list.toArray(p));
			}
		},pageable);
	}

	@Override
	public int countExpireByUserId(Long userId) {
		return repository.countExpireByUserId(userId);
	}

	@Override
	public List<TblUserCoupon> findAllowedToUseCoupon(Long userId, BigDecimal amount, Integer gameType) {
		return repository.findAllowedToUseCoupon(userId)
				.stream()
				.filter(e -> amount.compareTo(e.getCouponAmount()) != -1)
				.filter( e-> verificationRule(e.getCouponRules(),amount,gameType))
				.sorted(Comparator.comparing(TblUserCoupon::getEndTime))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	@LockAction(key = LockKey.lockUserCoupon, value = "#userCouponId", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
	public int useCoupon(Long userId, Long userCouponId, Long orderId, BigDecimal amount, Integer gameType) {
		TblUserCoupon userCoupon = repository.findFirstByUserCouponId(userCouponId);
		if (null == userCoupon){
			//红包不存在
			return 100001;
		}
		if (!userCoupon.getUserId().equals(userId)){
			//红包使用人不正确
			return 100002;
		}
		if (userCoupon.getCouponStatus().intValue() == 1){
			//红包状态已使用
			return 100003;
		}
		if (userCoupon.getCouponStatus().intValue() == 2){
			//红包状态已过期
			return 100004;
		}
		TblUserCouponRecord userCouponRecord = userCouponRecordService.findByVoucherNoAndType(orderId,1);
		if (null != userCouponRecord){
			//红包已使用
			return 100003;
		}
		if (!DateUtils.belongCalendar(new Date(),userCoupon.getStartTime(),userCoupon.getEndTime())){
			//红包不再可使用范围
			return 100005;
		}
		if (amount.compareTo(userCoupon.getCouponAmount()) == -1){
			//实际订单金额小于红包金额
			return 100006;
		}
		if (!verificationRule(userCoupon.getCouponRules(),amount,gameType)){
			//未通过红包规则校验
			return 100007;
		}
		try {
			//更新红包状态
			userCoupon.setCouponStatus(1);;
			repository.save(userCoupon);

			//添加操作记录  后续修改为切面操作
			TblUserCouponRecord record = new TblUserCouponRecord();
			record.setAmount(userCoupon.getCouponAmount());
			record.setUserCouponId(userCoupon.getUserCouponId());
			record.setCouponType(userCoupon.getCouponType());
			record.setUserId(userId);
			record.setVoucherNo(orderId);
			record.setType(1);
			record.setRecordId(idWorker.nextId());
			record.setCouponId(userCoupon.getCouponId());
			userCouponRecordService.save(record);
			return 100000;
		}catch (Exception e){
			log.error("useCoupon {}",e);
		}
		//异常终止
		return 100007;
	}

	@Override
	public Boolean compensateCoupon(Long userCouponId, BigDecimal amount, Long voucherNo) throws CloneNotSupportedException {
		TblUserCouponRecord userCouponRecord = userCouponRecordService.findByVoucherNoAndType(voucherNo,2);
		if (null == userCouponRecord){
			TblUserCoupon userCoupon = repository.findFirstByUserCouponId(userCouponId);
			if (null == userCoupon){
				return false;
			}
			TblUserCoupon newCoupon = (TblUserCoupon) userCoupon.clone();

			//更改红包类型
			newCoupon.setCouponType(1);
			newCoupon.setUserCouponId(idWorker.nextId());
			//id 置空？ 防止更新
			newCoupon.setId(null);
			newCoupon.setVersion(Long.parseLong("0"));
			newCoupon.setCouponAmount(amount);
			newCoupon.setCouponStatus(0);
			repository.save(newCoupon);

			//添加操作记录  后续修改为切面操作
			TblUserCouponRecord record = new TblUserCouponRecord();
			record.setAmount(newCoupon.getCouponAmount());
			record.setUserCouponId(newCoupon.getUserCouponId());
			record.setCouponType(newCoupon.getCouponType());
			record.setUserId(newCoupon.getUserId());
			record.setVoucherNo(voucherNo);
			record.setType(2);
			record.setRecordId(idWorker.nextId());
			record.setCouponId(newCoupon.getCouponId());
			userCouponRecordService.save(record);

			return true;
		}
		return false;
	}

	@Override
	public int countByUserIdAndCouponStatus(Long userId, Integer status) {
		return repository.countByUserIdAndCouponStatus(userId,status);
	}

	@Override
	public void updateInvalidCoupon() {
		repository.updateInvalidCoupon();
	}

	@Override
	public List<TblUserCoupon> findByActivityIdAndUserId(Long activityId, Long userId) {
		return repository.findByActivityIdAndUserId(activityId,userId);
	}

	@Override
	public TblUserCoupon findFirstByUserCouponId(Long userCouponId) {
		return repository.findFirstByUserCouponId(userCouponId);
	}

	@Override
	public List<TblUserCoupon> findExpireCoupon() {
		return repository.findExpireCoupon();
	}

	private boolean verificationRule(String couponRules, BigDecimal amount, Integer gameType) {
		//"20001:600@20002:2,3,4@20002:2,3,4"
		Iterable<String> ruleArr =Splitter.on("@").omitEmptyStrings().split(couponRules);
		for (String rules : ruleArr){
			String[] rule = rules.split(":");
			if (rule[0].equals(CouponRules.use001)){
				if (amount.compareTo(new BigDecimal(rule[1])) == -1){
					return false;
				}
			}
			if (rule[0].equals(CouponRules.use002)){
				if (!Arrays.asList(rule[1].split(",")).contains(gameType.toString())){
					return false;
				};
			}
		}
		return true;
	}
}
