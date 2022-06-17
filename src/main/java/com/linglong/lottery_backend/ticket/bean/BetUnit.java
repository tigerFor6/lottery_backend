package com.linglong.lottery_backend.ticket.bean;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ynght on 2019-04-26
 */
@Data
public class BetUnit {
    private Integer gameType;
    private Integer platformId;
    private Long orderId;
    private Long enterTime = System.currentTimeMillis();
    private List<TicketDto> tickets = new LinkedList<>();

    public BetUnit(Integer gameType, Integer platformId, Long orderId, List<TicketDto> tickets) {
        this.gameType = gameType;
        this.platformId = platformId;
        this.orderId = orderId;
        this.tickets = tickets;
    }
}
