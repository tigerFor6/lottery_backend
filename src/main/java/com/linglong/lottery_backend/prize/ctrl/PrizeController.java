package com.linglong.lottery_backend.prize.ctrl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linglong.lottery_backend.prize.service.impl.DltCalculationServiceImpl;
import com.linglong.lottery_backend.prize.service.impl.Pl3CalculationServiceImpl;
import com.linglong.lottery_backend.prize.service.impl.Pl5CalculationServiceImpl;
import com.linglong.lottery_backend.ticket.bean.welfare.FcSsqGame;
import com.linglong.lottery_backend.user.account.service.TblUserBalanceService;
import com.linglong.lottery_backend.lottery.digital.entity.TblDigitalResults;
import com.linglong.lottery_backend.lottery.digital.service.TblDigitalResultsService;
import com.linglong.lottery_backend.lottery.match.common.Result;
import com.linglong.lottery_backend.lottery.match.common.ResultGenerator;
import com.linglong.lottery_backend.order.entity.Order;
import com.linglong.lottery_backend.order.service.IOrderService;
import com.linglong.lottery_backend.prize.entity.BettingTicketDigital;
import com.linglong.lottery_backend.prize.entity.TblBettingTicketDetails;
import com.linglong.lottery_backend.prize.entity.TblBettingTicketGroup;
import com.linglong.lottery_backend.prize.service.TblBettingTicketDetailsService;
import com.linglong.lottery_backend.prize.service.TblBettingTicketGroupService;
import com.linglong.lottery_backend.prize.service.impl.SsqBonusCalculationServiceImpl;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.constant.WordConstant;
import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import com.linglong.lottery_backend.ticket.enums.AoliEnum;
import com.linglong.lottery_backend.ticket.service.TblBettingTicketDigitalService;
import com.linglong.lottery_backend.ticket.service.TblBettingTicketService;
import com.linglong.lottery_backend.utils.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 返奖
 */
@RestController
@RequestMapping(value = "/api/prize")
@Slf4j
public class PrizeController {

    @Autowired
    TblBettingTicketService bettingTicketService;

    @Autowired
    IOrderService iOrderService;

    @Autowired
    TblBettingTicketGroupService bettingTicketGroupService;

    @Autowired
    TblBettingTicketDetailsService bettingTicketDetailsService;

    @Autowired
    TblBettingTicketDigitalService tblBettingTicketDigitalService;

    @Autowired
    TblUserBalanceService userBalanceService;

    @Autowired
    SsqBonusCalculationServiceImpl ssqBonusCalculationService;

    @Autowired
    DltCalculationServiceImpl dltCalculationService;

    @Autowired
    Pl3CalculationServiceImpl pl3CalculationService;

    @Autowired
    Pl5CalculationServiceImpl pl5CalculationService;

    @Autowired
    TblDigitalResultsService tblDigitalResultsService;

    @Autowired
    private IdWorker idWorker;

