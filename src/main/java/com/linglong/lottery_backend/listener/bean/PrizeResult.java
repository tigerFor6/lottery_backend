package com.linglong.lottery_backend.listener.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class PrizeResult implements Serializable {

    @JsonProperty("match_id")
    private Long matchId;

    private List<PrizeResultDetails> result;

    public Long getMatchId() {
        return matchId;
    }

    public PrizeResult setMatchId(Long matchId) {
        this.matchId = matchId;
        return this;
    }

    public List<PrizeResultDetails> getResult() {
        return result;
    }

    public PrizeResult setResult(List<PrizeResultDetails> result) {
        this.result = result;
        return this;
    }
}
