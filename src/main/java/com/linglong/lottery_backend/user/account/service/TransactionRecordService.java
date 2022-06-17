package com.linglong.lottery_backend.user.account.service;

import com.linglong.lottery_backend.user.account.entity.TransactionRecord;
import com.linglong.lottery_backend.user.account.entity.User;
import com.linglong.lottery_backend.user.cashier.entity.SubstituteRecord;
import com.linglong.lottery_backend.lottery.match.common.Result;
import com.linglong.lottery_backend.user.cashier.entity.TblChargeOrder;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface TransactionRecordService {
    Result record(TransactionRecord transactionRecord, String resType);

//    Map<String, Object> getrecordByUserIdPage(Long userId, Integer page_end);

    Map<String, Object> getrecordByUserIdPage(Long userId, Integer page_end);

    TransactionRecord findByVoucherNo(Long voucherNo);

    Long insertRecord(User user, String type, String status);

    Long insertRecord(Long userId, BigDecimal amount, String type, String status);

    Boolean updateRecharge(String userId, BigDecimal amount, String recordNo, String recordStatus, String thirdPartyOrderId);

    void updateBalance(String userId, TransactionRecord record, BigDecimal amount, String operation);

    TransactionRecord findByRecordNoAndThirdPartyRecordNo(String recordNo, String thirdPartyRecordNo);

    void updateRecord(String recordNo,TransactionRecord record);

    List<SubstituteRecord> getSubstituteRecord();

    TransactionRecord findByRecordNo(Long orderNo);

    Page<TransactionRecord> findListByParam(Long valueOf, Integer page, Integer size);

    Boolean updateRechargeOrder(String userId, TransactionRecord record, TblChargeOrder chargeOrder);
}
