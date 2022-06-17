package com.linglong.lottery_backend.prize.entity.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BettingMatch implements Comparable<BettingMatch>, Serializable {

    @JsonProperty("match_id")
    private Long matchId;

    @JsonProperty("match_sn")
    private String matchSn;

    @JsonProperty("match_name")
    private String matchName;

    private HashMap<String, List<BettingOdds>> odds;

    @JsonProperty("plays")
    private HashMap<String, String> plays;

    @Override
    public int compareTo(BettingMatch o) {
        return compare(matchId.intValue(),o.matchId.intValue());
    }

    public static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public Long getMatchId() {
        return matchId;
    }

    public BettingMatch setMatchId(Long matchId) {
        this.matchId = matchId;
        return this;
    }

    public HashMap<String, List<BettingOdds>> getOdds() {
        return odds;
    }

    public BettingMatch setOdds(HashMap<String, List<BettingOdds>> odds) {
        this.odds = odds;
        return this;
    }

    public List<BettingOdds> getAllOdds(Long matchId, String matchSn){
        List<BettingOdds> result = new ArrayList<>();
        if (null != odds){
            odds.forEach((k,v)-> {
                v.forEach(d->{
                    d.setMatchId(matchId);
                    d.setPlayCode(k);
                    d.setMatchSn(matchSn);
                });
                result.addAll(v);
            });
        }
        return result;
    }

    public String getMatchSn() {
        return matchSn;
    }

    public BettingMatch setMatchSn(String matchSn) {
        this.matchSn = matchSn;
        return this;
    }

    public String getMatchName() {
        return matchName;
    }

    public BettingMatch setMatchName(String matchName) {
        this.matchName = matchName;
        return this;
    }

    public HashMap<String, String> getPlays() {
        return plays;
    }

    public BettingMatch setPlays(HashMap<String, String> plays) {
        this.plays = plays;
        return this;
    }
}
