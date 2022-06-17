package com.linglong.lottery_backend.prize.service.impl;

import com.alibaba.fastjson.JSON;
import com.linglong.lottery_backend.ticket.constant.WordConstant;
import com.linglong.lottery_backend.user.account.service.TblUserBalanceService;
import com.linglong.lottery_backend.common.bean.LockKey;
import com.linglong.lottery_backend.lottery.digital.service.TblDigitalResultsService;
import com.linglong.lottery_backend.order.service.IOrderService;
import com.linglong.lottery_backend.prize.bean.part.SsqBonusCalculation;
import com.linglong.lottery_backend.prize.entity.OpenPrizeStatus;
import com.linglong.lottery_backend.prize.entity.PrizeStatus;
import com.linglong.lottery_backend.lottery.digital.entity.TblDigitalResults;
import com.linglong.lottery_backend.prize.entity.custom.DigitalDetail;
import com.linglong.lottery_backend.prize.service.BonusCalculationService;
import com.linglong.lottery_backend.prize.task.BatchAsyncTask;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import com.linglong.lottery_backend.ticket.entity.TicketStatus;
import com.linglong.lottery_backend.ticket.service.TblBettingTicketDigitalService;
import com.linglong.lottery_backend.ticket.service.TblBettingTicketService;
import com.linglong.redisson.configure.annotations.LockAction;
import com.linglong.redisson.configure.util.LockType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service(value = "ssqBonusCalculationService")
@Slf4j
public class SsqBonusCalculationServiceImpl implements BonusCalculationService {

    @Autowired
    TblDigitalResultsService digitalResultsService;

    @Autowired
    TblBettingTicketDigitalService bettingTicketDigitalService;

    @Autowired
    TblBettingTicketService bettingTicketService;

    @Autowired
    TblUserBalanceService userBalanceService;

    @Autowired
    IOrderService orderService;

    @Autowired
    BatchAsyncTask batchAsyncTask;

    /**
     * 奖金
     */
    private final BigDecimal[] bonus = {BigDecimal.valueOf(-1),BigDecimal.valueOf(-1),BigDecimal.valueOf(3000),BigDecimal.valueOf(200),BigDecimal.valueOf(10),BigDecimal.valueOf(5)};
    /**
     * 中奖规则
     */
    private final static int[][] levelInfo = {
            {1, 6, 1},
            {2, 6, 0},
            {3, 5, 1},
            {4, 5, 0},
            {4, 4, 1},
            {5, 4, 0},
            {5, 3, 1},
            {6, 2, 1},
            {6, 1, 1},
            {6, 0, 1}
    };

