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
 * 排列三返奖
 */
@Service
public class Pl3CalculationServiceImpl implements BonusCalculationService {

    @Autowired
    private JpaBatch jpaBatch;

    @Autowired
    private SsqBonusCalculationServiceImpl ssqBonusCalculationService;

    @Autowired
    private Shx115CalculationServiceImpl shx115CalculationService;

    @Override
    public void calculation(SsqBonusCalculation bonusCalculation) {
    }

    public void pl3Calculation(List<BettingTicket> ticketList, String openResult) {
        String[] reBall = openResult.split(CommonConstant.COMMA_SPLIT_STR);

        ticketList.forEach(ticket -> {
            ticket.setOpenPrizeStatus(2);
            ticket.setBonusTime(new Date());
            String lotteryNumber = ticket.getLotteryNumber();
            String[] lotteryNumbers = lotteryNumber.split(CommonConstant.PERCENT_SPLIT_STR + "|" + CommonConstant.COMMON_AT_STR);

            String[] lotteryBalls = lotteryNumbers[1].split(CommonConstant.SPACE_SPLIT_STR);
            for (int i = 0; i < lotteryBalls.length; i++) {
                ticket = calcResultBonus(ticket, lotteryBalls[i], reBall);
            }
            ticket.setBonus(ticket.getBonus().multiply(new BigDecimal(ticket.getTimes())));
        });

        jpaBatch.batchUpdate(ticketList);
        List<Long> ids = ticketList.stream().map(e -> Long.valueOf(e.getTicketId())).collect(Collectors.toList());

        ssqBonusCalculationService.updateOrderStatus(ids);
        shx115CalculationService.refundBonus(ids);
    }

    private BettingTicket calcResultBonus(BettingTicket ticket, String lotteryBalls, String[] reBall) {
        String[] balls = lotteryBalls.split(CommonConstant.SEMICOLON_SPLIT_STR);
        if(!ticket.getPrizeStatus().equals(Integer.valueOf(1))) {
            ticket.setPrizeStatus(2);
        }

        Map<String, Object> resultBonusMap = isCalcBonus(reBall, balls, ticket.getPlayType(), ticket.getExtra());
        Integer isBonus = (Integer)resultBonusMap.get("isBonus");
        if(isBonus.equals(1)) {
            ticket.setPrizeStatus(isBonus);
            ticket.setReturnPrizeStatus(1);
            BigDecimal bonus = (BigDecimal)resultBonusMap.get("bonus");
            ticket.setBonus(ticket.getBonus().add(bonus.multiply(new BigDecimal(100))));
        }
        return ticket;
    }

    public Map<String, Object> isCalcBonus(String[] reBall, String[] balls, String playType, String extra) {
        Map<String, Object> resultBonusMap = new HashMap<>();
        Integer isBonus = new Integer(0);
        BigDecimal bonus = BigDecimal.ZERO;

        if(playType.equals(WordConstant.SINGLE)) {

            if(extra.equals(AoliEnum.P3DIRECT.getStatus())) {
                resultBonusMap = calcResultBonus(reBall, balls, extra);
                isBonus = Integer.valueOf(resultBonusMap.get("isBonus").toString());
                bonus = bonus.add(new BigDecimal(resultBonusMap.get("bonus").toString()));

            }else if(extra.equals(AoliEnum.GROUP6.getStatus())) {
                resultBonusMap = calcResultBonus(reBall, balls[0].split(CommonConstant.COMMA_SPLIT_STR), extra);
                isBonus = Integer.valueOf(resultBonusMap.get("isBonus").toString());
                bonus = bonus.add(new BigDecimal(resultBonusMap.get("bonus").toString()));

            }

        }else if(playType.equals(WordConstant.MULTIPLE)) {
            if(extra.equals(AoliEnum.GROUP3.getStatus()) ||
                    extra.equals(AoliEnum.GROUP6.getStatus())) {

                int ballsCount = 2;
                if(extra.equals(AoliEnum.GROUP6.getStatus())){
                    ballsCount = 3;
                }



                String[] loopBall = FcSsqGame.loop(balls[0].split(CommonConstant.COMMA_SPLIT_STR), ballsCount);
                for (int i1 = 0; i1 < loopBall.length; i1++) {
                    Map<String, Object> resultMap = calcResultBonus(reBall, loopBall[i1].split(CommonConstant.COMMA_SPLIT_STR), extra);
                    if(Integer.valueOf(resultMap.get("isBonus").toString()).equals(Integer.valueOf(1))) {
                        isBonus = Integer.valueOf(resultMap.get("isBonus").toString());
                        bonus = bonus.add(new BigDecimal(resultMap.get("bonus").toString()));
                    }
                }

            }else {
                String[] numberOne = balls[0].split(CommonConstant.COMMA_SPLIT_STR);
                String[] numberTwo = balls[1].split(CommonConstant.COMMA_SPLIT_STR);
                String[] numberThree = balls[2].split(CommonConstant.COMMA_SPLIT_STR);
                for (int i1 = 0; i1 < numberOne.length; i1++) {
                    for (int i2 = 0; i2 < numberTwo.length; i2++) {
                        for (int i3 = 0; i3 < numberThree.length; i3++) {
                            String[] ball = new String[3];
                            ball[0] = numberOne[i1];
                            ball[1] = numberTwo[i2];
                            ball[2] = numberThree[i3];
                            Map<String, Object> resultMap = calcResultBonus(reBall, ball, extra);
                            if(Integer.valueOf(resultMap.get("isBonus").toString()).equals(Integer.valueOf(1))) {
                                isBonus = Integer.valueOf(resultMap.get("isBonus").toString());
                                bonus = bonus.add(new BigDecimal(resultMap.get("bonus").toString()));
                            }
                        }
                    }
                }

            }

        }
        resultBonusMap.put("isBonus", isBonus);
        resultBonusMap.put("bonus", bonus);
        return resultBonusMap;
    }

