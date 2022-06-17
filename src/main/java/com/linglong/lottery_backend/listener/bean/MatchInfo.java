package com.linglong.lottery_backend.listener.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class MatchInfo implements Serializable {

    @JsonProperty("match_id")
    private Long matchId;

    @JsonProperty("team_id")
    private Long teamId;

    @JsonProperty("season_id")
    private Long seasonId;

    @JsonProperty("season_round_id")
    private Long seasonRoundId;

    @JsonProperty("season_team_id")
    private Long seasonTeamId;

    @JsonProperty("match_odds_id")
    private Long[] matchOddsId;

    @JsonProperty("match_result")
    private boolean matchResult = false;

    @JsonProperty("cancel_result")
    private boolean cancelResult= false;

    public Long getMatchId() {
        return matchId;
    }

    public MatchInfo setMatchId(Long matchId) {
        this.matchId = matchId;
        return this;
    }

    public Long getTeamId() {
        return teamId;
    }

    public MatchInfo setTeamId(Long teamId) {
        this.teamId = teamId;
        return this;
    }

    public Long getSeasonId() {
        return seasonId;
    }

    public MatchInfo setSeasonId(Long seasonId) {
        this.seasonId = seasonId;
        return this;
    }

    public Long getSeasonRoundId() {
        return seasonRoundId;
    }

    public MatchInfo setSeasonRoundId(Long seasonRoundId) {
        this.seasonRoundId = seasonRoundId;
        return this;
    }

    public Long getSeasonTeamId() {
        return seasonTeamId;
    }

    public MatchInfo setSeasonTeamId(Long seasonTeamId) {
        this.seasonTeamId = seasonTeamId;
        return this;
    }

    public Long[] getMatchOddsId() {
        return matchOddsId;
    }

    public MatchInfo setMatchOddsId(Long[] matchOddsId) {
        this.matchOddsId = matchOddsId;
        return this;
    }

    public boolean isMatchResult() {
        return matchResult;
    }

    public MatchInfo setMatchResult(boolean matchResult) {
        this.matchResult = matchResult;
        return this;
    }

    public boolean isCancelResult() {
        return cancelResult;
    }

    public void setCancelResult(boolean cancelResult) {
        this.cancelResult = cancelResult;
    }
}