    private final static BigDecimal amount = new BigDecimal(100);
    @Override
    @LockAction(key = LockKey.lockPrizeSsq, value = "#calculation.period", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public void calculation(SsqBonusCalculation calculation) {
        List<TblDigitalResults> r = digitalResultsService.findByPeriodAndLotteryType(calculation.getPeriod(),calculation.getLotteryType());
        if (r.size() == 3){
            Map<String, List<TblDigitalResults>> g = r.stream().collect(Collectors.groupingBy(TblDigitalResults::getSource));
            if (g.size() == 3){
                Set<String> lotteryResult = r.stream().map(e -> e.getLotteryResult()).collect(Collectors.toSet());
                if (lotteryResult.isEmpty()){
                    return;
                }
                if (lotteryResult.size()>1){
                    //出现异常结果
                    //插入异常开奖结果信息
                    log.error("ssq calculation error:{}",JSON.toJSONString(calculation));
                    return;
                }
                //String de = lotteryDetail.toArray(new String[lotteryDetail.size()])[0];
                String re = lotteryResult.toArray(new String[lotteryResult.size()])[0];
                //List<DigitalDetail> details = JSON.parseArray(de, DigitalDetail.class);
                List<DigitalDetail> details = new ArrayList<>();
                log.info("开奖计算----");
                Long[] ticketId = this.doStatisticsBettingDigital(re,details,calculation.getPeriod(),calculation.getLotteryType());
                if (null == ticketId || ticketId.length == 0){
                    return;
                }
                this.updateOrderStatus(Arrays.asList(ticketId));
            }
        }

    }

    @Transactional
    public void updateOrderStatus(List<Long> ticketIds) {
        //查询所有票
        List<BettingTicket> tickets = bettingTicketService.findAllByTicketIds(ticketIds);
        Map<Long, List<BettingTicket>> group = tickets.stream().collect(Collectors.groupingBy(BettingTicket::getOrderId));
        group.forEach((k, v) -> {
            Set<Integer> prizeArray = new HashSet<>();
            v.forEach(f -> prizeArray.add(f.getPrizeStatus()));
            boolean awarded = prizeArray.contains(PrizeStatus.awarded.getValue());
            BigDecimal totalAmount = new BigDecimal(0.00);
            if (awarded) {
                boolean bigAwarded = false;
                for (int i = 0; i < v.size(); i++) {
                    if (v.get(i).getBigPrizeStatus()) {
                        bigAwarded = true;
                    }
                    totalAmount = totalAmount.add(v.get(i).getBonus());
                }
                orderService.updateOpenAllPrizeStatus(k, OpenPrizeStatus.opened.getValue(), bigAwarded ? PrizeStatus.bigAwarded.getValue() : PrizeStatus.awarded.getValue(), totalAmount);
                return;
            }else{
                orderService.updateOpenAllPrizeStatus(k, OpenPrizeStatus.opened.getValue(), PrizeStatus.wait.getValue(), totalAmount);
                return;
            }
        });
    }

    /**
     * 计算双色球中奖结果
     * @param re
     * @param details
     * @param period
     * @param lotteryType
     */
    @Transactional
    protected Long[] doStatisticsBettingDigital(String re, List<DigitalDetail> details, Integer period, Integer lotteryType) {
        List<BettingTicket> bettingTickets = bettingTicketService.findByPeriodAndGameTypeAndTicketStatus(period,lotteryType, TicketStatus.success.getValue());
        bettingTickets = bettingTickets.stream().filter(e -> e.getPrizeStatus().equals(Integer.valueOf(0))).collect(Collectors.toList());
        if (bettingTickets.isEmpty()){
            return null;
        }

        //Map<Integer,DigitalDetail> digitalMap = details.stream().collect(Collectors.toMap(DigitalDetail::getLevel, s -> s));
        String[] lotteryNum = re.split(CommonConstant.COMMON_COLON_STR);

        String[] lotteryRedBall = lotteryNum[0].split(CommonConstant.COMMA_SPLIT_STR);
        String lotteryBlueBall = lotteryNum[1].split(CommonConstant.COMMA_SPLIT_STR)[0];
        Set<Long> ticketId = new HashSet<>();
        //暂时先这样写，后期如何数据量大的话，用多线程处理
        bettingTickets.forEach(e->{
            String lotteryNumber = e.getLotteryNumber();
            //String s = "2019072%05,07,10,15,20,33:03 01,05,10,20,27,33:04@2";
            String[] all = lotteryNumber.split(CommonConstant.COMMON_AT_STR)[0].split(CommonConstant.PERCENT_SPLIT_STR)[1].split(CommonConstant.SPACE_SPLIT_STR);
            BigDecimal total = BigDecimal.ZERO;
            for (int i = 0; i < all.length; i++) {
                String[] sAll = all[i].split(CommonConstant.COMMON_COLON_STR);
                String[] redAllArr = sAll[0].split(CommonConstant.COMMON_AND_STR);
                String[] danMa = {};
                String[] tuoMa = {};
                String[] blueBall = sAll[1].split(CommonConstant.COMMA_SPLIT_STR);
                if (redAllArr.length == 2){
                    danMa = redAllArr[0].split(CommonConstant.COMMA_SPLIT_STR);
                    tuoMa = redAllArr[1].split(CommonConstant.COMMA_SPLIT_STR);
                }else{
                    tuoMa = redAllArr[0].split(CommonConstant.COMMA_SPLIT_STR);
                }
                int [] awardInfo = analyseBidAwardLevels(danMa,tuoMa,blueBall,lotteryRedBall,lotteryBlueBall);
                for (int j = 0; j < awardInfo.length; j++) {
                    if (awardInfo[j] == 0){
                        continue;
                    }
                    if (bonus[j].compareTo(BigDecimal.ZERO) == -1){
                        e.setBigPrizeStatus(true);
                    }else{
                        if(e.getPlayType().equals(WordConstant.SINGLE)) {
                            total = total.add(bonus[j].multiply(BigDecimal.valueOf(awardInfo[j])).multiply(BigDecimal.valueOf(e.getTimes())));

                        }else {
                            if(!e.getBigPrizeStatus()) {
                                total = total.add(bonus[j].multiply(BigDecimal.valueOf(awardInfo[j])).multiply(BigDecimal.valueOf(e.getTimes())));
                            }
                        }

                    }
                }
            }
            total = total.multiply(amount);
            e.setOpenPrizeStatus(OpenPrizeStatus.opened.getValue());
            if (e.getBigPrizeStatus()) {
                e.setPrizeStatus(PrizeStatus.awarded.getValue());
                e.setReturnPrizeStatus(2);
                e.setBonus(total);
            }else if(total.compareTo(BigDecimal.ZERO) == 1){
                e.setPrizeStatus(PrizeStatus.awarded.getValue());
                e.setBonus(total);
            }else {
                e.setPrizeStatus(PrizeStatus.losing.getValue());
            }
            if (e.getPrizeStatus().equals(PrizeStatus.awarded.getValue())) {
                if (!e.getBigPrizeStatus()){
                    userBalanceService.reward(String.valueOf(e.getUserId()), e.getTicketId(), e.getBonus());
                    e.setReturnPrizeStatus(1);
                }
            }
            e.setBonusTime(new Date());
            bettingTicketService.updateBettingTicket(e);
            ticketId.add(e.getTicketId());
        });

        return ticketId.toArray(new Long[ticketId.size()]);

    }

//    public static void main(String[] args) {
//        String lotteryNumber = "2019072%01,02,03,04,05,32,33:02,03@14";
//        String[] all = lotteryNumber.split(CommonConstant.COMMON_AT_STR)[0].split(CommonConstant.PERCENT_SPLIT_STR)[1].split(CommonConstant.SPACE_SPLIT_STR);
//        BigDecimal total = BigDecimal.ZERO;
//        String re ="01,02,03,04,05,06:01";
//        String[] lotteryNum = re.split(CommonConstant.COMMON_COLON_STR);
//
//        String[] lotteryRedBall = lotteryNum[0].split(CommonConstant.COMMA_SPLIT_STR);
//        String lotteryBlueBall = lotteryNum[1].split(CommonConstant.COMMA_SPLIT_STR)[0];
//        for (int i = 0; i < all.length; i++) {
//            String[] sAll = all[i].split(CommonConstant.COMMON_COLON_STR);
//            String[] redAllArr = sAll[0].split(CommonConstant.COMMON_AND_STR);
//            String[] danMa = {};
//            String[] tuoMa = {};
//            String[] blueBall = sAll[1].split(CommonConstant.COMMA_SPLIT_STR);
//            if (redAllArr.length == 2) {
//                danMa = redAllArr[0].split(CommonConstant.COMMA_SPLIT_STR);
//                tuoMa = redAllArr[1].split(CommonConstant.COMMA_SPLIT_STR);
//            } else {
//                tuoMa = redAllArr[0].split(CommonConstant.COMMA_SPLIT_STR);
//            }
//            SsqBonusCalculationServiceImpl ssqBonusCalculationService = new SsqBonusCalculationServiceImpl();
//            int[] awardInfo = ssqBonusCalculationService.analyseBidAwardLevels(danMa, tuoMa, blueBall, lotteryRedBall, lotteryBlueBall);
//            System.out.println(JSON.toJSONString(awardInfo));
//        }
//
//    }
    /**
     * @param bidCoreRedBall 胆码红球
     * @param bidRedBall     拖码红球
     * @param bidBlueBall    蓝球
     * @param targetRedBall  中奖红球
     * @param targetBlueBall 中奖蓝球
     * @return
     */
    public int[] analyseBidAwardLevels(String[] bidCoreRedBall, String[] bidRedBall, String[] bidBlueBall,
                                       String[] targetRedBall, String targetBlueBall) {
        int[] result = new int[]{0, 0, 0, 0, 0, 0};
        int[] bingoNum = bingoNum(bidCoreRedBall, bidRedBall, bidBlueBall, targetRedBall, targetBlueBall);
        // 返回{拖码红球中奖数，蓝球中奖数，胆码红球中奖数}
        for (int i = 0; i < levelInfo.length; i++) {
            int cout = bingoRedMultiple(levelInfo[i][1], bingoNum[2], bingoNum[0], bidCoreRedBall.length,
                    bidRedBall.length) * bingoBlueMultiple(levelInfo[i][2], bingoNum[1], bidBlueBall);
            result[levelInfo[i][0] - 1] += cout;
        }
        return result;
    }

    /*
     * 返回篮球中奖的倍数
     */
    private int bingoBlueMultiple(int requestBlue, int bingoBlue, String[] bidBlueBall) {
        if (requestBlue == 1) // 要求篮球要中
        {
            return bingoBlue; // 篮球中返回1，否则返回0
        } else {
            return bidBlueBall.length - bingoBlue; // 所选篮球的个数减去中了的
        }
    }

    /**
     * 返回红球中奖的倍数
     *
     * @param requestRed           要中奖红球数
     * @param bingoCore            胆码中奖数
     * @param bingoRed             拖码中奖数
     * @param bidCoreRedBallLength 胆码红球数
     * @param bidCoreRedBallLength 拖码红球数
     */
    private int bingoRedMultiple(int requestRed, int bingoCore, int bingoRed, int bidCoreRedBallLength,
                                 int bidRedBallLength) {
        int maxBingoRed = Math.min(6 - bidCoreRedBallLength, bingoRed); // 最多中奖拖码个数
        /*
         * 如果胆码+拖码中奖数不足，则不可能 如果是胆拖，且胆码中奖数已经超过要求，则不可能
         */
        if (maxBingoRed + bingoCore < requestRed || (maxBingoRed >= 0 && bingoCore > requestRed)) {
            return 0;
        }
        return combine(bingoRed, requestRed - bingoCore) * combine(bidRedBallLength -
                bingoRed, 6 - bidCoreRedBallLength - requestRed + bingoCore);
    }

    /**
     * 计算排列组合的值
     *
     * @param total
     * @param select
     * @return
     */
    public static int combine(int total, int select) {
        if (select > total) {
            return 0;
        } else if (select == total) {
            return 1;
        } else if (total == 0) {
            return 1;
        } else {
            if (select > total / 2)
                select = total - select;

            long result = 1;
            for (int i = total; i > total - select; i--) {
                result *= i;
                if (result < 0)
                    return -1;
            }
            for (int j = select; j > 0; j--) {
                result /= j;
            }
            if (result > Integer.MAX_VALUE)
                return -1;
            return (int) result;
        }
    }

    /**
     * 返回红蓝球中了的个数
     *
     * @return
     */
    private int[] bingoNum(String[] bidCoreRedBall, String[] bidRedBall, String[] bidBlueBall, String[] targetRedBalls,
                           String targetBlueBall) {
        int[] numArray = {0, 0, 0};
        int j = 0;
        for (int i = 0; i < bidRedBall.length; i++) {
            int compareResult = bidRedBall[i].compareTo(targetRedBalls[j]);
            if (compareResult == 0) {
                j++;
                numArray[0]++;
            } else if (compareResult > 0) {
                j++;
                i--;
            }
            if (j >= targetRedBalls.length) {
                break;
            }
            continue;
        }

        j = 0;
        for (int i = 0; i < bidCoreRedBall.length; i++) {
            int compareResult = bidCoreRedBall[i].compareTo(targetRedBalls[j]);
            if (compareResult == 0) {
                j++;
                numArray[2]++;
            } else if (compareResult > 0) {
                j++;
                i--;
            }
            if (j >= targetRedBalls.length) {
                break;
            }
            continue;
        }

        for (String blueBall : bidBlueBall) {
            if (blueBall.equalsIgnoreCase(targetBlueBall)) {
                numArray[1] = 1;
            }
        }
        return numArray;
    }
}
