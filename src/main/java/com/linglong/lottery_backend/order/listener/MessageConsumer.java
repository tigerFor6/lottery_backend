/*
package com.linglong.lottery_backend.order.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Service
public class MessageConsumer implements MessageListenerConcurrently {
    @Value("${rocketmq.name-server}")
    private String namesrvAddr;

    private final DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("lottery_prize_order");

    @PostConstruct
    public void start() {
        try {
            consumer.setNamesrvAddr(namesrvAddr);

            //从消息队列头部开始消费
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

            //设置广播消费模式
            consumer.setMessageModel(MessageModel.BROADCASTING);

            //订阅主题
            consumer.subscribe("prize", "order");

            //注册消息监听器
            consumer.registerMessageListener(this);

            //启动消费端
            consumer.start();

            log.info("Message Consumer Start...");
            System.err.println("Message Consumer Start...");
        } catch (MQClientException e) {
            log.error("Message Consumer Start Error!!", e);
        }

    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        if (CollectionUtils.isEmpty(msgs)) {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        msgs.stream()
                .forEach(msg -> {
                    try {
                        String messageBody = new String(msg.getBody(), RemotingHelper.DEFAULT_CHARSET);
                        log.info("Message Consumer: Handle New Message: messageId:{}, topic:{}, tags:{}, keys:{}, messageBody:{}"
                                , msg.getMsgId(), msg.getTopic(), msg.getTags(), msg.getKeys(), messageBody);
                        System.err.println("Message Consumer: Handle New Message: messageId: " + msg.getMsgId() + ",topic: " + msg.getTopic() + ",tags: " + msg.getTags());
                    } catch (Exception e) {
                        log.error("Consume Message Error!!", e);
                    }
                });
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

}
*/
