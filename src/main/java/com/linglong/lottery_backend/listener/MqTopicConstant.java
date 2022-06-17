package com.linglong.lottery_backend.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class MqTopicConstant {

    public static String nameSrvAdd;

    public static String lotteryTopic;
    public static String crawlerTopic;

    public static String groupLottery;
    public static String groupcrawler;

//    @Value("${mq.lottery.topic}")
//    public void setLotteryTopic(String lotteryTopic) {
//        MqTopicConstant.lotteryTopic = lotteryTopic;
//    }
//
//    @Value("${mq.crawler.topic}")
//    public void setCrawlerTopic(String crawlerTopic) {
//        MqTopicConstant.crawlerTopic = crawlerTopic;
//    }
//
//    @Value("${mq.lottery.group}")
//    public void setLotteryGroup(String lotteryGroup) {
//        MqTopicConstant.lotteryGroup = lotteryGroup;
//    }
//
//    @Value("${mq.crawler.group}")
//    public void setCrawlerGroup(String crawlerGroup) {
//        MqTopicConstant.crawlerGroup = crawlerGroup;
//    }
//
//    @Value("${rocketmq.name-server}")
//    public void setNameSrvAdd(String nameSrvAdd) {
//        MqTopicConstant.nameSrvAdd = nameSrvAdd;
//    }

    @Autowired
    public void createLotteryConsumer(@Autowired LotteryListener lotteryListener,
                                      @Value("${rocketmq.name-server}")String nameSrvAdd,
                                      @Value("${rocketmq.lottery.group}")String groupLottery,
                                      @Value("${rocketmq.lottery.topic}")String lotteryTopic) {
        MqTopicConstant.nameSrvAdd = nameSrvAdd;
        MqTopicConstant.groupLottery = groupLottery;
        MqTopicConstant.lotteryTopic = lotteryTopic;

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupLottery);
        consumer.setNamesrvAddr(nameSrvAdd);
        try {
            log.info("start createLotteryConsumer NAMEADDR："+nameSrvAdd+", TOPIC : "+lotteryTopic+", GROUP："+groupLottery+"");
            consumer.subscribe(lotteryTopic, "*");
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.registerMessageListener(lotteryListener);
            consumer.start();
        }catch (Exception e) {
            log.error("createLotteryConsumer start error ："+e.getMessage());
        }
    }

    @Autowired
    public void createCrawlerConsumer(@Autowired MatchInfoListener matchInfoListener,
                                      @Value("${rocketmq.name-server}")String nameSrvAdd,
                                      @Value("${rocketmq.crawler.group}")String crawlerGroup,
                                      @Value("${rocketmq.crawler.topic}")String crawlerTopic) {
        MqTopicConstant.nameSrvAdd = nameSrvAdd;
        MqTopicConstant.groupcrawler = crawlerGroup;
        MqTopicConstant.crawlerTopic = crawlerTopic;

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(crawlerGroup);
        consumer.setNamesrvAddr(nameSrvAdd);
        try {
            log.info("start createCrawlerConsumer NAMEADDR："+nameSrvAdd+", TOPIC : "+crawlerTopic+", GROUP："+crawlerGroup+"");
            consumer.subscribe(crawlerTopic, "*");
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.registerMessageListener(matchInfoListener);
            consumer.start();
        }catch (Exception e) {
            log.error("createCrawlerConsumer start error ："+e.getMessage());
        }
    }

}
