package com.linglong.lottery_backend.user.transaction.service;

import com.linglong.lottery_backend.user.cashier.entity.GlobalConfiguration;

public interface GlobalConfigurationService {

    GlobalConfiguration findByCode(String code);
}
