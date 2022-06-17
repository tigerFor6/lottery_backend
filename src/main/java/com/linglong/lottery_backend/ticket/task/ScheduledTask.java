package com.linglong.lottery_backend.ticket.task;

import com.linglong.lottery_backend.user.account.service.TblUserBalanceService;
import com.linglong.lottery_backend.common.service.JpaBatch;
import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import com.linglong.lottery_backend.ticket.entity.Platform;
import com.linglong.lottery_backend.ticket.enums.PlatformEnum;
import com.linglong.lottery_backend.ticket.service.TblBettingTicketService;
import com.linglong.lottery_backend.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Configuration
@EnableScheduling
public class ScheduledTask {

    @Autowired
    TicketAsyncExecutorTask ticketAsyncExecutorTask;

    @Autowired
    TblBettingTicketService bettingTicketService;


    @Autowired
    JpaBatch jpaBatch;

    @Autowired
    TblUserBalanceService userBalanceService;

    /**
     * 北京公彩票商
     * 定时查询出票状态
     */
    public void queryTicketStatus() {
//        System.out.println("定时任务11111"+ DateUtil.formatDate(new Date(),2));
        Platform platform = PlatformEnum.BJGONGCAI.getPlatform();
        List<BettingTicket> all = this.findBettingTicketByStatus(platform.getPlatformId(), Integer.valueOf(0), Integer.valueOf(1));
        if (all.isEmpty()) {
            return;
        }
        Map<Long, List<BettingTicket>> ticketMap = all.stream().collect(Collectors.groupingBy(BettingTicket::getOrderId));
        ticketMap.forEach((k,v)->{
            ticketAsyncExecutorTask.doRepeatQueryTicketStatus(v);
        });
    }

    /**
     * AOLI票商
     * 定时查询出票状态
     */
    public void queryTicketStatusByAOLI() {
//        log.info("执行queryTicketStatusByAOLI");
        Platform platform = PlatformEnum.AOLI.getPlatform();
        List<BettingTicket> all = this.findBettingTicketByStatus(platform.getPlatformId(), Integer.valueOf(0), Integer.valueOf(1));
        if (all.isEmpty()) {
            return;
        }
        Map<Long, List<BettingTicket>> ticketMap = all.stream().collect(Collectors.groupingBy(BettingTicket::getOrderId));
        ticketMap.forEach((k, v) -> {
            ticketAsyncExecutorTask.doRepeatQueryTicketStatusByAOLI(v);
        });
    }


    private List<BettingTicket> findBettingTicketByStatus(Integer platformId, Integer... status) {
        List<BettingTicket> list = new ArrayList<>();
        for (int i = 0; i < status.length; i++) {
            List<BettingTicket> l = bettingTicketService.findByPlatformIdAndTicketStatus(platformId, status[i]);
            list.addAll(l);
        }
        return list;
    }


    /**
     * 陕11选5，获取每天奖期定时器
     */
    public void shx115SetLotteryTimer() {
//        log.info("执行shx115GetLotteryTimer");
        ticketAsyncExecutorTask.shx115SetLottery(4);
    }


    /**
     * 等待推送出票商定时器
     */
    public void waitPushTicketListTimer() {
//        log.info("执行waitPushTicketListTimer");
        ticketAsyncExecutorTask.waitPushTicketList();
    }


    /**
     * 等待推送SSQ任务
     * 周日、周二、周四21:10 - 21:20 执行两次
     */
    public void waitPushSSQTikcetListTask() {
        log.info("waitPushSSQTikcetListTask");
        ticketAsyncExecutorTask.waitPushSSQTikcetListTask();
    }


    /**
     * 等待推送DLT、PL3、PL5任务
     * 每天早上8点，将所有 待出票-3状态的票 改为 0
     */
    public void waitPushDPPTicketListTask() {
        log.info("waitPushDPPTikcetListTask");
        ticketAsyncExecutorTask.waitPushDPPTikcetListTask();
    }

}