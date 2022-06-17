package com.linglong.lottery_backend.ticket.platform.al;

import com.linglong.lottery_backend.ticket.repository.TblBettingTicketRepository;
import com.linglong.lottery_backend.ticket.bean.BetResult;
import com.linglong.lottery_backend.ticket.bean.BetResultWrap;
import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.enums.PlatformEnum;
import com.linglong.lottery_backend.ticket.platform.AbstractPlatformHandler;
import com.linglong.lottery_backend.ticket.platform.PlatformHandlerFactory;
import com.linglong.lottery_backend.ticket.platform.PlatformInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * hua zeng guang
 */
@Component
@Slf4j
public class AoliPlatform implements PlatformInterface {
    @Autowired
    private TblBettingTicketRepository tblBettingTicketRepository;

    @Override
    public void send2Center(Integer gameType, List<TicketDto> tickets) {
        if (tickets == null || tickets.size() <= 0) {
            return;
        }
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("sid", PlatformEnum.AOLI.getPlatform().getPlatformAccount());
        objectMap.put("ticketList", tickets);
        try {
            PlatformHandlerFactory.getInstance().getServiceHandler("aoliDoBetHandler")
                    .handle(gameType, objectMap);
        } catch (Exception e) {
            String ticketIdStr = AbstractPlatformHandler.generateTicketIdString(tickets);
            log.error("[DoBet] : failed to obtain required message, gameType=" + gameType + ", ticket ids=" + ticketIdStr, e);
        }
    }

    @Override
    public BetResultWrap queryTicketStatus(Integer gameType, Long orderId, List<Long> ticketIds) {
    return null;
//        if (ticketIds == null || ticketIds.isEmpty()) {
//            return null;
//        }
//        Map<String, Object> params = new HashMap<>();
//        try {
//            params.put("orderId", orderId);
//            params.put("ticketIds", ticketIds);
//            params.put("ticketDao", tblBettingTicketRepository);
//            return (BetResultWrap) PlatformHandlerFactory.getInstance().getServiceHandler
//                    ("bjGongCaiTicketStatusHandler").handle(gameType, params);
//        } catch (Exception ex) {
//            log.warn("[QueryTicketStatus] : failed to obtain required message, gameType=" + gameType + ", " + ex, ex);
//            return null;
//        }
    }

    @Override
    public int getTicketNumPerBatch(Integer type) {
        return 0;
    }

    @Override
    public List<BetResult> notifyTickets(String xml) {
        return null;
    }

    @Override
    public String respNotifyTickets(String xml, String code) {
        return null;
    }
}
