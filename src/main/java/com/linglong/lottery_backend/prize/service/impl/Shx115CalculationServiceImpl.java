package com.linglong.lottery_backend.prize.service.impl;

import com.linglong.lottery_backend.user.account.service.TblUserBalanceService;
import com.linglong.lottery_backend.common.service.JpaBatch;
import com.linglong.lottery_backend.prize.bean.part.SsqBonusCalculation;
import com.linglong.lottery_backend.prize.entity.BettingTicketDigital;
import com.linglong.lottery_backend.prize.service.BonusCalculationService;
import com.linglong.lottery_backend.ticket.bean.welfare.FcSsqGame;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.constant.WordConstant;
import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import com.linglong.lottery_backend.ticket.enums.AoliEnum;
import com.linglong.lottery_backend.ticket.repository.TblBettingTicketDigitalRepository;
import com.linglong.lottery_backend.ticket.repository.TblBettingTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Shx115CalculationServiceImpl implements BonusCalculationService {

    @Autowired
    private JpaBatch jpaBatch;

    @Autowired
    private TblBettingTicketDigitalRepository tblBettingTicketDigitalRepository;

    @Autowired
    private TblBettingTicketRepository tblBettingTicketRepository;

    @Autowired
    TblUserBalanceService userBalanceService;

    @Autowired
    private SsqBonusCalculationServiceImpl ssqBonusCalculationService;

    @Override
    public void calculation(SsqBonusCalculation bonusCalculation) {
        return;
    }

    /**
     * 陕115返奖
     * @param bettingTickets
     * @param openResult
     */
    public void shx115Calculation(List<BettingTicket> bettingTickets, String openResult) {

        List<BettingTicket> updateTicketList = new ArrayList<>();
        bettingTickets.forEach(ticket -> {
            ticket.setOpenPrizeStatus(2);
            ticket.setBonusTime(new Date());
            ticket = doShx115ChoosePlayType(ticket, openResult);
            updateTicketList.add(ticket);
        });

        jpaBatch.batchUpdate(updateTicketList);

        List<Long> ids = updateTicketList.stream().map(e -> Long.valueOf(e.getTicketId())).collect(Collectors.toList());

        ssqBonusCalculationService.updateOrderStatus(ids);

        refundBonus(ids);
    }


    /**
     * 陕11选5玩法
     * @param ticket
     */
    private BettingTicket doShx115ChoosePlayType(BettingTicket ticket, String openResult) {
        String[] openResults = openResult.split(CommonConstant.COMMA_SPLIT_STR);

        List<BettingTicketDigital> bettingTicketDigitals = tblBettingTicketDigitalRepository.findByBettingTicketId(ticket.getTicketId());

        ticket.setPrizeStatus(2);

        List<BettingTicketDigital> updateDigitals = new ArrayList<>();
        bettingTicketDigitals.forEach(digitals -> {
            BigDecimal bonus = digitals.getPreTaxBonus();
            digitals.setBettingResult(2);

            String balls = digitals.getBettingGroup();
            if(ticket.getExtra().equals(AoliEnum.TOP_ONE.getStatus())) {
                bonus = new BigDecimal(AoliEnum.TOP_ONE.getCode());

                String[] ball = balls.split(CommonConstant.SEMICOLON_SPLIT_STR);

                //选一：选号的1个号码与当期顺序摇出的5个号码中的第1个号码相同，则中奖。
                if(digitals.getPlayType().equals(WordConstant.SINGLE)) {
                    if(ball[0].equals(openResults[0])) {
                        ticket.setBonus(ticket.getBonus().add(bonus));
                        ticket.setPrizeStatus(1);
                        ticket.setReturnPrizeStatus(1);
                        digitals.setBettingResult(1);
                    }

                }else if(digitals.getPlayType().equals(WordConstant.DIRECT)) {
                    for (int i1 = 0; i1 < ball.length; i1++) {
                        if(ball[i1].equals(openResults[0])) {
                            ticket.setBonus(ticket.getBonus().add(bonus));
                            ticket.setPrizeStatus(1);
                            ticket.setReturnPrizeStatus(1);
                            digitals.setBettingResult(1);
                        }
                    }

                }

            }else if(ticket.getExtra().equals(AoliEnum.TOP_TWO_DIRECT.getStatus())) {
                bonus = new BigDecimal(AoliEnum.TOP_TWO_DIRECT.getCode());

                String[] ball = balls.split(CommonConstant.SEMICOLON_SPLIT_STR);

                //前二直选：选号的2个号码与当期顺序摇出的5个号码中的前2个号码相同且顺序一致，则中奖。
                if(digitals.getPlayType().equals(WordConstant.SINGLE)) {
                    if(ball[0].equals(openResults[0])
                            && ball[1].equals(openResults[1])) {
                        ticket.setBonus(ticket.getBonus().add(bonus));
                        ticket.setPrizeStatus(1);
                        ticket.setReturnPrizeStatus(1);
                        digitals.setBettingResult(1);
                    }

                }else if(digitals.getPlayType().equals(WordConstant.DIRECT)) {
                    String[] dircetFirstBall = ball[0].split(CommonConstant.COMMA_SPLIT_STR);
                    String[] dircetSecondBall = ball[1].split(CommonConstant.COMMA_SPLIT_STR);

                    for (int i1 = 0; i1 < dircetFirstBall.length; i1++) {
                        for (int i2 = 0; i2 < dircetSecondBall.length; i2++) {
                            if(dircetFirstBall[i1].equals(openResults[0])
                                    && dircetSecondBall[i2].equals(openResults[1])) {
                                ticket.setBonus(ticket.getBonus().add(bonus));
                                ticket.setPrizeStatus(1);
                                ticket.setReturnPrizeStatus(1);
                                digitals.setBettingResult(1);
                            }
                        }
                    }

                }


            }else if(ticket.getExtra().equals(AoliEnum.TOP_THREE_DIRECT.getStatus())) {
                bonus = new BigDecimal(AoliEnum.TOP_THREE_DIRECT.getCode());
                String[] ball = balls.split(CommonConstant.SEMICOLON_SPLIT_STR);

                //前三直选：选号的3个号码与当期顺序摇出的5个号码中的前3个号码相同且顺序一致，则中奖。
                if(digitals.getPlayType().equals(WordConstant.SINGLE)) {

                    if(ball[0].equals(openResults[0])
                            && ball[1].equals(openResults[1])
                            && ball[2].equals(openResults[2])) {
                        ticket.setBonus(ticket.getBonus().add(bonus));
                        ticket.setPrizeStatus(1);
                        ticket.setReturnPrizeStatus(1);
                        digitals.setBettingResult(1);
                    }

                }else if(digitals.getPlayType().equals(WordConstant.DIRECT)) {
                    String[] dircetFirstBall = ball[0].split(CommonConstant.COMMA_SPLIT_STR);
                    String[] dircetSecondBall = ball[1].split(CommonConstant.COMMA_SPLIT_STR);
                    String[] dircetThirdBall = ball[2].split(CommonConstant.COMMA_SPLIT_STR);

                    for (int i1 = 0; i1 < dircetFirstBall.length; i1++) {
                        for (int i2 = 0; i2 < dircetSecondBall.length; i2++) {
                            for (int i3 = 0; i3 < dircetThirdBall.length; i3++) {
                                if(dircetFirstBall[i1].equals(openResults[0])
                                        && dircetSecondBall[i2].equals(openResults[1])
                                        && dircetThirdBall[i3].equals(openResults[2])) {
                                    ticket.setBonus(ticket.getBonus().add(bonus));
                                    ticket.setPrizeStatus(1);
                                    ticket.setReturnPrizeStatus(1);
                                    digitals.setBettingResult(1);
                                }

                            }
                        }
                    }

                }


            }else if(ticket.getExtra().equals(AoliEnum.TOP_TWO_GROUP.getStatus())) {
                bonus = new BigDecimal(AoliEnum.TOP_TWO_GROUP.getCode());
                String[] ball = balls.split(CommonConstant.SEMICOLON_SPLIT_STR);

                //前二组选：选号的2个号码与当期顺序摇出的5个号码中的前2个号码相同，则中奖。
                if(digitals.getPlayType().equals(WordConstant.SINGLE)) {
                    String[] groupBall = ball[0].split(CommonConstant.COMMA_SPLIT_STR);
                    if((groupBall[0].equals(openResults[0]) || groupBall[0].equals(openResults[1]))
                            && (groupBall[1].equals(openResults[0]) || groupBall[1].equals(openResults[1]))) {
                        ticket.setBonus(ticket.getBonus().add(bonus));
                        ticket.setPrizeStatus(1);
                        digitals.setBettingResult(1);
                        ticket.setReturnPrizeStatus(1);
                    }

                }else if(digitals.getPlayType().equals(WordConstant.MULTIPLE)) {
                    String[] groupBall = ball[0].split(CommonConstant.COMMA_SPLIT_STR);
                    String[] loopGroupBall = FcSsqGame.loop(groupBall, 2);

                    for (int i1 = 0; i1 < loopGroupBall.length; i1++) {
                        String[] splitBall = loopGroupBall[i1].split(CommonConstant.COMMA_SPLIT_STR);
                        if((splitBall[0].equals(openResults[0]) || splitBall[0].equals(openResults[1]))
                                && (splitBall[1].equals(openResults[0]) || splitBall[1].equals(openResults[1]))) {
                            ticket.setBonus(ticket.getBonus().add(bonus));
                            ticket.setPrizeStatus(1);
                            digitals.setBettingResult(1);
                            ticket.setReturnPrizeStatus(1);
                        }

                    }
                }else if(digitals.getPlayType().equals(WordConstant.DANTUO)) {
                    String[] groupBall = ball[1].split(CommonConstant.COMMA_SPLIT_STR);
                    String[] loopGroupBall = FcSsqGame.loop(groupBall, 1);

                    for (int i1 = 0; i1 < loopGroupBall.length; i1++) {
                        if((ball[0].equals(openResults[0]) || ball[0].equals(openResults[1]))
                                && (loopGroupBall[i1].equals(openResults[0]) || loopGroupBall[i1].equals(openResults[1]))) {
                            ticket.setBonus(ticket.getBonus().add(bonus));
                            ticket.setPrizeStatus(1);
                            digitals.setBettingResult(1);
                            ticket.setReturnPrizeStatus(1);
                        }

                    }

                }

            }else if(ticket.getExtra().equals(AoliEnum.TOP_THREE_GROUP.getStatus())) {
                bonus = new BigDecimal(AoliEnum.TOP_THREE_GROUP.getCode());
                String[] ball = balls.split(CommonConstant.SEMICOLON_SPLIT_STR);

                //前三组选：选号的3个号码与当期顺序摇出的5个号码中的前3个号码相同，则中奖。
                if(digitals.getPlayType().equals(WordConstant.SINGLE)) {

                    if((ball[0].equals(openResults[0]) || ball[0].equals(openResults[1]) || ball[0].equals(openResults[2]))
                            && (ball[1].equals(openResults[0]) || ball[1].equals(openResults[1])  || ball[1].equals(openResults[2]))
                            && (ball[2].equals(openResults[0]) || ball[2].equals(openResults[1])  || ball[2].equals(openResults[2]))) {
                        ticket.setBonus(ticket.getBonus().add(bonus));
                        ticket.setPrizeStatus(1);
                        digitals.setBettingResult(1);
                        ticket.setReturnPrizeStatus(1);
                    }

                }else if(digitals.getPlayType().equals(WordConstant.MULTIPLE)) {
                    String[] groupBall = ball[0].split(CommonConstant.COMMA_SPLIT_STR);
                    String[] loopGroupBall = FcSsqGame.loop(groupBall, 3);

                    for (int i1 = 0; i1 < loopGroupBall.length; i1++) {
                        String[] splitBall = loopGroupBall[i1].split(CommonConstant.COMMA_SPLIT_STR);
                        if((splitBall[0].equals(openResults[0]) || splitBall[0].equals(openResults[1]) || splitBall[0].equals(openResults[2]))
                                && (splitBall[1].equals(openResults[0]) || splitBall[1].equals(openResults[1])  || splitBall[1].equals(openResults[2]))
                                && (splitBall[2].equals(openResults[0]) || splitBall[2].equals(openResults[1])  || splitBall[2].equals(openResults[2]))) {
                            ticket.setBonus(ticket.getBonus().add(bonus));
                            ticket.setPrizeStatus(1);
                            digitals.setBettingResult(1);
                            ticket.setReturnPrizeStatus(1);
                        }

                    }

                }else if(digitals.getPlayType().equals(WordConstant.DANTUO)) {
                    String[] danBall = ball[0].split(CommonConstant.COMMA_SPLIT_STR);
                    String[] groupBall = ball[1].split(CommonConstant.COMMA_SPLIT_STR);
                    String[] loopGroupBall = FcSsqGame.loop(groupBall, 3 - danBall.length);

                    for (int i1 = 0; i1 < loopGroupBall.length; i1++) {
                        String[] splitBall = (ball[0] + "," +loopGroupBall[i1]).split(CommonConstant.COMMA_SPLIT_STR);
                        if((splitBall[0].equals(openResults[0]) || splitBall[0].equals(openResults[1]) || splitBall[0].equals(openResults[2]))
                                && (splitBall[1].equals(openResults[0]) || splitBall[1].equals(openResults[1])  || splitBall[1].equals(openResults[2]))
                                && (splitBall[2].equals(openResults[0]) || splitBall[2].equals(openResults[1])  || splitBall[2].equals(openResults[2]))) {
                            ticket.setBonus(ticket.getBonus().add(bonus));
                            ticket.setPrizeStatus(1);
                            digitals.setBettingResult(1);
                            ticket.setReturnPrizeStatus(1);
                        }

                    }

                }

            }else if(ticket.getExtra().equals(AoliEnum.EITHER_TWO.getStatus())) {
                bonus = new BigDecimal(AoliEnum.EITHER_TWO.getCode());

                String[] ball = balls.split(CommonConstant.SEMICOLON_SPLIT_STR);

                //任选二：选号的2个号码与当期摇出的5个号码中的任2个号码相同，则中奖。
                if(digitals.getPlayType().equals(WordConstant.SINGLE)) {
                    if(atChooseMany(openResults, ball[0].split(CommonConstant.COMMA_SPLIT_STR), 2, 2)) {
                        ticket.setBonus(ticket.getBonus().add(bonus));
                        ticket.setPrizeStatus(1);
                        digitals.setBettingResult(1);
                        ticket.setReturnPrizeStatus(1);
                    }

                }else if(digitals.getPlayType().equals(WordConstant.MULTIPLE)) {
                    String[] loopGroupBall = FcSsqGame.loop(ball[0].split(CommonConstant.COMMA_SPLIT_STR), 2);
                    for (int i1 = 0; i1 < loopGroupBall.length; i1++) {
                        if(atChooseMany(openResults, loopGroupBall[i1].split(CommonConstant.COMMA_SPLIT_STR), 2, 2)) {
                            ticket.setBonus(ticket.getBonus().add(bonus));
                            ticket.setPrizeStatus(1);
                            digitals.setBettingResult(1);
                            ticket.setReturnPrizeStatus(1);
                        }
                    }

                }else if(digitals.getPlayType().equals(WordConstant.DANTUO)) {

                    String[] loopGroupBall = FcSsqGame.loop(ball[1].split(CommonConstant.COMMA_SPLIT_STR), 1);
                    for (int i1 = 0; i1 < loopGroupBall.length; i1++) {
                        String group = ball[0] + "," + loopGroupBall[i1];
                        if(atChooseMany(openResults, group.split(CommonConstant.COMMA_SPLIT_STR), 2, 2)) {
                            ticket.setBonus(ticket.getBonus().add(bonus));
                            ticket.setPrizeStatus(1);
                            digitals.setBettingResult(1);
                            ticket.setReturnPrizeStatus(1);
                        }
                    }
                }

            }else if(ticket.getExtra().equals(AoliEnum.EITHER_THREE.getStatus())) {
                bonus = new BigDecimal(AoliEnum.EITHER_THREE.getCode());
                String[] ball = balls.split(CommonConstant.SEMICOLON_SPLIT_STR);

                //任选三：选号的3个号码与当期摇出的5个号码中的任3个号码相同，则中奖。
                if(digitals.getPlayType().equals(WordConstant.SINGLE)) {

                    if(atChooseMany(openResults, ball[0].split(CommonConstant.COMMA_SPLIT_STR), 3, 3)) {
                        ticket.setBonus(ticket.getBonus().add(bonus));
                        ticket.setPrizeStatus(1);
                        digitals.setBettingResult(1);
                        ticket.setReturnPrizeStatus(1);
                    }

                }else if(digitals.getPlayType().equals(WordConstant.MULTIPLE)) {
                    String[] loopGroupBall = FcSsqGame.loop(ball[0].split(CommonConstant.COMMA_SPLIT_STR), 3);
                    for (int i1 = 0; i1 < loopGroupBall.length; i1++) {
                        if(atChooseMany(openResults, loopGroupBall[i1].split(CommonConstant.COMMA_SPLIT_STR), 3, 3)) {
                            ticket.setBonus(ticket.getBonus().add(bonus));
                            ticket.setPrizeStatus(1);
                            digitals.setBettingResult(1);
                            ticket.setReturnPrizeStatus(1);
                        }
                    }


                }else if(digitals.getPlayType().equals(WordConstant.DANTUO)) {
                    String[] danBall = ball[0].split(CommonConstant.COMMA_SPLIT_STR);
                    String[] loopGroupBall = FcSsqGame.loop(ball[1].split(CommonConstant.COMMA_SPLIT_STR), 3 - danBall.length);
                    for (int i1 = 0; i1 < loopGroupBall.length; i1++) {
                        String group = ball[0] + "," + loopGroupBall[i1];
                        if(atChooseMany(openResults, group.split(CommonConstant.COMMA_SPLIT_STR), 3, 3)) {
                            ticket.setBonus(ticket.getBonus().add(bonus));
                            ticket.setPrizeStatus(1);
                            digitals.setBettingResult(1);
                            ticket.setReturnPrizeStatus(1);
                        }
                    }
                }

            }else if(ticket.getExtra().equals(AoliEnum.EITHER_FOUR.getStatus())) {
                bonus = new BigDecimal(AoliEnum.EITHER_FOUR.getCode());
                String[] ball = balls.split(CommonConstant.SEMICOLON_SPLIT_STR);

                //任选四中4：选号的4个号码与当期摇出的5个号码中的任4个号码相同，则中奖。
                if(digitals.getPlayType().equals(WordConstant.SINGLE)) {

                    if(atChooseMany(openResults, ball[0].split(CommonConstant.COMMA_SPLIT_STR), 4, 4)) {
                        ticket.setBonus(ticket.getBonus().add(bonus));
                        ticket.setPrizeStatus(1);
                        digitals.setBettingResult(1);
                        ticket.setReturnPrizeStatus(1);
                    }

                }else if(digitals.getPlayType().equals(WordConstant.MULTIPLE)) {

                    String[] loopGroupBall = FcSsqGame.loop(ball[0].split(CommonConstant.COMMA_SPLIT_STR), 4);
                    for (int i1 = 0; i1 < loopGroupBall.length; i1++) {
                        if(atChooseMany(openResults, loopGroupBall[i1].split(CommonConstant.COMMA_SPLIT_STR), 4, 4)) {
                            ticket.setBonus(ticket.getBonus().add(bonus));
                            ticket.setPrizeStatus(1);
                            digitals.setBettingResult(1);
                            ticket.setReturnPrizeStatus(1);
                        }
                    }

                }else if(digitals.getPlayType().equals(WordConstant.DANTUO)) {
                    String[] danBall = ball[0].split(CommonConstant.COMMA_SPLIT_STR);
                    String[] loopGroupBall = FcSsqGame.loop(ball[1].split(CommonConstant.COMMA_SPLIT_STR), 4 - danBall.length);
                    for (int i1 = 0; i1 < loopGroupBall.length; i1++) {
                        String group = ball[0] + "," + loopGroupBall[i1];
                        if(atChooseMany(openResults, group.split(CommonConstant.COMMA_SPLIT_STR), 4, 4)) {
                            ticket.setBonus(ticket.getBonus().add(bonus));
                            ticket.setPrizeStatus(1);
                            digitals.setBettingResult(1);
                            ticket.setReturnPrizeStatus(1);
                        }
                    }

                }

            }else if(ticket.getExtra().equals(AoliEnum.EITHER_FIVE.getStatus())) {
                bonus = new BigDecimal(AoliEnum.EITHER_FIVE.getCode());
                String[] ball = balls.split(CommonConstant.SEMICOLON_SPLIT_STR);

                //任选五中5：选号的5个号码与当期摇出的5个号码相同，则中奖。
                if(digitals.getPlayType().equals(WordConstant.SINGLE)) {

                    if(atChooseMany(openResults, ball[0].split(CommonConstant.COMMA_SPLIT_STR), 5, 5)) {
                        ticket.setBonus(ticket.getBonus().add(bonus));
                        ticket.setPrizeStatus(1);
                        digitals.setBettingResult(1);
                        ticket.setReturnPrizeStatus(1);
                    }

                }else if(digitals.getPlayType().equals(WordConstant.MULTIPLE)) {

                    String[] loopGroupBall = FcSsqGame.loop(ball[0].split(CommonConstant.COMMA_SPLIT_STR), 5);
                    for (int i1 = 0; i1 < loopGroupBall.length; i1++) {
                        if(atChooseMany(openResults, loopGroupBall[i1].split(CommonConstant.COMMA_SPLIT_STR), 5, 5)) {
                            ticket.setBonus(ticket.getBonus().add(bonus));
                            ticket.setPrizeStatus(1);
                            digitals.setBettingResult(1);
                            ticket.setReturnPrizeStatus(1);
                        }
                    }

                }else if(digitals.getPlayType().equals(WordConstant.DANTUO)) {
                    String[] danBall = ball[0].split(CommonConstant.COMMA_SPLIT_STR);
                    String[] loopGroupBall = FcSsqGame.loop(ball[1].split(CommonConstant.COMMA_SPLIT_STR), 5 - danBall.length);
                    for (int i1 = 0; i1 < loopGroupBall.length; i1++) {
                        String group = ball[0] + "," + loopGroupBall[i1];
                        if(atChooseMany(openResults, group.split(CommonConstant.COMMA_SPLIT_STR), 5, 5)) {
                            ticket.setBonus(ticket.getBonus().add(bonus));
                            ticket.setPrizeStatus(1);
                            digitals.setBettingResult(1);
                            ticket.setReturnPrizeStatus(1);
                        }
                    }

                }

            }else if(ticket.getExtra().equals(AoliEnum.EITHER_SIX.getStatus())) {
                bonus = new BigDecimal(AoliEnum.EITHER_SIX.getCode());
                String[] ball = balls.split(CommonConstant.SEMICOLON_SPLIT_STR);


                //任选六中5：选号的6个号码中任5个号码与当期摇出的5个号码相同，则中奖。
                if(digitals.getPlayType().equals(WordConstant.SINGLE)) {

                    if(atChooseMany(openResults, ball[0].split(CommonConstant.COMMA_SPLIT_STR), 6, 5)) {
                        ticket.setBonus(ticket.getBonus().add(bonus));
                        ticket.setPrizeStatus(1);
                        digitals.setBettingResult(1);
                        ticket.setReturnPrizeStatus(1);
                    }

                }else if(digitals.getPlayType().equals(WordConstant.MULTIPLE)) {

                    String[] loopGroupBall = FcSsqGame.loop(ball[0].split(CommonConstant.COMMA_SPLIT_STR), 6);
                    for (int i1 = 0; i1 < loopGroupBall.length; i1++) {
                        if(atChooseMany(openResults, loopGroupBall[i1].split(CommonConstant.COMMA_SPLIT_STR), 6, 5)) {
                            ticket.setBonus(ticket.getBonus().add(bonus));
                            ticket.setPrizeStatus(1);
                            digitals.setBettingResult(1);
                            ticket.setReturnPrizeStatus(1);
                        }
                    }

                }else if(digitals.getPlayType().equals(WordConstant.DANTUO)) {
                    String[] danBall = ball[0].split(CommonConstant.COMMA_SPLIT_STR);
                    String[] loopGroupBall = FcSsqGame.loop(ball[1].split(CommonConstant.COMMA_SPLIT_STR), 6 - danBall.length);
                    for (int i1 = 0; i1 < loopGroupBall.length; i1++) {
                        String group = ball[0] + "," + loopGroupBall[i1];
                        if(atChooseMany(openResults, group.split(CommonConstant.COMMA_SPLIT_STR), 6, 5)) {
                            ticket.setBonus(ticket.getBonus().add(bonus));
                            ticket.setPrizeStatus(1);
                            digitals.setBettingResult(1);
                            ticket.setReturnPrizeStatus(1);
                        }
                    }

                }

            }else if(ticket.getExtra().equals(AoliEnum.EITHER_SENVEN.getStatus())) {
                bonus = new BigDecimal(AoliEnum.EITHER_SENVEN.getCode());
                String[] ball = balls.split(CommonConstant.SEMICOLON_SPLIT_STR);

                //任选七中5：选号的7个号码中任5个号码与当期摇出的5个号码相同，则中奖。
                if(digitals.getPlayType().equals(WordConstant.SINGLE)) {

                    if(atChooseMany(openResults, ball[0].split(CommonConstant.COMMA_SPLIT_STR), 7, 5)) {
                        ticket.setBonus(ticket.getBonus().add(bonus));
                        ticket.setPrizeStatus(1);
                        digitals.setBettingResult(1);
                        ticket.setReturnPrizeStatus(1);
                    }

                }else if(digitals.getPlayType().equals(WordConstant.MULTIPLE)) {

                    String[] loopGroupBall = FcSsqGame.loop(ball[0].split(CommonConstant.COMMA_SPLIT_STR), 7);
                    for (int i1 = 0; i1 < loopGroupBall.length; i1++) {
                        if(atChooseMany(openResults, loopGroupBall[i1].split(CommonConstant.COMMA_SPLIT_STR), 7, 5)) {
                            ticket.setBonus(ticket.getBonus().add(bonus));
                            ticket.setPrizeStatus(1);
                            digitals.setBettingResult(1);
                            ticket.setReturnPrizeStatus(1);
                        }
                    }

                }else if(digitals.getPlayType().equals(WordConstant.DANTUO)) {

                    String[] danBall = ball[0].split(CommonConstant.COMMA_SPLIT_STR);
                    String[] loopGroupBall = FcSsqGame.loop(ball[1].split(CommonConstant.COMMA_SPLIT_STR), 7 - danBall.length);
                    for (int i1 = 0; i1 < loopGroupBall.length; i1++) {
                        String group = ball[0] + "," + loopGroupBall[i1];
                        if(atChooseMany(openResults, group.split(CommonConstant.COMMA_SPLIT_STR), 7, 5)) {
                            ticket.setBonus(ticket.getBonus().add(bonus));
                            ticket.setPrizeStatus(1);
                            digitals.setBettingResult(1);
                            ticket.setReturnPrizeStatus(1);
                        }
                    }

                }

            }else if(ticket.getExtra().equals(AoliEnum.EITHER_EIGHT.getStatus())) {
                bonus = new BigDecimal(AoliEnum.EITHER_EIGHT.getCode());
                String[] ball = balls.split(CommonConstant.SEMICOLON_SPLIT_STR);

                //任选八中5：选号的8个号码中任5个号码与当期摇出的5个号码相同，则中奖。
                if(digitals.getPlayType().equals(WordConstant.SINGLE)) {

                    if(atChooseMany(openResults, ball[0].split(CommonConstant.COMMA_SPLIT_STR), 8, 5)) {
                        ticket.setBonus(ticket.getBonus().add(bonus));
                        ticket.setPrizeStatus(1);
                        digitals.setBettingResult(1);
                        ticket.setReturnPrizeStatus(1);
                    }

                }else if(digitals.getPlayType().equals(WordConstant.MULTIPLE)) {

                    String[] loopGroupBall = FcSsqGame.loop(ball[0].split(CommonConstant.COMMA_SPLIT_STR), 8);
                    for (int i1 = 0; i1 < loopGroupBall.length; i1++) {
                        if(atChooseMany(openResults, loopGroupBall[i1].split(CommonConstant.COMMA_SPLIT_STR), 8, 5)) {
                            ticket.setBonus(ticket.getBonus().add(bonus));
                            ticket.setPrizeStatus(1);
                            digitals.setBettingResult(1);
                            ticket.setReturnPrizeStatus(1);
                        }
                    }

                }else if(digitals.getPlayType().equals(WordConstant.DANTUO)) {
                    String[] danBall = ball[0].split(CommonConstant.COMMA_SPLIT_STR);
                    String[] loopGroupBall = FcSsqGame.loop(ball[1].split(CommonConstant.COMMA_SPLIT_STR), 8 - danBall.length);
                    for (int i1 = 0; i1 < loopGroupBall.length; i1++) {
                        String group = ball[0] + "," + loopGroupBall[i1];
                        if(atChooseMany(openResults, group.split(CommonConstant.COMMA_SPLIT_STR), 8, 5)) {
                            ticket.setBonus(ticket.getBonus().add(bonus));
                            ticket.setPrizeStatus(1);
                            digitals.setBettingResult(1);
                            ticket.setReturnPrizeStatus(1);
                        }
                    }

                }

            }
            digitals.setLotteryTime(new Date());
            updateDigitals.add(digitals);
        });

        jpaBatch.batchUpdate(updateDigitals);

        if(ticket.getPrizeStatus().equals(1))
            ticket.setBonus(
                    ticket.getBonus().multiply(
                            new BigDecimal(ticket.getTimes())
                    ).
                            multiply(new BigDecimal(100)));

        return ticket;
    }

    /**
     * 任选 几(at) 中 几(many)
     * @param openResults
     * @param balls
     * @param at
     * @param many
     * @return
     */
    private boolean atChooseMany(String[] openResults, String[] balls, int at, int many) {
        int manyCount = 0;

        for (int i = 0; i < at; i++) {

            for (int i1 = 0; i1 < openResults.length; i1++) {
                if(balls[i].equals(openResults[i1])) {
                    manyCount++;
                }
            }

        }

        if(manyCount == many) {
            return true;
        }
        return false;
    }

    /**
     * 返回中奖金额到余额
     * @param ids
     */
    public void refundBonus(List<Long> ids) {
        List<BettingTicket> ticketList = tblBettingTicketRepository.findAllByTicketIdIn(ids);
        ticketList = ticketList.stream().filter(e -> e.getPrizeStatus().equals(1) || e.getBonus().doubleValue() > 0).collect(Collectors.toList());

        ticketList.forEach(ticket -> {
            userBalanceService.reward(ticket.getUserId().toString(), ticket.getTicketId(), ticket.getBonus());
        });
    }
}
