package com.linglong.lottery_backend.activity.repository;


import com.linglong.lottery_backend.activity.entity.TblUserCouponRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TblUserCouponRecordRepository extends JpaRepository<TblUserCouponRecord, Long>{

    TblUserCouponRecord findByVoucherNoAndType(Long orderId, Integer type);
}
