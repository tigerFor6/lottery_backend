//package com.linglong.lottery_backend.listener.service;
//
//import com.alibaba.fastjson.JSON;
//import com.linglong.lottery_backend.common.service.JpaBatch;
//import com.linglong.lottery_backend.listener.LotteryTags;
//import com.linglong.lottery_backend.prize.entity.TblBettingGroupDetails;
//import com.linglong.lottery_backend.prize.entity.TblBettingTicketDetails;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.rocketmq.client.producer.SendCallback;
//import org.apache.rocketmq.client.producer.SendResult;
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import javax.transaction.Transactional;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@Slf4j
//public class PrizeResultListenerService {
//
//    //private static final Logger logger = LoggerFactory.getLogger(PrizeResultListenerService.class);
//
//    @Autowired
//    JpaBatch jpaBatch;
//
//    @Resource
//    private RocketMQTemplate rocketMQTemplate;
//
////    @Autowired
////    DefaultMQProducer defaultMQProducer;
//
//    @Transactional
//    @Deprecated
//    public void updateBatchBettingGroupDetails(List<TblBettingGroupDetails> data) {
//        jpaBatch.batchUpdate(data);
//    }
//
//
////    public void sendToPrizeResultStatistics(Set<Long> orderIdSet) {
////        if (orderIdSet.isEmpty()){
////            return;
////        }
////        rocketMQTemplate.asyncSend("lottery:" + LotteryTags.statistics, MessageBuilder.withPayload(JSON.toJSONString(orderIdSet)).build(), new SendCallback() {
////            @Override
////            public void onSuccess(SendResult sendResult) {
////                log.info("send to "+LotteryTags.statistics+" success body:{}",JSON.toJSONString(sendResult));
////            }
////
////            @Override
////            public void onException(Throwable throwable) {
////                log.info("send to "+LotteryTags.statistics+" fail:",throwable);
////            }
////        });
////    }
//
//    public void sendPrizeResultToOrder(List<Map<String, Object>> sendList) {
//        if (sendList.isEmpty()){
//            return;
//        }
//        rocketMQTemplate.asyncSend("lottery:" + LotteryTags.bettingResult, MessageBuilder.withPayload(JSON.toJSONString(sendList)).build(), new SendCallback() {
//            @Override
//            public void onSuccess(SendResult sendResult) {
//                log.info("send to "+LotteryTags.bettingResult+" success body:{}",JSON.toJSONString(sendResult));
//            }
//
//            @Override
//            public void onException(Throwable throwable) {
//                log.info("send to "+LotteryTags.bettingResult+" fail:",throwable);
//            }
//        });
//    }
//
////    public void sendWinningRecordToTransaction(Set<CommonRecord> recordSet) {
////        if (recordSet.isEmpty()){
////            return;
////        }
////        rocketMQTemplate.asyncSend("lottery:" + LotteryTags.transaction, MessageBuilder.withPayload(JSON.toJSONString(new LinkedList<>(recordSet))).build(), new SendCallback() {
////            @Override
////            public void onSuccess(SendResult sendResult) {
////                log.info("send to "+LotteryTags.transaction+" success body:{}",JSON.toJSONString(sendResult));
////            }
////
////            @Override
////            public void onException(Throwable throwable) {
////                log.info("send to "+LotteryTags.transaction+" fail:",throwable);
////            }
////        });
////    }
//
//    @Transactional
//    public void updateBatchBettingTicketDetails(List<TblBettingTicketDetails> data) {
//        if (data.isEmpty()){
//            return;
//        }
//        log.info("updateBatchBettingTicketDetails: {}", data);
//        jpaBatch.batchUpdate(data);
//    }
//
////    public void sendToPrizeTicketResult(Set<Long> ticketIdSet) {
////        if (ticketIdSet.isEmpty()){
////            return;
////        }
////        rocketMQTemplate.asyncSend("lottery:" + LotteryTags.prizeResult, MessageBuilder.withPayload(JSON.toJSONString(ticketIdSet)).build(), new SendCallback() {
////            @Override
////            public void onSuccess(SendResult sendResult) {
////                log.info("send to "+LotteryTags.prizeResult+" success body:{}",sendResult.getMsgId());
////            }
////
////            @Override
////            public void onException(Throwable throwable) {
////                log.info("error data: {}",ticketIdSet);
////                log.error("send to "+LotteryTags.prizeResult+" fail:",throwable);
////            }
////        });
////    }
//
////    private void sendMessage(Message message) throws RemotingException, MQClientException, InterruptedException {
////        defaultMQProducer.send(message,new SendCallback() {
////            @Override
////            public void onSuccess(SendResult sendResult) {}
////            @Override
////            public void onException(Throwable e) {
////                logger.error("send to topic error",e);
////                logger.error("send message"+JSON.toJSONString(message));
////            }
////        });
////    }
//}
