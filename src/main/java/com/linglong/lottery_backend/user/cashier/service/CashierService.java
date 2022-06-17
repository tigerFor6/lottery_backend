package com.linglong.lottery_backend.user.cashier.service;

import com.linglong.lottery_backend.user.cashier.entity.CashierBody;
import com.linglong.lottery_backend.user.cashier.entity.CashierNewBody;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Map;

public interface CashierService {
    String cashierSign(String hrefUrl, String userId) throws UnsupportedEncodingException;

    void updateOrderStatus(CashierBody body);

    Map<String,Object> payOrder(String returnUrl, BigDecimal amount, String code, String userId) throws UnsupportedEncodingException;

    String queryOrder(Long recordNo) throws UnsupportedEncodingException;

    void updateChargeOrderStatus(CashierNewBody body);

   // void updateRecordStatus(WithdrawalEntity entity);
}
