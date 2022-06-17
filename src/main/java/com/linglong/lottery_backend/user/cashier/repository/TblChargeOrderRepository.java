package com.linglong.lottery_backend.user.cashier.repository;

import com.linglong.lottery_backend.user.cashier.entity.TblChargeOrder;
import com.linglong.lottery_backend.user.cashier.entity.TblPaymentConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TblChargeOrderRepository extends JpaRepository<TblChargeOrder, Long> {

    TblChargeOrder findByThirdPartyRecordNo(String thirdPartyRecordNo);

    TblChargeOrder findByRecordNo(Long recordNo);
}
