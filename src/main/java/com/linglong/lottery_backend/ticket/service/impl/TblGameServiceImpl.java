package com.linglong.lottery_backend.ticket.service.impl;

import com.linglong.lottery_backend.ticket.repository.TblGameRepository;
import com.linglong.lottery_backend.ticket.service.TblGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TblGameServiceImpl implements TblGameService {

    @Autowired
    private TblGameRepository repository;
}
