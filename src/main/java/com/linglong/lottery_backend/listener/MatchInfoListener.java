package com.linglong.lottery_backend.listener;

import com.alibaba.fastjson.JSON;
import com.linglong.lottery_backend.listener.bean.MatchInfo;
import com.linglong.lottery_backend.lottery.match.task.RedisAsyncExecutorTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
//@RocketMQMessageListener(topic = "crawler", consumerGroup = "crawler",
//        consumeMode = ConsumeMode.CONCURRENTLY, messageModel = MessageModel.CLUSTERING,
//        selectorExpression = "updateInfo", selectorType = SelectorType.TAG)
public class MatchInfoListener implements MessageListenerConcurrently {

    @Autowired
    RedisAsyncExecutorTask redisAsyncExecutorTask;

    // private static final Logger logger = LoggerFactory.getLogger(MatchAspect.class);


    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        list.forEach(messageExt -> {
            try {
                String data = new String(messageExt.getBody());
                log.warn("MatchInfoListener consumeMessage："+data);
                MatchInfo matchInfo = JSON.parseObject(data, MatchInfo.class);
                if (null != matchInfo.getTeamId()) {
                    log.info("MatchInfoListener team body:{}", data);
                    redisAsyncExecutorTask.doUpdateTblTeam(matchInfo.getTeamId());
                }
                if (null != matchInfo.getMatchId()) {
                    log.info("MatchInfoListener match body:{}", data);
                    redisAsyncExecutorTask.doUpdateTblMatch(matchInfo.getMatchId(), matchInfo.isMatchResult(),matchInfo.isCancelResult());
                }
                if (null != matchInfo.getSeasonId()) {
                    log.info("MatchInfoListener season body:{}", data);
                    redisAsyncExecutorTask.doUpdateTblSeason(matchInfo.getSeasonId());
                }
                if (null != matchInfo.getSeasonRoundId()) {
                    log.info("MatchInfoListener seasonRound body:{}", data);
                    redisAsyncExecutorTask.doUpdateTblSeasonRound(matchInfo.getSeasonRoundId());
                }
                if (null != matchInfo.getSeasonTeamId()) {
                    log.info("MatchInfoListener seasonTeam body:{}", data);
                    redisAsyncExecutorTask.doUpdateTblSeasonTeam(matchInfo.getSeasonTeamId());
                }
                if (null != matchInfo.getMatchOddsId()) {
                    log.info("MatchInfoListener matchOdds body:{}", data);
                    redisAsyncExecutorTask.doUpdateTblMatchOdds(matchInfo);
                }
                return ;
            } catch (Exception e) {
                log.error("MatchInfoListener error: {}", e);
                return ;
            }
        });
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

//    @Override
//    public void onMessage(String data) {
//        try {
//            MatchInfo matchInfo = JSON.parseObject(data, MatchInfo.class);
//            if (null != matchInfo.getTeamId()) {
//                log.info("MatchInfoListener team body:{}", data);
//                redisAsyncExecutorTask.doUpdateTblTeam(matchInfo.getTeamId());
//            }
//            if (null != matchInfo.getMatchId()) {
//                log.info("MatchInfoListener match body:{}", data);
//                redisAsyncExecutorTask.doUpdateTblMatch(matchInfo.getMatchId(), matchInfo.isMatchResult(),matchInfo.isCancelResult());
//            }
//            if (null != matchInfo.getSeasonId()) {
//                log.info("MatchInfoListener season body:{}", data);
//                redisAsyncExecutorTask.doUpdateTblSeason(matchInfo.getSeasonId());
//            }
//            if (null != matchInfo.getSeasonRoundId()) {
//                log.info("MatchInfoListener seasonRound body:{}", data);
//                redisAsyncExecutorTask.doUpdateTblSeasonRound(matchInfo.getSeasonRoundId());
//            }
//            if (null != matchInfo.getSeasonTeamId()) {
//                log.info("MatchInfoListener seasonTeam body:{}", data);
//                redisAsyncExecutorTask.doUpdateTblSeasonTeam(matchInfo.getSeasonTeamId());
//            }
//            if (null != matchInfo.getMatchOddsId()) {
//                log.info("MatchInfoListener matchOdds body:{}", data);
//                redisAsyncExecutorTask.doUpdateTblMatchOdds(matchInfo);
//            }
//        } catch (Exception e) {
//            log.error("MatchInfoListener error: {}", e);
//        }
//
//    }
}
