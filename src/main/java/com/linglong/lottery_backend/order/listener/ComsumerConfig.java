//package com.linglong.lottery_backend.order.listener;
//
//import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
//import org.apache.rocketmq.client.exception.MQClientException;
//import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
//import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
///**
// * @Author: qihua.li
// * @since: 2019-04-20
// */
//@Configuration
//public class ComsumerConfig {
//
//    @Bean
//    public DefaultMQPushConsumer getConsumer() throws MQClientException {
//        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("lottery_prize_order");
//        consumer.setNamesrvAddr("39.98.69.232:9876");
//        consumer.setInstanceName(UUID.randomUUID().toString());
//        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
//        consumer.setMessageModel(MessageModel.BROADCASTING);
//        consumer.subscribe("prize", "order");
//        consumer.start();
//        return consumer;
//    }
//}
