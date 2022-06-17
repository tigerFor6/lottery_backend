package com.linglong.lottery_backend.ticket.service;

import com.linglong.lottery_backend.ticket.bean.TicketDto;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by ynght on 2019-04-20
 */
public interface SplitService {

    int splitOrder(Long orderId, String userId, Timestamp deadline);

    void sqlitMuDandcolorBall(String lotteryNumber, String matchSn, TicketDto ticketDto);
}
