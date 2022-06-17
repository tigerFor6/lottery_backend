package com.linglong.lottery_backend.prize.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BatchAsyncTask {

//    @Async
//    public Future<String> dealHaveReturnTask(int i) {
//        log.info("asyncInvokeReturnFuture, parementer=" + i);
//        Future<String> future;
//        try {
//            Thread.sleep(1000 * i);
//            future = new AsyncResult<String>("success:" + i);
//        } catch (InterruptedException e) {
//            future = new AsyncResult<String>("error");
//        }
//        return future;
//    }
//
//    @Async("taskExecutor")
//    public CompletableFuture<String> doUpdateBettingTicket(ImmutableList<BettingTicket> tickets) {
//    }
}
