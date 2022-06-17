package com.linglong.lottery_backend.prize.service.impl;

import com.linglong.lottery_backend.common.service.JpaBatch;
import com.linglong.lottery_backend.prize.bean.part.SsqBonusCalculation;
import com.linglong.lottery_backend.prize.service.BonusCalculationService;
import com.linglong.lottery_backend.ticket.bean.welfare.FcSsqGame;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.constant.WordConstant;
import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import com.linglong.lottery_backend.ticket.platform.al.AoliGameEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 大乐透返奖
 */
@Service
public class DltCalculationServiceImpl implements BonusCalculationService {

    @Autowired
    private JpaBatch jpaBatch;

    @Autowired
    private SsqBonusCalculationServiceImpl ssqBonusCalculationService;

    @Autowired
    private Shx115CalculationServiceImpl shx115CalculationService;

    @Override
    public void calculation(SsqBonusCalculation bonusCalculation) {
    }

    public void dltCalculation(List<BettingTicket> ticketList, String openResult) {
        String[] reBalls = openResult.split(CommonConstant.COMMON_COLON_STR);

        //开奖红球蓝球
        String[] reRedBall = reBalls[0].split(CommonConstant.COMMA_SPLIT_STR);
        String[] reBlueBall = reBalls[1].split(CommonConstant.COMMA_SPLIT_STR);

        ticketList.forEach(ticket -> {
            ticket.setOpenPrizeStatus(2);
            ticket.setBonusTime(new Date());
            String lotteryNumber = ticket.getLotteryNumber();
            String[] lotteryNumbers = lotteryNumber.split(CommonConstant.PERCENT_SPLIT_STR + "|" + CommonConstant.COMMON_AT_STR);

            String[] lotteryBalls = lotteryNumbers[1].split(CommonConstant.SPACE_SPLIT_STR);

            for (int lotteryCount = 0; lotteryCount < lotteryBalls.length; lotteryCount++) {
                String[] balls = lotteryBalls[lotteryCount].split("\\|");
                String[] redBall = balls[0].split(CommonConstant.SEMICOLON_SPLIT_STR);
                String[] blueBall = balls[1].split(CommonConstant.SEMICOLON_SPLIT_STR);

                //单式
                if(ticket.getPlayType().equals(WordConstant.SINGLE)) {
                    Map<String, Object> resultBonus =
                            calcResultBonus(reRedBall, reBlueBall,
                                    redBall[0].split(CommonConstant.COMMA_SPLIT_STR),
                                    blueBall[0].split(CommonConstant.COMMA_SPLIT_STR));

                    if(resultBonus.get("isBonus").equals(0)) {
                        if(!ticket.getPrizeStatus().equals(Integer.valueOf(1))) {
                            ticket.setPrizeStatus(2);
                        }
                        continue;
                    }

                    ticket.setPrizeStatus(1);
                    BigDecimal bonus = (BigDecimal) resultBonus.get("bonus");

                    if(!(Boolean)resultBonus.get("isBigBonus")) {
                        ticket.setBonus(ticket.getBonus().add(bonus.multiply(new BigDecimal(100))));
                        ticket.setReturnPrizeStatus(1);
                    }else {
                        ticket.setReturnPrizeStatus(2);
                        ticket.setBigPrizeStatus((Boolean)resultBonus.get("isBigBonus"));
                    }

                }else if(ticket.getPlayType().equals(WordConstant.MULTIPLE)) {
                    //复式
                    String[] loopRedball = FcSsqGame.loop(redBall[0].split(CommonConstant.COMMA_SPLIT_STR), 5);
                    String[] loopBlueball = FcSsqGame.loop(blueBall[0].split(CommonConstant.COMMA_SPLIT_STR), 2);

                    for (int i = 0; i < loopRedball.length; i++) {
                        for (int i1 = 0; i1 < loopBlueball.length; i1++) {
                            Map<String, Object> resultBonus =
                                    calcResultBonus(reRedBall, reBlueBall,
                                            loopRedball[i].split(CommonConstant.COMMA_SPLIT_STR),
                                            loopBlueball[i1].split(CommonConstant.COMMA_SPLIT_STR));

                            if(resultBonus.get("isBonus").equals(0)) {
                                if(!ticket.getPrizeStatus().equals(Integer.valueOf(1))) {
                                    ticket.setPrizeStatus(2);
                                }
                                continue;
                            }

                            ticket.setPrizeStatus(1);
                            BigDecimal bonus = (BigDecimal) resultBonus.get("bonus");

                            if(!(Boolean)resultBonus.get("isBigBonus")) {
                                ticket.setBonus(ticket.getBonus().add(bonus.multiply(new BigDecimal(100))));
                                ticket.setReturnPrizeStatus(1);
                            }else {
                                ticket.setReturnPrizeStatus(2);
                                ticket.setBigPrizeStatus((Boolean)resultBonus.get("isBigBonus"));
                            }
                        }
                    }

                }else if(ticket.getPlayType().equals(WordConstant.DANTUO)) {
                    //胆拖
                    String redDanBall = redBall.length > 1 ? redBall[0] : null;
                    String blueDanBall = blueBall.length > 1 ? blueBall[0] : null;

                    int redDanLen = redDanBall == null ? 0 : redDanBall.split(CommonConstant.COMMA_SPLIT_STR).length;
                    int blueDanLen = blueDanBall == null ? 0 : blueDanBall.split(CommonConstant.COMMA_SPLIT_STR).length;

                    String[] loopRedball = FcSsqGame.loop(
                            redDanBall == null ?
                                    redBall[0].split(CommonConstant.COMMA_SPLIT_STR) :
                                    redBall[1].split(CommonConstant.COMMA_SPLIT_STR), 5 - redDanLen);
                    String[] loopBlueball = FcSsqGame.loop(
                            blueDanBall == null ?
                                    blueBall[0].split(CommonConstant.COMMA_SPLIT_STR) :
                                    blueBall[1].split(CommonConstant.COMMA_SPLIT_STR), 2 - blueDanLen);

                    for (int i = 0; i < loopRedball.length; i++) {
                        for (int i1 = 0; i1 < loopBlueball.length; i1++) {
                            Map<String, Object> resultBonus =
                                    calcResultBonus(reRedBall, reBlueBall,
                                            ((redDanBall == null ? "" : redDanBall + ",") + loopRedball[i]).split(CommonConstant.COMMA_SPLIT_STR),
                                            ((blueDanBall == null ? "" : blueDanBall + ",") + loopBlueball[i1]).split(CommonConstant.COMMA_SPLIT_STR));

                            if(resultBonus.get("isBonus").equals(0)) {
                                if(!ticket.getPrizeStatus().equals(Integer.valueOf(1))) {
                                    ticket.setPrizeStatus(2);
                                }
                                continue;
                            }

                            ticket.setPrizeStatus(1);
                            BigDecimal bonus = (BigDecimal) resultBonus.get("bonus");

                            if(!(Boolean)resultBonus.get("isBigBonus")) {
                                ticket.setBonus(ticket.getBonus().add(bonus.multiply(new BigDecimal(100))));
                                ticket.setReturnPrizeStatus(1);
                            }else {
                                ticket.setBigPrizeStatus((Boolean)resultBonus.get("isBigBonus"));
                                ticket.setReturnPrizeStatus(2);
                            }
                        }
                    }

                }

            }
            if(!ticket.getBigPrizeStatus()) {
                ticket.setBonus(ticket.getBonus().multiply(new BigDecimal(ticket.getTimes())));
            }else {
                ticket.setBonus(BigDecimal.ZERO);
            }
        });

        jpaBatch.batchUpdate(ticketList);
        List<Long> ids = ticketList.stream().map(e -> Long.valueOf(e.getTicketId())).collect(Collectors.toList());

        ssqBonusCalculationService.updateOrderStatus(ids);
        shx115CalculationService.refundBonus(ids);

    }

