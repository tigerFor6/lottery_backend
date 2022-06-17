package com.linglong.lottery_backend.activity.service.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.linglong.lottery_backend.activity.entity.TblActivity;
import com.linglong.lottery_backend.activity.entity.TblCoupon;
import com.linglong.lottery_backend.activity.entity.TblUserCoupon;
import com.linglong.lottery_backend.activity.repository.TblActivityRepository;
import com.linglong.lottery_backend.activity.rules.CouponRules;
import com.linglong.lottery_backend.activity.service.TblActivityService;
import com.linglong.lottery_backend.activity.service.TblCouponService;
import com.linglong.lottery_backend.activity.service.TblUserCouponService;
import com.linglong.lottery_backend.common.bean.LockKey;
import com.linglong.lottery_backend.utils.IdWorker;
import com.linglong.redisson.configure.annotations.LockAction;
import com.linglong.redisson.configure.util.LockType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TblActivityServiceImpl implements TblActivityService {

	@Autowired
	private TblActivityRepository repository;

	@Autowired
	private TblCouponService couponService;

//	@Autowired
//	private TblActivityRelationService activityRelationService;

//	@Autowired
//	private TblUserActivityService userActivityService;

	@Autowired
	private TblUserCouponService userCouponService;
//
//	@Autowired
//	private TblCouponCountService couponCountService;

	@Autowired
	private IdWorker idWorker;

	@Override
	public TblActivity findTblActivityByActivityId(Long activityId) {
		return repository.findByActivityId(activityId);
	}

	@Override
	@Transactional
	@LockAction(key = LockKey.lockActivity, value = "#activity.activityId", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
	public void participateInActivity(TblActivity activity, Long userId) {

		List<TblCoupon> coupons = couponService.findByActivityId(activity.getActivityId());
		if (coupons.isEmpty()){
			//抛出异常
			//未查询到活动下的关联
			throw new RuntimeException("活动配置未完成");
		}
		//List<TblUserCoupon> userCoupons = new LinkedList<>();
		List<TblUserCoupon> userCoupons = coupons.stream()
				.filter(e -> e.getEnabled())
				.filter(e -> verificationRule(e, userId))
				.map(e->{
					TblUserCoupon userCoupon = new TblUserCoupon();
					userCoupon.setUserId(userId);
					userCoupon.setCouponId(e.getCouponId());
					userCoupon.setCouponAmount(e.getAmount());
					userCoupon.setCouponName(e.getName());
					userCoupon.setCouponType(e.getType());
					String [] ruleArr = e.getRules().split("@");
					Set<String> ruleSet = new HashSet<>();
					for (String rules:ruleArr){
						String[] rule = rules.split(":");
						if (rule[0].startsWith("10")){
							continue;
						}
						String[] timeRule  = null;
						if (rule[0].equals(CouponRules.use004)){
							timeRule = rule[1].split(",");
							Date startTime = DateUtils.addDays(new Date(),Integer.parseInt(timeRule[0]));
							Date endTime = DateUtils.addDays(startTime,Integer.parseInt(timeRule[1]));
							userCoupon.setStartTime(com.linglong.lottery_backend.activity.util.DateUtils.transToDate(startTime,0));
							System.out.println("endTime:"+com.linglong.lottery_backend.activity.util.DateUtils.transToDate(endTime,-1));
							userCoupon.setEndTime(com.linglong.lottery_backend.activity.util.DateUtils.transToDate(endTime,-1));
							//continue;
						}else if (rule[0].equals(CouponRules.use003)){
							timeRule = rule[1].split(",");
							userCoupon.setStartTime(new Date(Long.parseLong(timeRule[0])));
							userCoupon.setEndTime(new Date(Long.parseLong(timeRule[1])));
							//continue;
						}
						ruleSet.add(rules);
					}
					userCoupon.setUserCouponId(idWorker.nextId());
					userCoupon.setActivityId(activity.getActivityId());
					userCoupon.setCouponRules(Joiner.on("@").join(ruleSet));
					return userCoupon;
				}).collect(Collectors.toList());
		if (userCoupons.isEmpty()){
			throw new RuntimeException("红包已领完了");
		}
		userCouponService.saveAll(userCoupons);
	}

	private boolean verificationRule(TblCoupon e,Long userId) {
		if (StringUtils.isBlank(e.getRules())){
			return false;
		}
		Iterable<String> ruleArr = Splitter.on("@").omitEmptyStrings().split(e.getRules());
		for (String rules:ruleArr){
				String[] rule = rules.split(":");
				if (rule[0].equals(CouponRules.draw001)){
					//红包总数
					int total = e.getNumber().intValue();
					String [] countArr = rule[1].split(",");
					//红包允许领取次数
					int drawFrequency = Integer.parseInt(countArr[0]);
					//红包允许领取个数
					int drawNumber = Integer.parseInt(countArr[1]);
					//用户领取红包总个数
					Integer drawCount = userCouponService.findDrawCouponCountByCouponId(e.getCouponId());
					//用户需要领取的个数
					int userTotal = drawFrequency*drawNumber;
					//判断红包总数是否小余用户要领取个数
					if ((total-drawCount.intValue())<userTotal){
						return false;
					}
					//用户领取个数
					Integer count = userCouponService.findUserCouponCountByCouponIdAndUserId(e.getCouponId(),userId);
					int userDrawFrequency = count.intValue()/drawNumber;
					if (userDrawFrequency>=drawFrequency){
						return false;
					}
				}
		}
		return true;
	}

	@Override
	public List<TblActivity> findParticipateList() {
		//List<TblActivity> list = repository.findParticipateList();
		return repository.findParticipateList();
	}

	@Override
	public List<TblCoupon> findTblCouponsByActivityId(Long activityId) {
		return couponService.findByActivityId(activityId);
	}


	public static void main(String[] args) {
		Date startTime = DateUtils.addDays(new Date(),0);
		Date endTime = DateUtils.addDays(startTime,3);
		System.out.println(com.linglong.lottery_backend.activity.util.DateUtils.transToDate(endTime,-1));
	}
}
