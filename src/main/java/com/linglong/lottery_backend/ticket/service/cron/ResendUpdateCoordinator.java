package com.linglong.lottery_backend.ticket.service.cron;

import com.linglong.lottery_backend.ticket.bean.ResendUpdateUnit;
import com.linglong.lottery_backend.ticket.service.LotteryBetService;
import com.linglong.lottery_backend.ticket.thread.ResendUpdateDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by ynght on 2019-04-28
 */
@Component
@Slf4j
public class ResendUpdateCoordinator {

    public static AtomicBoolean SWITCH_ON = new AtomicBoolean(false);
    private final ExecutorService singleExec = Executors.newSingleThreadExecutor();
    private static Map<Integer, ThreadPoolExecutor> RESEND_UPDATE_EXECUTOR_MAP = new HashMap<>();
    // 查询队列任务队列，核心
    public static LinkedBlockingQueue<ResendUpdateUnit> RESEND_RESULT_QUEUE = new LinkedBlockingQueue<>();


    @Autowired
    private LotteryBetService lotteryBetService;

    @PostConstruct
    public void init() {
        if (SWITCH_ON.compareAndSet(false, true)) {
            singleExec.execute(new ResendUpdateDispatcher(this, lotteryBetService));
        }
    }

    public ThreadPoolExecutor getUpdateExecutor(Integer gameType) {
        if (!RESEND_UPDATE_EXECUTOR_MAP.containsKey(gameType)) {
            synchronized (gameType.toString().concat("getUpdateExecutor").intern()) {
                if (!RESEND_UPDATE_EXECUTOR_MAP.containsKey(gameType)) {
                    RESEND_UPDATE_EXECUTOR_MAP.put(gameType, new ThreadPoolExecutor(3, 3, 60, TimeUnit.SECONDS, new
                            LinkedBlockingQueue<>()));
                }
            }
        }
        return RESEND_UPDATE_EXECUTOR_MAP.get(gameType);
    }
}
