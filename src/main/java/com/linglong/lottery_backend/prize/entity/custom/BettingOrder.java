package com.linglong.lottery_backend.prize.entity.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class BettingOrder implements Serializable {

    @JsonProperty("bonus")
    private String bonus;

    @JsonProperty("chuan_guan")
    private String chuanGuan;

    @JsonProperty("multiple")
    private Integer multiple;

    @JsonProperty("bet_number")
    private Integer betNumber;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("user_id")
    private Long userId;

    private List<BettingMatch> matchs;

    public String getBonus() {
        return bonus;
    }

    public BettingOrder setBonus(String bonus) {
        this.bonus = bonus;
        return this;
    }

    public String getChuanGuan() {
        return chuanGuan;
    }

    public BettingOrder setChuanGuan(String chuanGuan) {
        this.chuanGuan = chuanGuan;
        return this;
    }

    public Integer getMultiple() {
        return multiple;
    }

    public BettingOrder setMultiple(Integer multiple) {
        this.multiple = multiple;
        return this;
    }

    public Integer getBetNumber() {
        return betNumber;
    }

    public BettingOrder setBetNumber(Integer betNumber) {
        this.betNumber = betNumber;
        return this;
    }

    public List<BettingMatch> getMatchs() {
        return matchs;
    }

    public BettingOrder setMatchs(List<BettingMatch> matchs) {
        this.matchs = matchs;
        return this;
    }

    public Long getOrderId() {
        return orderId;
    }

    public BettingOrder setOrderId(Long orderId) {
        this.orderId = orderId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public BettingOrder setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

}
