package com.linglong.lottery_backend.user.cashier.service;

import com.linglong.lottery_backend.user.cashier.entity.WithdrawalEntity;

import java.math.BigDecimal;

public interface SubstitutePayService {

    void substituteToAccount(long accountId, String userId, BigDecimal amount);

    void getResponse(WithdrawalEntity entity);

    void verifiedRequest(Long orderNo,String verifiedResult);
}
