package com.linglong.lottery_backend.ticket.thread;

import com.linglong.lottery_backend.ticket.bean.BetUnit;
import com.linglong.lottery_backend.ticket.cache.GameCache;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.service.cron.SendOrderCoordinator;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by ynght on 2019-04-26
 */
@Slf4j
public class BetDispatcher implements Runnable {
    private SendOrderCoordinator sendOrderCoordinator;

    public BetDispatcher(SendOrderCoordinator sendOrderCoordinator) {
        this.sendOrderCoordinator = sendOrderCoordinator;
    }

    @Override
    public void run() {
        log.info("[BetDispatcher] start");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                BetUnit betUnit = SendOrderCoordinator.BET_QUEUE.poll(1, TimeUnit.SECONDS);
                if (null != betUnit) {
//                    //List<TicketDto> tickets = betUnit.getTickets();
//                    Integer gameType = betUnit.getGameType();
//                    if (GameCache.getGame(gameType).getType() == Game.TYPE_JC) {
//                        //betUnit.getEnterTime()
//                        //获取betUnit 中最小开售时间
//                        //判断开售时间是否过了5分钟
//                        //如果没有过5分钟 弹回BET_QUEUE
//                    }
                    sendOrderCoordinator.getExecutor(betUnit).submit(new BetTask(betUnit));
                }
            } catch (RejectedExecutionException rex) {
                log.info("[BetDispatcher] error ", rex);
            } catch (Exception e) {
                log.warn("[BetDispatcher] error " + e.getMessage(), e);
            }
        }
        sendOrderCoordinator.SWITCH_ON.compareAndSet(true, false);
        sendOrderCoordinator.init();
        log.error("[BetDispatcher] aborted, do not worry we will restart a new one, but pls check the reason! ");
    }
}
