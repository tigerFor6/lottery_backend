package com.linglong.lottery_backend.lottery.area.service.impl;

import com.linglong.lottery_backend.lottery.area.entity.TblLotteryArea;
import com.linglong.lottery_backend.lottery.area.repository.TblLotteryAreaRepository;
import com.linglong.lottery_backend.lottery.area.service.TblLotteryAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TblLotteryAreaServiceImpl implements TblLotteryAreaService {

    @Autowired
    private TblLotteryAreaRepository repository;

    @Override
    public List<TblLotteryArea> getgetLottery() {
        Sort sort = new Sort(Sort.Direction.DESC, "level");
        return repository.findAll(sort);
    }
}
