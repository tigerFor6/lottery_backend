package com.linglong.lottery_backend.ticket.thread;

import com.linglong.lottery_backend.ticket.bean.ResendUpdateUnit;
import com.linglong.lottery_backend.ticket.service.LotteryBetService;
import com.linglong.lottery_backend.ticket.service.cron.ResendUpdateCoordinator;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Created by ynght on 2019-04-28
 */
@Slf4j
public class ResendUpdateDispatcher implements Runnable {

    private ResendUpdateCoordinator resendUpdateCoordinator;
    private LotteryBetService lotteryBetService;

    public ResendUpdateDispatcher(ResendUpdateCoordinator resendUpdateCoordinator, LotteryBetService lotteryBetService) {
        this.resendUpdateCoordinator = resendUpdateCoordinator;
        this.lotteryBetService = lotteryBetService;
    }

    @Override
    public void run() {
        log.info("[ResendUpdateDispatcher] start");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ResendUpdateUnit resendUpdateUnit = ResendUpdateCoordinator.RESEND_RESULT_QUEUE.poll(5, TimeUnit.SECONDS);
                if (null != resendUpdateUnit) {
                    //从对应彩种的线程池中获取线程执行发送
                    resendUpdateCoordinator.getUpdateExecutor(resendUpdateUnit.getGameType()).submit(new ResendUpdateTask
                            (resendUpdateUnit, lotteryBetService));
                }
            } catch (Exception e) {
                log.warn("[ResendUpdateDispatcher] error " + e.getMessage(), e);
            }
        }
        resendUpdateCoordinator.SWITCH_ON.compareAndSet(true, false);
        resendUpdateCoordinator.init();
        log.error("[ResendUpdateDispatcher] aborted, do not worry we will restart a new one, but pls check the " +
                "reason! ");
    }
}
