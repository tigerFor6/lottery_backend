package com.linglong.lottery_backend.ticket.service.impl;


import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import com.linglong.lottery_backend.ticket.repository.TblBettingTicketRepository;
import com.linglong.lottery_backend.ticket.service.TblBettingTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TblBettingTicketServiceImpl implements TblBettingTicketService {

    @Autowired
    private TblBettingTicketRepository repository;

//    @Override
//    public List<BettingTicket> findAllByIds(List<Long> ids) {
//
//        return repository.findAllById(ids);
//    }

    @Override
    public List<BettingTicket> findByOrderId(Long orderId) {
        return repository.findByOrderId(orderId);
    }

    @Override
    public List<BettingTicket> findAllByTicketIds(List<Long> ticketIds) {
        return repository.findAllByTicketIdIn(ticketIds);
    }

    @Override
    public void updateBatchTicket(List<BettingTicket> tickets) {
        if (tickets.isEmpty()) {
            return;
        }
        repository.saveAll(tickets);
    }

    @Override
    public List<BettingTicket> findAllByOrderIds(List<Long> orderIds) {
        return repository.findAllByOrderIdIn(orderIds);
    }

    @Override
    public List<BettingTicket> findByTicketStatus(Integer status) {
        return repository.findByTicketStatus(status);
    }

    @Override
    public List<BettingTicket> findByTicketStatusAndPrizeStatus(Integer ticketStatus, Integer prizeStatus) {
        return repository.findByTicketStatusAndPrizeStatus(ticketStatus,prizeStatus);
    }

    @Override
    public BettingTicket findByTicketId(Long ticketId) {
        return repository.findByTicketId(ticketId);
    }

    @Override
    public boolean existsByOrderId(Long orderId) {
        return repository.existsByOrderId(orderId);
    }

    @Override
    public Page<BettingTicket> findAllByExample(Integer pageNum, Integer pageSize, BettingTicket ticket) {
        Example<BettingTicket> example = Example.of(ticket);
        Pageable pageable = PageRequest.of(pageNum,pageSize);
        return repository.findAll(example,pageable);
    }

    @Override

    public void updateBettingTicket(BettingTicket bettingTicket) {
        repository.saveAndFlush(bettingTicket);
    }

    @Override
    public List<BettingTicket> findByPlatformIdAndTicketStatus(Integer platformId, Integer status) {
        return repository.findByPlatformIdAndTicketStatus(platformId,status);
    }

    @Override
    public List<BettingTicket> findByPeriodAndGameType(Integer period, Integer lotteryType) {
        return repository.findByPeriodAndGameType(period,lotteryType);
    }

    @Override
    public List<BettingTicket> findByPeriodAndGameTypeAndTicketStatus(Integer period, Integer lotteryType, Integer ticketStatus) {
        return repository.findByPeriodAndGameTypeAndTicketStatus(period,lotteryType,ticketStatus);
    }

    @Override
    public void saveAll(List<BettingTicket> tickets) {
        repository.saveAll(tickets);
    }

}
