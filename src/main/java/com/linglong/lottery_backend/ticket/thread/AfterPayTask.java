package com.linglong.lottery_backend.ticket.thread;

import com.linglong.lottery_backend.ticket.constant.ResultConstant;
import com.linglong.lottery_backend.ticket.service.LotteryBetService;
import com.linglong.lottery_backend.ticket.service.SplitService;
import com.linglong.lottery_backend.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;

/**
 * Created by ynght on 2019-04-20
 */
@Slf4j
public class AfterPayTask implements Runnable {
    private Long orderId;
    private Integer gameType;
    private String userId;
    private Timestamp deadline;
    private SplitService splitService;
    private LotteryBetService lotteryBetService;

    public AfterPayTask(Long orderId, String userId, Integer gameType, Timestamp deadline, SplitService splitService,
                        LotteryBetService lotteryBetService) {
        this.gameType = gameType;
        this.orderId = orderId;
        this.userId = userId;
        this.deadline = deadline;
        this.splitService = splitService;
        this.lotteryBetService = lotteryBetService;
    }

    @Override
    public void run() {
        try {
            log.info("[SPLIT] execute start time" + DateUtil.getCurrentTimestamp() + ",orderId:" + orderId);
            int ret = splitService.splitOrder(orderId, userId, deadline);
            log.info("[SPLIT] split order over orderId:" + orderId + ",split ret:" + ret);
            if (ret == ResultConstant.SUCCESS) {
                lotteryBetService.doBet(orderId);
            }
        } catch (Exception ex) {
            log.error("afterTask execute is fail.orderId:" + orderId, ex);

        }
    }
}
