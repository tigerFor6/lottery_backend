package com.linglong.lottery_backend.user.transaction.repository;

import com.linglong.lottery_backend.user.cashier.entity.GlobalConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalConfigurationRepository extends JpaRepository<GlobalConfiguration, Long> {

    GlobalConfiguration findByCode(String code);
}
