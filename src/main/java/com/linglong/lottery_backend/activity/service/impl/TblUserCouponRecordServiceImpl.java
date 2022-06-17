package com.linglong.lottery_backend.activity.service.impl;


import com.linglong.lottery_backend.activity.entity.TblUserCouponRecord;
import com.linglong.lottery_backend.activity.repository.TblUserCouponRecordRepository;
import com.linglong.lottery_backend.activity.service.TblUserCouponRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TblUserCouponRecordServiceImpl implements TblUserCouponRecordService {

	@Autowired
	private TblUserCouponRecordRepository repository;

	@Override
	public void save(TblUserCouponRecord record) {
		repository.save(record);
	}

	@Override
	public TblUserCouponRecord findByVoucherNoAndType(Long orderId, Integer type) {
		return repository.findByVoucherNoAndType(orderId,type);
	}


}
