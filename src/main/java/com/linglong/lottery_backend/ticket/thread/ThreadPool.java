package com.linglong.lottery_backend.ticket.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by ynght on 2019-04-20
 */
public class ThreadPool {
    private static ExecutorService bettingExec = new ThreadPoolExecutor(5, 5, 5 * 60, TimeUnit.SECONDS, new
            LinkedBlockingQueue<>());
    private static ThreadPool instance = new ThreadPool();

    private ThreadPool() {
    }

    public static ThreadPool getInstance() {
        return instance;
    }

    public void executeRunnable(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (runnable instanceof AfterPayTask) {
            bettingExec.execute(runnable);
        }
    }
}
