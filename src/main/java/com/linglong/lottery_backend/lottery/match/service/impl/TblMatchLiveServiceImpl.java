package com.linglong.lottery_backend.lottery.match.service.impl;

import com.linglong.lottery_backend.lottery.match.repository.TblMatchLiveRepository;
import com.linglong.lottery_backend.lottery.match.service.TblMatchLiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TblMatchLiveServiceImpl implements TblMatchLiveService {

    @Autowired
    private TblMatchLiveRepository repository;

}
