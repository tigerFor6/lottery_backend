package com.linglong.lottery_backend.user.account.service;


import com.linglong.lottery_backend.user.account.entity.TblUserBalance;
import com.linglong.lottery_backend.listener.bean.CommonRecord;
import com.linglong.lottery_backend.lottery.match.common.Result;

import java.math.BigDecimal;
import java.util.List;

public interface TblUserBalanceService {

    Result recharge(String userId, BigDecimal amount);

    void refund(String userId, BigDecimal amount, String voucher_no, String orderId);

    Result reward(String userId, Long voucherNo, BigDecimal amount);

    TblUserBalance findByUserId(String userId);

    Result deduction(String userId, BigDecimal amount, Long order_id, Long userCouponId, Integer gameType);

    void batchReward(List<CommonRecord> commonRecords);

    void withdrawalReturn(String userId, BigDecimal amount,long relateRecordId);

//    Result exceptionOrderRefound(String userId,BigDecimal amount,String orderId);

}
