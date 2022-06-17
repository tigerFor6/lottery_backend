package com.linglong.lottery_backend.lottery.match.service;

import com.linglong.lottery_backend.lottery.match.entity.TblMatchHot;

import java.util.List;

public interface TblMatchHotService {
    List<TblMatchHot> tblMatchHot();
    Integer deleteMatchHot();
}
