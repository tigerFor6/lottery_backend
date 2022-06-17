package com.linglong.lottery_backend.ticket.platform.bjgc;

import com.linglong.lottery_backend.ticket.repository.TblBettingTicketRepository;
import com.linglong.lottery_backend.ticket.bean.BetResult;
import com.linglong.lottery_backend.ticket.bean.BetResultWrap;
import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.enums.PlatformEnum;
import com.linglong.lottery_backend.ticket.platform.PlatformHandlerFactory;
import com.linglong.lottery_backend.ticket.platform.PlatformInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ynght on 2019-04-26
 */
@Component
@Slf4j
public class BjGongCaiPlatform implements PlatformInterface {
    @Autowired
    private TblBettingTicketRepository tblBettingTicketRepository;

    @Override
    public void send2Center(Integer gameType, List<TicketDto> tickets) {
        if (tickets == null || tickets.size() <= 0) {
            return;
        }
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("sid", PlatformEnum.BJGONGCAI.getPlatform().getPlatformAccount());
        objectMap.put("ticketList", tickets);

        try {
            PlatformHandlerFactory.getInstance().getServiceHandler("bjGongCaiDoBetHandler")
                    .handle(gameType, objectMap);
        } catch (Exception e) {
            List<Long> ids = tickets.stream().map(ticketDto -> Long.valueOf(ticketDto.getTicketId())).collect(Collectors.toList());
            tblBettingTicketRepository.updateTicketStatusByTicketId(0, ids);

            log.error("[DoBet] : failed to obtain required message, gameType=" + gameType + ", ticket ids=" + ids.toString(), e);
        }
    }

    @Override
    public BetResultWrap queryTicketStatus(Integer gameType, Long orderId, List<Long> ticketIds) {

        if (ticketIds == null || ticketIds.isEmpty()) {
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        try {
            params.put("orderId", orderId);
            params.put("ticketIds", ticketIds);
            params.put("ticketDao", tblBettingTicketRepository);
            return (BetResultWrap) PlatformHandlerFactory.getInstance().getServiceHandler
                    ("bjGongCaiTicketStatusHandler").handle(gameType, params);
        } catch (Exception ex) {
            log.warn("[QueryTicketStatus] : failed to obtain required message, gameType=" + gameType + ", " + ex, ex);
            return null;
        }
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
