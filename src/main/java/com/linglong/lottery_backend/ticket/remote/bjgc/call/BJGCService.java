package com.linglong.lottery_backend.ticket.remote.bjgc.call;

import com.linglong.lottery_backend.ticket.remote.bjgc.entity.Message;
import com.linglong.lottery_backend.ticket.remote.bjgc.entity.part.PrizeResponse;
import com.linglong.lottery_backend.ticket.remote.bjgc.entity.part.Response;

/**
 *
 */
public interface BJGCService {

    Response callTicketStatus(Message message, Integer gameType);

    PrizeResponse callPrizeTicketStatus(Message message);
}
