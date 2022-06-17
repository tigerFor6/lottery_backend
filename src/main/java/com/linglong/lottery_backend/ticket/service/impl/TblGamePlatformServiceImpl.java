package com.linglong.lottery_backend.ticket.service.impl;

import com.linglong.lottery_backend.ticket.repository.TblGamePlatformRepository;
import com.linglong.lottery_backend.ticket.service.TblGamePlatformService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class TblGamePlatformServiceImpl implements TblGamePlatformService {

    @Autowired
    private TblGamePlatformRepository repository;
}
