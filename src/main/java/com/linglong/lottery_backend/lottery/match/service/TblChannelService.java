package com.linglong.lottery_backend.lottery.match.service;


import com.linglong.lottery_backend.lottery.match.entity.TblChannel;

public interface TblChannelService {

    TblChannel findByChannelCode(String channel);
}
