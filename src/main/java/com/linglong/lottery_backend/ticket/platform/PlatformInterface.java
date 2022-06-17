package com.linglong.lottery_backend.ticket.platform;

import com.linglong.lottery_backend.ticket.bean.BetResult;
import com.linglong.lottery_backend.ticket.bean.BetResultWrap;
import com.linglong.lottery_backend.ticket.bean.TicketDto;

import java.util.List;

/**
 * Created by ynght on 2019-04-26
 */
public interface PlatformInterface {
    void send2Center(Integer gameType, List<TicketDto> tickets);

    BetResultWrap queryTicketStatus(Integer gameType, Long orderId, List<Long> ticketIds);

    int getTicketNumPerBatch(Integer type);

    List<BetResult> notifyTickets(String xml);

    String respNotifyTickets(String xml, String code);

}
