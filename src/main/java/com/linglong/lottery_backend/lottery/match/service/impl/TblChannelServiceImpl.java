package com.linglong.lottery_backend.lottery.match.service.impl;

import com.linglong.lottery_backend.lottery.match.entity.TblChannel;
import com.linglong.lottery_backend.lottery.match.repository.TblChannelRepository;
import com.linglong.lottery_backend.lottery.match.service.TblChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TblChannelServiceImpl implements TblChannelService {

    @Autowired
    private TblChannelRepository repository;

    @Override
    public TblChannel findByChannelCode(String channel) {
        return repository.findByChannelCode(channel);
    }
}
