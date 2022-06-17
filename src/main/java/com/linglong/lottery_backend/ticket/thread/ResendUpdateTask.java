package com.linglong.lottery_backend.ticket.thread;

import com.linglong.lottery_backend.ticket.bean.ResendUpdateUnit;
import com.linglong.lottery_backend.ticket.service.LotteryBetService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by ynght on 2019-04-28
 */
@Slf4j
public class ResendUpdateTask implements Runnable {

    private ResendUpdateUnit resendUpdateUnit;
    private LotteryBetService lotteryBetService;

    public ResendUpdateTask(ResendUpdateUnit resendUpdateUnit, LotteryBetService lotteryBetService) {
        this.resendUpdateUnit = resendUpdateUnit;
        this.lotteryBetService = lotteryBetService;
    }

    @Override
    public void run() {
//        try {
//            long start = System.currentTimeMillis();
//            List<BetResult> results = resendUpdateUnit.getResults();
//            Integer gameType = resendUpdateUnit.getGameType();
//            Integer platformId = resendUpdateUnit.getPlatformId();
//            Long orderId = resendUpdateUnit.getOrderId();
//            List<BetResult> successList = new ArrayList<>();
//            List<Long> ticketIds = new ArrayList<>();
//            // 合作商返回的票状态为投注成功的票集合，以orderId为key批量处理
//            for (BetResult result : results) {
//                if (result.getTicketStatus() == null) {
//                    log.error("返回的票信息有误." + result);
//                } else if (result.getTicketStatus() == BettingTicket.TICKET_STATUS_BET_SUCCESS) {
//                    successList.add(result);
//                } else if (result.getTicketStatus() == BettingTicket.TICKET_STATUS_INIT) {
//                    ticketIds.add(result.getTicketId());
//                }
//            }
//            //更新成功的票
//            List<Runnable> runnableList = lotteryBetService.updateSuccessTickets(orderId, platformId, successList);
//            for (Runnable task : runnableList) {
//                ThreadPool.getInstance().executeRunnable(task);
//            }
//            //再次发送合作商没有收到的票
//            if (!ticketIds.isEmpty()) {
//                lotteryBetService.doBet(gameType, platformId, orderId, ticketIds);
//            }
//        } catch (Exception e) {
//            log.error("重发更新异常！resendUpdateUnit:" + resendUpdateUnit, e);
//        }
    }
}
