package com.linglong.lottery_backend.user.transaction.service.impl;

import com.linglong.lottery_backend.user.transaction.repository.GlobalConfigurationRepository;
import com.linglong.lottery_backend.user.transaction.service.GlobalConfigurationService;
import com.linglong.lottery_backend.user.cashier.entity.GlobalConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GlobalConfigurationServiceImpl implements GlobalConfigurationService {

    @Autowired
    private GlobalConfigurationRepository globalConfigurationRepository;

    @Override
    public GlobalConfiguration findByCode(String code) {
        return globalConfigurationRepository.findByCode(code);
    }
}