    @RequestMapping("getBettingPrizeList")
    public Result getBettingPrizeList(@RequestParam("orderId") Long orderId) {
        List<BettingTicket> tickets = bettingTicketService.findByOrderId(orderId);
        List<BettingTicket> success = tickets.stream().filter(e -> e.getTicketStatus().equals(2)).collect(Collectors.toList());
        List<Map<String,Object>> result = new LinkedList<>();
        if (success.isEmpty()){
            return ResultGenerator.genProcessingResult(result);
        }

        List<Long> ticketIds = success.stream().map(e -> e.getTicketId()).collect(Collectors.toList());
        List<TblBettingTicketDetails> ticketDetails = bettingTicketDetailsService.findByTicketIds(ticketIds);
        List<BettingTicketDigital> bettingTicketDigitals = tblBettingTicketDigitalService.findByBettingTicketIds(ticketIds);
        Map<Long, List<BettingTicketDigital>> digitalMap = bettingTicketDigitals.stream().collect(Collectors.groupingBy(BettingTicketDigital::getBettingTicketId));

        //查询组合
        List<TblBettingTicketGroup> ticketGroups = bettingTicketGroupService.findByTicketIds(ticketIds);
        Map<Long, List<TblBettingTicketDetails>> detailMap = ticketDetails.stream().collect(Collectors.groupingBy(TblBettingTicketDetails::getBettingTicketId));
        Map<Long, List<TblBettingTicketGroup>> groupMap = ticketGroups.stream().collect(Collectors.groupingBy(TblBettingTicketGroup::getBettingTicketId));
        for (int sucindex = 0; sucindex < success.size(); sucindex++) {
            BettingTicket e = success.get(sucindex);

            if (e.getGameType().equals(AoliEnum.JCZQ.getCode()) ||
                    e.getGameType().equals(AoliEnum.JCLQ.getCode())) {
                Map<String, Object> m = new HashMap<>();
                m.put("voucherNo", String.valueOf(e.getTicketId()));
                m.put("ticketStatus", String.valueOf(e.getTicketStatus()));
                m.put("prizeStatus", String.valueOf(e.getPrizeStatus()));
                String bettingType = e.getExtra().split("_")[0];

                List<Map<String, Object>> detailList = new ArrayList<>();
                List<TblBettingTicketGroup> groups = groupMap.get(e.getTicketId());
                List<TblBettingTicketDetails> details = detailMap.get(e.getTicketId());
                if (null == details || null == groups){
                    continue;
                }
                Map<Long, TblBettingTicketDetails> detailsMap = details.stream().collect(Collectors.toMap(TblBettingTicketDetails::getTicketDetailsId, Function.identity()));
                groups.forEach(g -> {
                    Map<String, Object> resGroup = new HashMap<>();
                    resGroup.put("bettingType", bettingType);
                    resGroup.put("multiple", e.getTimes());
                    resGroup.put("groupStatus", g.getStatus());
                    String detail = g.getDetail();
                    String[] detailIds = detail.split(",");
                    List<TblBettingTicketDetails> group = new LinkedList<>();
                    for (int i = 0; i < detailIds.length; i++) {
                        group.add(detailsMap.get(Long.valueOf(detailIds[i])));
                    }
                    List<Map<String, Object>> r = group.stream().map(d -> {
                        Map<String, Object> md = new HashMap<>();
                        md.put("playCode", d.getPlayCode());
                        md.put("matchSn", d.getMatchSn());
                        md.put("oddsCode", d.getOddsCode());
                        md.put("odds", String.valueOf(d.getOdds()));
                        return md;
                    }).collect(Collectors.toList());
                    resGroup.put("detail", r);
                    detailList.add(resGroup);
                });
                m.put("group", detailList);
                result.add(m);

            } else if (e.getGameType().equals(AoliEnum.SSQ.getCode())) {
                String[] lotteryNumbers = e.getLotteryNumber().split(CommonConstant.PERCENT_SPLIT_STR + "|" + CommonConstant.COMMON_AT_STR);
                String[] balls = lotteryNumbers[1].split(CommonConstant.SPACE_SPLIT_STR);

                Map<String, Object> resultData = new HashMap<>();
                resultData.put("voucherNo", e.getTicketId().toString());
                resultData.put("ticketStatus", e.getTicketStatus());

                List<Map<String, Object>> groupList = new ArrayList<>();
                for (int i = 0; i < balls.length; i++) {
                    Map<String, Object> groupData = new HashMap<>();
                    String playType = e.getPlayType();
                    groupData.put("playCode", playType);
                    groupData.put("multiple", e.getTimes());
                    String[] ball;
                    String[] danBall ={};
                    if (balls[i].indexOf(CommonConstant.COMMON_AND_STR) < 0) {
                        ball = balls[i].split(CommonConstant.COMMON_COLON_STR);

                    } else {
                        danBall = balls[i].split(CommonConstant.COMMON_AND_STR);
                        groupData.put("danBall", danBall[0]);
                        ball = danBall[1].split(CommonConstant.COMMON_COLON_STR);
                    }

                    groupData.put("redBall", ball[0]);
                    groupData.put("blueBall", ball[1]);
                    groupData.put("prizeStatus",e.getPrizeStatus());

                    if(e.getOpenPrizeStatus().equals(Integer.valueOf(2))) {

                        List<TblDigitalResults> tblDigitalResults = tblDigitalResultsService.findByPeriodAndLotteryType(e.getPeriod(), e.getGameType());
                        if(!tblDigitalResults.isEmpty() && tblDigitalResults.size() == 3) {
                            groupData.put("prizeStatus",2);
                            String[] lotteryResults = tblDigitalResults.get(0).getLotteryResult().split(CommonConstant.COMMON_COLON_STR);
                            //计算每一注是否中奖
                            int [] awardInfo = ssqBonusCalculationService.analyseBidAwardLevels(danBall,ball[0].split(CommonConstant.COMMA_SPLIT_STR),ball[1].split(CommonConstant.COMMA_SPLIT_STR),lotteryResults[0].split(CommonConstant.COMMA_SPLIT_STR),lotteryResults[1]);
                            for (int i1 = 0; i1 < awardInfo.length; i1++) {
                                if(awardInfo[i1] > 0) {
                                    groupData.put("prizeStatus",1);
                                    break;
                                }

                            }

                        }

                    }

                    groupList.add(groupData);
                }
                resultData.put("group", groupList);
                result.add(resultData);
            }else if(e.getGameType().equals(AoliEnum.SHX115.getCode())) {
                List<BettingTicketDigital> digitalList = digitalMap.get(e.getTicketId());
                if(digitalList == null)
                    continue;
                if(digitalList.isEmpty())
                    continue;

                Map<String, Object> resultData = new HashMap<>();
                resultData.put("voucherNo", e.getTicketId().toString());
                resultData.put("ticketStatus", e.getTicketStatus());

                List<Map<String, Object>> groupList = new ArrayList<>();
                digitalList.forEach(digital -> {
                    Map<String, Object> groupData = new HashMap<>();
                    String playType = e.getPlayType();
                    groupData.put("playCode", playType);
                    groupData.put("multiple", digital.getMultiple());
                    groupData.put("playType", e.getExtra());

                    if(playType.equals(WordConstant.DANTUO)) {
                        digital.setBettingGroup("("+CommonConstant.SPACE_SPLIT_STR + digital.getBettingGroup()
                                .replaceFirst(CommonConstant.SEMICOLON_SPLIT_STR, CommonConstant.SPACE_SPLIT_STR+CommonConstant.COMMON_BRACKET_RIGHT + CommonConstant.SPACE_SPLIT_STR));
                    }

                    groupData.put("ballNumber", digital.getBettingGroup()
                            .replaceAll(CommonConstant.SEMICOLON_SPLIT_STR, CommonConstant.SPACE_SPLIT_STR + CommonConstant.COMMON_VERTICAL_STR + CommonConstant.SPACE_SPLIT_STR)
                            .replaceAll(CommonConstant.COMMA_SPLIT_STR, CommonConstant.SPACE_SPLIT_STR));

                    groupData.put("prizeStatus", digital.getBettingResult());
                    groupList.add(groupData);
                });
                resultData.put("group", groupList);
                result.add(resultData);
            }else if(e.getGameType().equals(AoliEnum.DLT.getCode())) {
                String[] lotteryNumbers = e.getLotteryNumber().split(CommonConstant.PERCENT_SPLIT_STR + "|" + CommonConstant.COMMON_AT_STR);
                String[] balls = lotteryNumbers[1].split(CommonConstant.SPACE_SPLIT_STR);
                Map<String, Object> resultData = new HashMap<>();
                resultData.put("voucherNo", e.getTicketId().toString());
                resultData.put("ticketStatus", e.getTicketStatus());
                resultData.put("chasing", e.getExtra());
                List<Map<String, Object>> groupList = new ArrayList<>();
                for (int i = 0; i < balls.length; i++) {
                    Map<String, Object> groupData = new HashMap<>();
                    String playType = e.getPlayType();
                    groupData.put("playCode", playType);
                    groupData.put("multiple", e.getTimes());
                    groupData.put("prizeStatus",e.getPrizeStatus());
                    String[] ball = balls[i].split(CommonConstant.COMMON_ESCAPE_STR+CommonConstant.COMMON_VERTICAL_STR);

                    if(e.getOpenPrizeStatus().equals(Integer.valueOf(2))) {

                        List<TblDigitalResults> tblDigitalResults = tblDigitalResultsService.findByPeriodAndLotteryType(e.getPeriod(), e.getGameType());
                        if(!tblDigitalResults.isEmpty()){
                            TblDigitalResults tblDigitalResult = tblDigitalResults.get(0);
                            String openResult = tblDigitalResult.getLotteryResult();
                            String[] openResults = openResult.split(CommonConstant.COMMON_COLON_STR);

                            Map<String, Object> bonus = null;
                            if(playType.equals(WordConstant.SINGLE)) {
                                bonus  = dltCalculationService.calcResultBonus(openResults[0].split(CommonConstant.COMMA_SPLIT_STR),
                                        openResults[1].split(CommonConstant.COMMA_SPLIT_STR), ball[0].split(CommonConstant.COMMA_SPLIT_STR), ball[1].split(CommonConstant.COMMA_SPLIT_STR));

                            }else if(playType.equals(WordConstant.MULTIPLE)) {
                                String[] loopRedball = FcSsqGame.loop(ball[0].split(CommonConstant.COMMA_SPLIT_STR), 5);
                                String[] loopBlueball = FcSsqGame.loop(ball[1].split(CommonConstant.COMMA_SPLIT_STR), 2);

                                loop_for:
                                for (int i1 = 0; i1 < loopRedball.length; i1++) {
                                    for (int i2 = 0; i2 < loopBlueball.length; i2++) {
                                        bonus  = dltCalculationService.calcResultBonus(openResults[0].split(CommonConstant.COMMA_SPLIT_STR),
                                                openResults[1].split(CommonConstant.COMMA_SPLIT_STR), ball[0].split(CommonConstant.COMMA_SPLIT_STR), ball[1].split(CommonConstant.COMMA_SPLIT_STR));

                                        if(Integer.valueOf(bonus.get("isBonus").toString()).equals(Integer.valueOf(1))) {
                                            break loop_for;
                                        }
                                    }
                                }

                            }else if(playType.equals(WordConstant.DANTUO)) {
                                String[] redBall = ball[0].split(CommonConstant.SEMICOLON_SPLIT_STR);
                                String[] blueBall = ball[1].split(CommonConstant.SEMICOLON_SPLIT_STR);
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

                                loop_for:
                                for (int i1 = 0; i1 < loopRedball.length; i1++) {
                                    for (int i2 = 0; i2 < loopBlueball.length; i2++) {
                                        bonus  = dltCalculationService.calcResultBonus(
                                                openResults[0].split(CommonConstant.COMMA_SPLIT_STR),
                                                openResults[1].split(CommonConstant.COMMA_SPLIT_STR),
                                                (redDanBall + "," +loopRedball[i1]).split(CommonConstant.COMMA_SPLIT_STR) ,
                                                (blueDanBall + "," + loopBlueball[i2]).split(CommonConstant.COMMA_SPLIT_STR));

                                        if(Integer.valueOf(bonus.get("isBonus").toString()).equals(Integer.valueOf(1))) {
                                            break loop_for;
                                        }
                                    }
                                }

                            }

                            if(Integer.valueOf(bonus.get("isBonus").toString()).equals(Integer.valueOf(1))) {
                                groupData.put("prizeStatus", 1);

                            }else {
                                groupData.put("prizeStatus", 2);
                            }
                        }

                    }

                    if(playType.equals(WordConstant.DANTUO)) {
                        ball[0] = ball[0].indexOf(CommonConstant.SEMICOLON_SPLIT_STR) > 0 ? "( "+ball[0].replaceFirst(CommonConstant.SEMICOLON_SPLIT_STR, " ) ") : ball[0];
                        ball[1] = ball[1].indexOf(CommonConstant.SEMICOLON_SPLIT_STR) > 0 ? "( "+ball[1].replaceFirst(CommonConstant.SEMICOLON_SPLIT_STR, " ) ") : ball[1];
                    }

                    groupData.put("redBall", ball[0].replaceAll(CommonConstant.COMMA_SPLIT_STR, CommonConstant.SPACE_SPLIT_STR));
                    groupData.put("blueBall", ball[1].replaceAll(CommonConstant.COMMA_SPLIT_STR, CommonConstant.SPACE_SPLIT_STR));

                    groupList.add(groupData);
                }
                resultData.put("group", groupList);
                result.add(resultData);
            }else if(e.getGameType().equals(AoliEnum.PL3.getCode())) {
                String[] lotteryNumbers = e.getLotteryNumber().split(CommonConstant.PERCENT_SPLIT_STR + "|" + CommonConstant.COMMON_AT_STR);
                String[] balls = lotteryNumbers[1].split(CommonConstant.SPACE_SPLIT_STR);

                Map<String, Object> resultData = new HashMap<>();
                resultData.put("voucherNo", e.getTicketId().toString());
                resultData.put("ticketStatus", e.getTicketStatus());

                List<Map<String, Object>> groupList = new ArrayList<>();
                for (int i = 0; i < balls.length; i++) {
                    Map<String, Object> groupData = new HashMap<>();
                    String playType = e.getPlayType();
                    groupData.put("playCode", playType);
                    groupData.put("multiple", e.getTimes());
                    groupData.put("prizeStatus", 0);

                    if(e.getOpenPrizeStatus().equals(Integer.valueOf(2))) {
                        List<TblDigitalResults> tblDigitalResults = tblDigitalResultsService.findByPeriodAndLotteryType(e.getPeriod(), e.getGameType());
                        if(!tblDigitalResults.isEmpty()){
                            TblDigitalResults tblDigitalResult = tblDigitalResults.get(0);
                            String openResult = tblDigitalResult.getLotteryResult();
                            String[] openResults = openResult.split(CommonConstant.COMMA_SPLIT_STR);

                            Map<String, Object> resultBonusMap = pl3CalculationService.isCalcBonus(openResults, balls[i].split(CommonConstant.SEMICOLON_SPLIT_STR),e.getPlayType(), e.getExtra());

                            if(Integer.valueOf(resultBonusMap.get("isBonus").toString()).equals(Integer.valueOf(1))) {
                                groupData.put("prizeStatus", 1);

                            }else {
                                groupData.put("prizeStatus", 2);
                            }
                        }
                    }


                    groupData.put("ballNumber",
                            balls[i].replaceAll(CommonConstant.COMMA_SPLIT_STR, CommonConstant.SPACE_SPLIT_STR)
                    .replaceAll(CommonConstant.SEMICOLON_SPLIT_STR, " "+ CommonConstant.COMMON_VERTICAL_STR + " "));

                    groupList.add(groupData);
                }
                resultData.put("group", groupList);
                result.add(resultData);
            }else if(e.getGameType().equals(AoliEnum.PL5.getCode())) {
                String[] lotteryNumbers = e.getLotteryNumber().split(CommonConstant.COMMON_AT_STR);
                String[] balls = lotteryNumbers[0].split(CommonConstant.SPACE_SPLIT_STR);

                Map<String, Object> resultData = new HashMap<>();
                resultData.put("voucherNo", e.getTicketId().toString());
                resultData.put("ticketStatus", e.getTicketStatus());

                List<Map<String, Object>> groupList = new ArrayList<>();
                for (int i = 0; i < balls.length; i++) {
                    Map<String, Object> groupData = new HashMap<>();
                    String playType = e.getPlayType();
                    groupData.put("playCode", playType);
                    groupData.put("multiple", e.getTimes());
                    groupData.put("prizeStatus",e.getPrizeStatus());

                    List<TblDigitalResults> tblDigitalResults = tblDigitalResultsService.findByPeriodAndLotteryType(e.getPeriod(), e.getGameType());
                    if(!tblDigitalResults.isEmpty()){
                        TblDigitalResults tblDigitalResult = tblDigitalResults.get(0);
                        String openResult = tblDigitalResult.getLotteryResult();

                        boolean isBonus = pl5CalculationService.calcBonus(balls[i], openResult);

                        if(isBonus) {
                            groupData.put("prizeStatus", 1);

                        }else {
                            groupData.put("prizeStatus", 2);
                        }
                    }

                    groupData.put("ballNumber",
                            balls[i].replaceAll(CommonConstant.COMMA_SPLIT_STR, CommonConstant.SPACE_SPLIT_STR)
                                    .replaceAll(CommonConstant.SEMICOLON_SPLIT_STR, " "+ CommonConstant.COMMON_VERTICAL_STR + " "));

                    groupList.add(groupData);
                }
                resultData.put("group", groupList);
                result.add(resultData);
            }
        }
        return ResultGenerator.genSuccessResult(result);
    }

