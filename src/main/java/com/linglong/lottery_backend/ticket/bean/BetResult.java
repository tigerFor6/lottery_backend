package com.linglong.lottery_backend.ticket.bean;

import lombok.Data;

import java.util.Map;

/**
 * Created by ynght on 2019-04-26
 */
@Data
public class BetResult {
    private Long ticketId;
    private String ticketNo;
    private Integer ticketStatus;

    private Map<String, Map<String, Map<String, String>>> spMap;

    public BetResult(Long ticketId, String ticketNo, Integer ticketStatus) {
        this.ticketId = ticketId;
        this.ticketNo = ticketNo;
        this.ticketStatus = ticketStatus;
    }

    public BetResult(Long ticketId, String ticketNo, Integer ticketStatus,
                     Map<String, Map<String, Map<String, String>>> spMap) {
        this.ticketId = ticketId;
        this.ticketNo = ticketNo;
        this.ticketStatus = ticketStatus;
        this.spMap = spMap;
    }
}
