package com.linglong.lottery_backend.user.cashier.repository;

import com.linglong.lottery_backend.user.cashier.entity.TblPaymentConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TblPaymentConfigurationRepository extends JpaRepository<TblPaymentConfiguration, Long> {

    TblPaymentConfiguration findByCode(String code);
}
