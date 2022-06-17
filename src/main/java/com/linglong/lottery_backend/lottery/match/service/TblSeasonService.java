package com.linglong.lottery_backend.lottery.match.service;


import com.linglong.lottery_backend.lottery.match.entity.TblSeason;

public interface TblSeasonService {

    TblSeason findSeasonById(Long seasonId);
}
