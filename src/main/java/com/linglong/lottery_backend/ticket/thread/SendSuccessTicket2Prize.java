package com.linglong.lottery_backend.ticket.thread;

import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

import java.util.List;

/**
 * Created by ynght on 2019-04-28
 */
@Slf4j
public class SendSuccessTicket2Prize implements Runnable {
    private List<BettingTicket> tickets;
    private RocketMQTemplate rocketMQTemplate;

    public SendSuccessTicket2Prize(List<BettingTicket> tickets, RocketMQTemplate rocketMQTemplate) {
        this.tickets = tickets;
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @Override
    public void run() {
        if (null == tickets || tickets.isEmpty()) {
            return;
        }
        for (BettingTicket ticket : tickets) {
        }
        /*
        rocketMQTemplate.asyncSend("lottery:" + LotteryTags.betting, MessageBuilder.withPayload().build(),
                new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("send to " + LotteryTags.betting + " success body:{}" + sendResult.toString());
            }

            @Override
            public void onException(Throwable throwable) {
                log.info("send to " + LotteryTags.betting + " fail:", throwable);
            }
        });
        */
    }
}
