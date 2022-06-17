package com.linglong.lottery_backend.activity.service;


import com.linglong.lottery_backend.activity.entity.TblUserCouponRecord;

public interface TblUserCouponRecordService  {

    void save(TblUserCouponRecord record);

    TblUserCouponRecord findByVoucherNoAndType(Long orderId, Integer type);
}
