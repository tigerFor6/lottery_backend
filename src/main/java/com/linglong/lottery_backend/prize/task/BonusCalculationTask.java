package com.linglong.lottery_backend.prize.task;

import com.linglong.lottery_backend.prize.bean.AbstractBonusCalculation;
import com.linglong.lottery_backend.prize.bean.part.SsqBonusCalculation;
import com.linglong.lottery_backend.prize.service.BonusCalculationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BonusCalculationTask {

    @Autowired
    @Qualifier(value = "ssqBonusCalculationService")
    BonusCalculationService ssqBonusCalculationService;

    @Async("taskExecutor")
    public void doBonusCalculation(AbstractBonusCalculation calculation) {

        if (calculation instanceof SsqBonusCalculation){
            //双色球开奖计算
            SsqBonusCalculation ssq  = (SsqBonusCalculation) calculation;
            ssqBonusCalculationService.calculation(ssq);
        }
    }
}
