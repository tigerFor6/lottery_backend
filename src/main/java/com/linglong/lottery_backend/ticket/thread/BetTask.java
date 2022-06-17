package com.linglong.lottery_backend.ticket.thread;

import com.linglong.lottery_backend.ticket.cache.PlatformCache;
import com.linglong.lottery_backend.ticket.bean.BetUnit;
import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.enums.AoliEnum;
import com.linglong.lottery_backend.ticket.platform.PlatformFactory;
import com.linglong.lottery_backend.ticket.platform.PlatformInterface;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by ynght on 2019-04-26
 */
@Slf4j
public class BetTask implements Runnable, Comparable<BetTask> {
    private BetUnit betUnit;

    public BetTask(BetUnit betUnit) {
        this.betUnit = betUnit;
    }

    @Override
    public void run() {
        try {
  //          long start = System.currentTimeMillis();
//            if (betUnit != null && (start - betUnit.getEnterTime()) > IniCache.getIniIntValue(IniConstant.RESEND_UNIT_MAX_DELAY, 10000)) {
//                log.error("[BET-ESTIMATE][BET-TASK-QUEUE-TIME] BetTaskQueueCost " + (start - betUnit.getEnterTime())
//                        + " ms" + " resendUnit:" + betUnit);
//            }
            List<TicketDto> tickets = betUnit.getTickets();
            Integer gameType = betUnit.getGameType();
            Integer platformId = betUnit.getPlatformId();
            if (tickets == null || tickets.isEmpty()) {
                log.error("betUnit is empty. " + betUnit);
                return;
            }
            PlatformInterface pi = PlatformFactory.getInstance().getPlatform(platformId);
            int betNum = PlatformCache.getPlatform(platformId).getBetNum();
            int total = tickets.size() / betNum + (tickets.size() % betNum == 0 ? 0 : 1);
            for (int i = 0; i < total; i++) {
                int end = (i + 1) * betNum > tickets.size() ? tickets.size() : (i + 1) * betNum;
                pi.send2Center(gameType, tickets.subList(i * betNum, end));
            }
        } catch (Exception e) {
            log.error("bet error！betUnit:" + betUnit, e);
        }
    }

    @Override
    public int compareTo(BetTask betTask) {
        try {
            BetUnit otherUnit = betTask.getBetUnit();
            BigDecimal amount = BigDecimal.ZERO;
            for (TicketDto dto : betUnit.getTickets()) {
                amount = amount.add(dto.getTicketAmount());
            }
            BigDecimal otherAmount = BigDecimal.ZERO;
            for (TicketDto otherDto : otherUnit.getTickets()) {
                otherAmount = otherAmount.add(otherDto.getTicketAmount());
            }
            return otherAmount.compareTo(amount);
        } catch (Exception e) {
            log.error("compareTo betTask." + betTask);
        }
        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 59;
        int result = prime * 1 + ((betUnit.getOrderId() == null) ? 0 : betUnit.getOrderId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BetTask other = (BetTask) obj;
        if (betUnit == null) {
            if (other.getBetUnit() != null) {
                return false;
            }
        } else if (!betUnit.getOrderId().equals(other.getBetUnit().getOrderId()) || !betUnit.getPlatformId().equals
                (other.getBetUnit().getPlatformId())) {
            return false;
        }
        return true;
    }

    public BetUnit getBetUnit() {
        return betUnit;
    }
}
