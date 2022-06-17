/*
package com.linglong.lottery_backend.order.producer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
public class ProducerOrder {

    @Value("${rocketmq.name-server}")
    private String namesrvAddr;
    private static final String GROUP_NAME = "lottery_match";
    private static final Logger logger = LoggerFactory.getLogger(ProducerOrder.class);

    @Bean
    public DefaultMQProducer getRocketMQProducer() {
        DefaultMQProducer producer = new DefaultMQProducer(GROUP_NAME);
        producer.setNamesrvAddr(namesrvAddr);
        producer.setSendMsgTimeout(10000);
        try {
            producer.start();
        } catch (MQClientException e) {
            logger.error("producer is error {}", e.getErrorMessage());
        }

        return producer;
    }
}
*/
