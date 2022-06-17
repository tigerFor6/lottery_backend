package com.linglong.lottery_backend.ticket.service.impl;


import com.linglong.lottery_backend.ticket.repository.TblWeightRepository;
import com.linglong.lottery_backend.ticket.service.TblWeightService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class TblWeightServiceImpl implements TblWeightService {

    @Autowired
    private TblWeightRepository repository;
}
