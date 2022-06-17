package com.linglong.lottery_backend.listener;

import com.alibaba.fastjson.JSON;
import com.linglong.lottery_backend.activity.entity.TblUserActivity;
import com.linglong.lottery_backend.activity.entity.bean.DrawActivity;
import com.linglong.lottery_backend.activity.service.TblActivityService;
import com.linglong.lottery_backend.activity.service.TblUserActivityService;
import com.linglong.lottery_backend.lottery.digital.cache.TblDigitalResultsCache;
import com.linglong.lottery_backend.lottery.digital.entity.TblDigitalResults;
import com.linglong.lottery_backend.prize.bean.part.SsqBonusCalculation;
import com.linglong.lottery_backend.prize.entity.TblBettingTicketDetails;
import com.linglong.lottery_backend.prize.task.BonusCalculationTask;
import com.linglong.lottery_backend.prize.task.PrizeAsyncExecutorTask;
import com.linglong.lottery_backend.ticket.task.TicketAsyncExecutorTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
//@RocketMQMessageListener(topic = "lottery", consumerGroup = "lottery",
//        consumeMode = ConsumeMode.CONCURRENTLY, messageModel = MessageModel.CLUSTERING)
public class LotteryListener implements MessageListenerConcurrently {

    @Autowired
    PrizeAsyncExecutorTask prizeAsyncExecutorTask;

//    @Autowired
//    private HitPrizeExecutor hitPrizeExecutor;

    @Autowired
    TicketAsyncExecutorTask ticketAsyncExecutorTask;

    @Autowired
    BonusCalculationTask bonusCalculationTask;

    @Autowired
    TblActivityService tblActivityService;

    @Autowired
    TblUserActivityService tblUserActivityService;

