package com.linglong.lottery_backend.prize.entity.custom;

import java.io.Serializable;

public class BettingOdds implements Serializable {

    private String item;

    private String odds;

    private Long matchId;

    private String playCode;

    private String matchSn;

    public String getItem() {
        return item;
    }

    public BettingOdds setItem(String item) {
        this.item = item;
        return this;
    }

    public String getOdds() {
        return odds;
    }

    public BettingOdds setOdds(String odds) {
        this.odds = odds;
        return this;
    }

    public Long getMatchId() {
        return matchId;
    }

    public BettingOdds setMatchId(Long matchId) {
        this.matchId = matchId;
        return this;
    }

    public String getPlayCode() {
        return playCode;
    }

    public BettingOdds setPlayCode(String playCode) {
        this.playCode = playCode;
        return this;
    }

    public String getMatchSn() {
        return matchSn;
    }

    public BettingOdds setMatchSn(String matchSn) {
        this.matchSn = matchSn;
        return this;
    }
}