    /**
     * 计算返奖
     * @param reBall
     * @param lotteryBall
     * @param extra
     * @return
     */
    private Map<String, Object> calcResultBonus(String[] reBall, String[] lotteryBall, String extra) {

        Map<String, Object> resultBonusMap = new HashMap<>();
        resultBonusMap.put("isBonus", 0);
        resultBonusMap.put("bonus", BigDecimal.ZERO);
        //排3直选	投注号与中奖号相同且顺序一致
        if(extra.equals(AoliEnum.P3DIRECT.getStatus())) {
            if(reBall[0].equals(lotteryBall[0]) && reBall[1].equals(lotteryBall[1]) && reBall[2].equals(lotteryBall[2])) {
                resultBonusMap.put("isBonus", 1);
                resultBonusMap.put("bonus", new BigDecimal(AoliEnum.P3DIRECT.getCode()));
            }

        }else if(extra.equals(AoliEnum.GROUP3.getStatus())) {
            //组选三	中奖号任意两位相同 投注号与中奖号相同且顺序不限

            if(isRepeatNumber(reBall)) {
                if((lotteryBall[0].equals(reBall[0]) || lotteryBall[0].equals(reBall[1]) || lotteryBall[0].equals(reBall[2])) &&
                        (lotteryBall[1].equals(reBall[0]) || lotteryBall[1].equals(reBall[1]) || lotteryBall[1].equals(reBall[2]))) {

                    resultBonusMap.put("isBonus", 1);
                    resultBonusMap.put("bonus", new BigDecimal(AoliEnum.GROUP3.getCode()));
                }

            }

        }else if(extra.equals(AoliEnum.GROUP6.getStatus())) {

            //组选六	中奖号三位各不相同 投注号与中奖号相同且顺序不限
            if(!isRepeatNumber(reBall)) {
                System.out.println((lotteryBall[0].equals(reBall[0]) || lotteryBall[0].equals(reBall[1]) || lotteryBall[0].equals(reBall[2])));
                System.out.println((lotteryBall[1].equals(reBall[0]) || lotteryBall[1].equals(reBall[1]) || lotteryBall[1].equals(reBall[2])));
                System.out.println((lotteryBall[2].equals(reBall[0]) || lotteryBall[2].equals(reBall[1]) || lotteryBall[2].equals(reBall[2])));
                if((lotteryBall[0].equals(reBall[0]) || lotteryBall[0].equals(reBall[1]) || lotteryBall[0].equals(reBall[2])) &&
                        (lotteryBall[1].equals(reBall[0]) || lotteryBall[1].equals(reBall[1]) || lotteryBall[1].equals(reBall[2])) &&
                        (lotteryBall[2].equals(reBall[0]) || lotteryBall[2].equals(reBall[1]) || lotteryBall[2].equals(reBall[2]))) {

                    resultBonusMap.put("isBonus", 1);
                    resultBonusMap.put("bonus", new BigDecimal(AoliEnum.GROUP6.getCode()));
                }
            }
        }

        return resultBonusMap;
    }

    /**
     * 判断中奖号有重复
     * @param redBall
     * @return
     */
    private boolean isRepeatNumber(String[] redBall) {
        Set<String> setArray = new HashSet<>();

        for (int i = 0; i < redBall.length; i++) {
            setArray.add(redBall[i]);
        }

        if(setArray.size() == redBall.length) {
            return false;
        }else {
            return true;
        }
    }
}
