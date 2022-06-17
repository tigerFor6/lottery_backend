package com.linglong.lottery_backend.ticket.service.impl;


import com.linglong.lottery_backend.prize.entity.BettingTicketDigital;
import com.linglong.lottery_backend.ticket.repository.TblBettingTicketDigitalRepository;
import com.linglong.lottery_backend.ticket.service.TblBettingTicketDigitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TblBettingTicketDigitalServiceImpl implements TblBettingTicketDigitalService {

    @Autowired
    private TblBettingTicketDigitalRepository repository;

    @Override
    public List<BettingTicketDigital> findByPeriodAndBettingType(Integer period, Integer lotteryType) {
        return repository.findByPeriodAndBettingType(period,lotteryType);
    }

    @Override
    public List<BettingTicketDigital> findByBettingTicketIds(List<Long> bettingTicketIds) {
        return repository.findByBettingTicketIdIn(bettingTicketIds);
    }
}
