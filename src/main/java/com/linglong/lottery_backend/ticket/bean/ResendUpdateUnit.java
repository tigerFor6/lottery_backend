package com.linglong.lottery_backend.ticket.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ynght on 2019-04-28
 */
@Data
@NoArgsConstructor
public class ResendUpdateUnit {
    private Integer gameType;
    private Integer platformId;
    private Long orderId;
    private Long enterTime = System.currentTimeMillis();
    private List<BetResult> results = new LinkedList<>();

    public ResendUpdateUnit(Integer gameType, Integer platformId, Long orderId, List<BetResult> results) {
        this.gameType = gameType;
        this.platformId = platformId;
        this.orderId = orderId;
        this.results = results;
    }
}
