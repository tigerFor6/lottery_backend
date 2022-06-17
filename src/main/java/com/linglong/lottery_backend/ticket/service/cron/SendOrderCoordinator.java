package com.linglong.lottery_backend.ticket.service.cron;

import com.alibaba.fastjson.JSON;
import com.linglong.lottery_backend.ticket.cache.GameCache;
import com.linglong.lottery_backend.ticket.bean.BetUnit;
import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.thread.BetDispatcher;
import com.linglong.lottery_backend.ticket.thread.PriorityThreadPoolExecutor;
import com.linglong.lottery_backend.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by ynght on 2019-04-26
 */
@Component
@Slf4j
public class SendOrderCoordinator {
    public static final LinkedBlockingQueue<BetUnit> BET_QUEUE = new LinkedBlockingQueue<>();
    public static AtomicBoolean SWITCH_ON = new AtomicBoolean(false);
    // 用于加锁使用
    private static Object object = new Object();
    private static ThreadPoolExecutor betExec = new PriorityThreadPoolExecutor(30, 30, 5 * 60, TimeUnit.SECONDS, new
            PriorityBlockingQueue<>());
//    private static Map<Long, ThreadPoolExecutor> betHfExecMap = new HashMap<>();
    private final ExecutorService singleExec = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void init() {
        if (SWITCH_ON.compareAndSet(false, true)) {
            singleExec.execute(new BetDispatcher(this));
        }
    }

    public void addToBet(Integer gameType, Integer platformId, Long orderId, List<TicketDto> ticketDtoList) {
        try {
            BetUnit betUnit = new BetUnit(gameType, platformId, orderId, ticketDtoList);
            log.info("addToBet body: {}", JSON.toJSONString(betUnit));
            BET_QUEUE.put(betUnit);
        } catch (Exception e) {
            log.error("addToBet error." + CommonUtil.mergeUnionKey(gameType, platformId, orderId,
                    ticketDtoList), e);
        }
    }

    //非高频彩使用betExec，高频彩按平台独享
    public ThreadPoolExecutor getExecutor(BetUnit betUnit) {
        Integer gameType = betUnit.getGameType();
        if (GameCache.getGame(gameType).getType().equals(Integer.valueOf(1))) {
            return betExec;
        }
        //后面有高频了可按平台分发不同的线程池
        return betExec;
    }
}
