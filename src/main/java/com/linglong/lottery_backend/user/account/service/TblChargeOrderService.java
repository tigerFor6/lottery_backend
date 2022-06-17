package com.linglong.lottery_backend.user.account.service;

import com.linglong.lottery_backend.user.account.entity.User;
import com.linglong.lottery_backend.user.cashier.entity.TblChargeOrder;
import com.linglong.lottery_backend.user.cashier.entity.TblPaymentConfiguration;

import java.math.BigDecimal;

public interface TblChargeOrderService {

    BigDecimal insertChargeRecord(User user, Long orderNo, String code, String recordStatus, BigDecimal amount, TblPaymentConfiguration configuration);

    TblChargeOrder findByRecordNo(Long recordNo);

    TblChargeOrder findByThirdPartyRecordNo(String thirdPartyRecordNo);

    void updateChargeRecord(TblChargeOrder record);
}
