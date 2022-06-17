package com.linglong.lottery_backend.prize.service.impl;

import com.linglong.lottery_backend.common.service.JpaBatch;
import com.linglong.lottery_backend.prize.bean.part.SsqBonusCalculation;
import com.linglong.lottery_backend.prize.service.BonusCalculationService;
import com.linglong.lottery_backend.ticket.bean.welfare.FcSsqGame;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.constant.WordConstant;
import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import com.linglong.lottery_backend.ticket.enums.AoliEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 排列五返奖
 */
@Service
public class Pl5CalculationServiceImpl implements BonusCalculationService {

    @Autowired
    private JpaBatch jpaBatch;

    @Autowired
    private SsqBonusCalculationServiceImpl ssqBonusCalculationService;

    @Autowired
    private Shx115CalculationServiceImpl shx115CalculationService;

    @Override
    public void calculation(SsqBonusCalculation bonusCalculation) {
    }

    public void pl5Calculation(List<BettingTicket> ticketList, String openResult) {

        ticketList.forEach(ticket -> {
            ticket.setOpenPrizeStatus(2);
            ticket.setPrizeStatus(2);
            String lotteryNumber = ticket.getLotteryNumber();
            String[] lotteryNumbers = lotteryNumber.split(CommonConstant.COMMON_AT_STR);

            String[] lotteryBalls = lotteryNumbers[0].split(CommonConstant.SPACE_SPLIT_STR);

            for (int i = 0; i < lotteryBalls.length; i++) {
                boolean isBonus = calcBonus(lotteryBalls[i],openResult);
                if(isBonus) {
                    ticket.setPrizeStatus(1);
                    ticket.setReturnPrizeStatus(2);
                    ticket.setBigPrizeStatus(true);
//                    ticket.setBonus(new BigDecimal(10000000).multiply(new BigDecimal(ticket.getTimes())));
                }
            }

        });

        jpaBatch.batchUpdate(ticketList);
        List<Long> ids = ticketList.stream().map(e -> Long.valueOf(e.getTicketId())).collect(Collectors.toList());

        ssqBonusCalculationService.updateOrderStatus(ids);
//        shx115CalculationService.refundBonus(ids);
    }

    public boolean calcBonus(String ball, String openResult) {
        boolean isBonus = false;

        String[] balls = ball.split(CommonConstant.SEMICOLON_SPLIT_STR);
        String[] openResults = openResult.split(CommonConstant.COMMA_SPLIT_STR);

        balls_for:
        for (int i = 0; i < balls.length; i++) {
            String[] ballNum = balls[i].split(CommonConstant.COMMA_SPLIT_STR);

            for (int i1 = 0; i1 < ballNum.length; i1++) {
                if(ballNum[i1].equals(openResults[i])) {
                    isBonus = true;
                    continue balls_for;
                }
            }
            isBonus = false;
            return isBonus;
        }

        return true;
    }

}
