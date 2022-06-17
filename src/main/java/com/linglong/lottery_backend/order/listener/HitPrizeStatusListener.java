/*
package com.linglong.lottery_backend.order.listener;

import com.alibaba.fastjson.JSON;
import com.linglong.lottery_backend.order.listener.bean.HitPrizeResult;
import com.linglong.lottery_backend.order.listener.service.HitPrizeExecutorForOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RocketMQMessageListener(topic = "prize", consumerGroup = "lottery_match", consumeMode = ConsumeMode.CONCURRENTLY, messageModel = MessageModel.CLUSTERING,
        selectorExpression = "order", selectorType = SelectorType.TAG)
public class HitPrizeStatusListener implements RocketMQListener<String> {

    //private final HitPrizeExecutorForOrder hitPrizeExecutorForOrder;

    @Override
    public void onMessage(String message) {
        log.info("HitPrizeStatusListener message {}", message);
        //HitPrizeResult hitPrizeResult = JSON.parseObject(message, HitPrizeResult.class);
        //hitPrizeExecutorForOrder.updateOpenPrizeResult(hitPrizeResult);
    }
}
*/