    /**
     * 计算返奖
     * @param reRedBall
     * @param reBlueBall
     * @param redBall
     * @param blueBall
     * @return
     */
    public Map<String, Object> calcResultBonus(String[] reRedBall, String[] reBlueBall, String[] redBall, String[] blueBall) {
        int bingoRed = calc(reRedBall, redBall);
        int bingoBlue = calc(reBlueBall, blueBall);

        Map<String, Object> resultBonus = new HashMap<>();
        String bonusStr = AoliGameEnum.DLT_RESULT_BONUS_MAP.get(bingoRed+"+"+bingoBlue);
        if(bonusStr == null) {
            resultBonus.put("isBonus", 0);
            return resultBonus;
        }

        BigDecimal bonus = new BigDecimal(bonusStr);
        resultBonus.put("bonus", bonus);
        resultBonus.put("isBonus", 1);
        resultBonus.put("isBigBonus", bonus.doubleValue() >= 10000D ? true : false);

        return resultBonus;
    }

    private int calc(String[] reBall , String[] ball) {
        int bingoCount = 0;
        for (int i = 0; i < ball.length; i++) {
            for (int i1 = 0; i1 < reBall.length; i1++) {
                if(ball[i].equals(reBall[i1])) {
                    bingoCount++;
                    break;
                }

            }
        }
        return bingoCount;
    }
}