    @PostMapping("review")
    @Transactional
    public Result review(@RequestParam("ticketId") Long ticketId) {
        BettingTicket ticket = bettingTicketService.findByTicketId(ticketId);
        userBalanceService.reward(String.valueOf(ticket.getUserId()), ticket.getTicketId(), ticket.getBonus());
        ticket.setReturnPrizeStatus(1);
        //更改票状态
        bettingTicketService.updateBettingTicket(ticket);

        //更改订单明细字段的delivery_prize_status 状态
        Order order =iOrderService.findByOrderId(ticket.getOrderId());
        JSONObject detailsObject = JSON.parseObject(order.getOrderDetails());
        detailsObject.put("delivery_prize_status",1);
        order.setOrderDetails(detailsObject.toJSONString());
        iOrderService.updateOrder(order);
        return ResultGenerator.genSuccessResult();
    }

    /**
     * @description: 异常订单退款，运营后台专用
     * @author: ZhiYao.Zhang
     * @param:
     * @return:
     * @create: 2019-06-06
     **/

    @PostMapping("exceptionOrderRefound")
    public Result exceptionOrderRefound(@RequestParam("userId") String userId, @RequestParam("amount") BigDecimal amount, @RequestParam("orderId") String orderId) {
        //userBalanceService.exceptionOrderRefound( userId, amount, orderId);
        return null;
    }

}