    @Autowired
    private TblDigitalResultsCache tblDigitalResultsCache;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeOrderlyContext) {
        list.forEach(messageExt -> {
            try {
                String body = new String(messageExt.getBody());
                log.warn("LotteryListener consumeMessage："+body);
                switch (messageExt.getTags()) {
                    //                case LotteryTags.statistics:
                    //                    //返奖模块 计算中奖
                    //                    log.info("LotteryListener statistics body: {}", body);
                    //                    List<Long> result = JSON.parseArray(body, Long.class);
                    //                    prizeAsyncExecutorTask.doStatisticsGroupResult(result);
                    //                    break;
                    case LotteryTags.betting:
                        //返奖模块 出票投注详情
                        log.info("LotteryListener betting body: {}", body);
                        //BettingOrder bettingOrder = assembleBettingOrder(body);
                        //prizeAsyncExecutorTask.doSaveBettingOrder(bettingOrder);
                        List<TblBettingTicketDetails> details = JSON.parseArray(body, TblBettingTicketDetails.class);
                        prizeAsyncExecutorTask.doSaveBettingTicketDetails(details);
                        return ;
                    case LotteryTags.bettingDigitalSsq:
                        log.info("LotteryListener betting digital ssq body:{}",body);
                        tblDigitalResultsCache.refresh(3);
                        SsqBonusCalculation results = JSON.parseObject(body, SsqBonusCalculation.class);
                        bonusCalculationTask.doBonusCalculation(results);
                        return ;
                    case LotteryTags.coupon:
                        //订单模块 中奖结果通知
                        log.info("LotteryListener coupon body: {}", body);
                        DrawActivity drawActivity = JSON.parseObject(body, DrawActivity.class);
                        TblUserActivity userActivity = tblUserActivityService.findUserActivityByUserIdAndActivityId(drawActivity.getUserId(),drawActivity.getActivity().getActivityId());
                        if (null == userActivity){
                            return ;
                        }
                        if (userActivity.getStatus().intValue() != 1){
                            int status = 1;
                            try {
                                tblActivityService.participateInActivity(drawActivity.getActivity(),drawActivity.getUserId());
                            }catch (Exception e){
                                status = 2;
                                log.error("LotteryListener coupon error",e);
                            }finally {
                                //记录用户参与活动
                                userActivity.setStatus(status);
                                tblUserActivityService.saveUserActivity(userActivity);
                            }
                        }
                        return ;
                    //                case LotteryTags.transaction:
                    //                    //交易模块 中奖结果通知
                    //                    log.info("LotteryListener transaction body: {}", body);
                    //                    List<BettingTicket> bettingTickets = JSON.parseArray(body, BettingTicket.class);
                    //                    List<CommonRecord> commonRecords = bettingTickets.stream().map(e->{
                    //                    CommonRecord record = new CommonRecord();
                    //                    record.setOrderId(e.getOrderId());
                    //                    record.setUserId(e.getUserId());
                    //                    record.setAmount(e.getBonus());
                    //                    record.setVoucherNo(e.getTicketId());
                    //                    return record;
                    //                }).collect(Collectors.toList());
                    //                    prizeAsyncExecutorTask.doUpdateUserAccount(commonRecords);
                    //                    break;
                    case LotteryTags.prizeResult:
                        //出票模块 中奖结果通知
                        log.info("LotteryListener ticket body: {}", body);
                        List<Long> ticketId = JSON.parseArray(body, Long.class);
                        prizeAsyncExecutorTask.doStatisticsTicketResult(ticketId);
                        return ;
                    //                case LotteryTags.bjgcRepeatTicket:
                    //                    //TODO 处理中的北京公彩出票 二次请求
                    //                    List<Ticket> tickets = JSON.parseArray(body, Ticket.class);
                    //                    ticketAsyncExecutorTask.doRepeatQueryTicketStatus(tickets);
                    //                    break;
                    case LotteryTags.bettingDigitalshx:
                        log.info("LotteryListener bettingDigitalshx TblDigitalResults body: {}", body);
                        tblDigitalResultsCache.refresh(4);
                        prizeAsyncExecutorTask.doCalculateShx115Result(JSON.parseObject(body, TblDigitalResults.class));
                        return ;
                    case LotteryTags.bettingDigitaldlt:
                        log.info("LotteryListener bettingDigitaldlt period: {}", body);
                        tblDigitalResultsCache.refresh(5);
                        prizeAsyncExecutorTask.doCalculateDLTResult(JSON.parseObject(body, SsqBonusCalculation.class));
                        return ;
                    case LotteryTags.bettingDigitalpl3:
                        log.info("LotteryListener bettingDigitalpl3 period: {}", body);
                        tblDigitalResultsCache.refresh(6);
                        prizeAsyncExecutorTask.doCalculatePl3Result(JSON.parseObject(body, SsqBonusCalculation.class));
                        return ;
                    case LotteryTags.bettingDigitalpl5:
                        log.info("LotteryListener bettingDigitalpl5 period: {}", body);
                        prizeAsyncExecutorTask.doCalculatePl5Result(JSON.parseObject(body, SsqBonusCalculation.class));
                        return ;
                    default:
                        log.info("未知的tags:"+body);
                        return ;
                }
            } catch (Exception e) {
                log.error("LotteryListener body: {}" , messageExt);
                log.error("LotteryListener error", e);
                return ;
            }
            
        });
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    //    @Override
//    public void onMessage(MessageExt messageExt) {
//        try {
//            String body = new String(messageExt.getBody());
//            switch (messageExt.getTags()) {
////                case LotteryTags.statistics:
////                    //返奖模块 计算中奖
////                    log.info("LotteryListener statistics body: {}", body);
////                    List<Long> result = JSON.parseArray(body, Long.class);
////                    prizeAsyncExecutorTask.doStatisticsGroupResult(result);
////                    break;
//                case LotteryTags.betting:
//                    //返奖模块 出票投注详情
//                    log.info("LotteryListener betting body: {}", body);
//                    //BettingOrder bettingOrder = assembleBettingOrder(body);
//                    //prizeAsyncExecutorTask.doSaveBettingOrder(bettingOrder);
//                    List<TblBettingTicketDetails> details = JSON.parseArray(body, TblBettingTicketDetails.class);
//                    prizeAsyncExecutorTask.doSaveBettingTicketDetails(details);
//                    break;
//                case LotteryTags.bettingDigitalSsq:
//                    log.info("LotteryListener betting digital ssq body:{}",body);
//                    tblDigitalResultsCache.refresh(3);
//                    SsqBonusCalculation results = JSON.parseObject(body, SsqBonusCalculation.class);
//                    bonusCalculationTask.doBonusCalculation(results);
//                    break;
//                case LotteryTags.coupon:
//                    //订单模块 中奖结果通知
//                    log.info("LotteryListener coupon body: {}", body);
//                    DrawActivity drawActivity = JSON.parseObject(body, DrawActivity.class);
//                    TblUserActivity userActivity = tblUserActivityService.findUserActivityByUserIdAndActivityId(drawActivity.getUserId(),drawActivity.getActivity().getActivityId());
//                    if (null == userActivity){
//                        return;
//                    }
//                    if (userActivity.getStatus().intValue() != 1){
//                        int status = 1;
//                        try {
//                            tblActivityService.participateInActivity(drawActivity.getActivity(),drawActivity.getUserId());
//                        }catch (Exception e){
//                            status = 2;
//                            log.error("LotteryListener coupon error",e);
//                        }finally {
//                            //记录用户参与活动
//                            userActivity.setStatus(status);
//                            tblUserActivityService.saveUserActivity(userActivity);
//                        }
//                    }
//                    break;
////                case LotteryTags.transaction:
////                    //交易模块 中奖结果通知
////                    log.info("LotteryListener transaction body: {}", body);
////                    List<BettingTicket> bettingTickets = JSON.parseArray(body, BettingTicket.class);
////                    List<CommonRecord> commonRecords = bettingTickets.stream().map(e->{
////                    CommonRecord record = new CommonRecord();
////                    record.setOrderId(e.getOrderId());
////                    record.setUserId(e.getUserId());
////                    record.setAmount(e.getBonus());
////                    record.setVoucherNo(e.getTicketId());
////                    return record;
////                }).collect(Collectors.toList());
////                    prizeAsyncExecutorTask.doUpdateUserAccount(commonRecords);
////                    break;
//                case LotteryTags.prizeResult:
//                    //出票模块 中奖结果通知
//                    log.info("LotteryListener ticket body: {}", body);
//                    List<Long> ticketId = JSON.parseArray(body, Long.class);
//                    prizeAsyncExecutorTask.doStatisticsTicketResult(ticketId);
//                    break;
////                case LotteryTags.bjgcRepeatTicket:
////                    //TODO 处理中的北京公彩出票 二次请求
////                    List<Ticket> tickets = JSON.parseArray(body, Ticket.class);
////                    ticketAsyncExecutorTask.doRepeatQueryTicketStatus(tickets);
////                    break;
//                case LotteryTags.bettingDigitalshx:
//                    log.info("LotteryListener bettingDigitalshx TblDigitalResults body: {}", body);
//                    tblDigitalResultsCache.refresh(4);
//                    prizeAsyncExecutorTask.doCalculateShx115Result(JSON.parseObject(body, TblDigitalResults.class));
//                    break;
//                case LotteryTags.bettingDigitaldlt:
//                    log.info("LotteryListener bettingDigitaldlt period: {}", body);
//                    tblDigitalResultsCache.refresh(5);
//                    prizeAsyncExecutorTask.doCalculateDLTResult(JSON.parseObject(body, SsqBonusCalculation.class));
//                    break;
//                case LotteryTags.bettingDigitalpl3:
//                    log.info("LotteryListener bettingDigitalpl3 period: {}", body);
//                    tblDigitalResultsCache.refresh(6);
//                    prizeAsyncExecutorTask.doCalculatePl3Result(JSON.parseObject(body, SsqBonusCalculation.class));
//                    break;
//                case LotteryTags.bettingDigitalpl5:
//                    log.info("LotteryListener bettingDigitalpl5 period: {}", body);
//                    prizeAsyncExecutorTask.doCalculatePl5Result(JSON.parseObject(body, SsqBonusCalculation.class));
//                    break;
//                default:
//                    log.info("未知的tags:"+body);
//                    break;
//            }
//        } catch (Exception e) {
//            log.error("LotteryListener body: {}" , messageExt);
//            log.error("LotteryListener error", e);
//        }
//
//    }

//    public static void main(String[] args) {
//        Map<String,Object> param = new HashMap<>();
//        param.put("activity",new TblActivity());
//        param.put("userId","123456789");
//
//        Map<String,Object> couponMap = JSON.parseObject(JSON.toJSONString(param), HashMap.class);
//        TblActivity activity = (TblActivity) couponMap.get("activity");
//        String userId = (String) couponMap.get("userId");
//        System.out.println(activity);
//        System.out.println(userId);
//
//    }
//    private BettingOrder assembleBettingOrder(String data) {
//        BettingOrder bettingOrder = JSON.parseObject(data, BettingOrder.class);
//        bettingOrder.getMatchs().forEach(e -> {
//            HashMap<String, List<BettingOdds>> odds = new HashMap<>();
//            e.getPlays().forEach((k, v) -> {
//                HashMap<String, Object> m = JSON.parseObject(v, HashMap.class);
//                List<BettingOdds> list = JSON.parseArray(JSON.toJSONString(m.get("items")), BettingOdds.class);
//                odds.put(k, list);
//            });
//            e.setOdds(odds);
//        });
//        return bettingOrder;
//    }
}
