package com.linglong.lottery_backend.lottery.match.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity(name = "tbl_match_live")
@DynamicUpdate
@DynamicInsert
public class TblMatchLive implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "match_id")
    private Long matchId;

    @Column(name = "event_time")
    private Date eventTime;

    @Column(name = "event_team")
    private String eventTeam;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "event_player")
    private String eventPlayer;

    @Column(name = "match_prediction")
    private String matchPrediction;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;

    @Column(name = "delete_flag")
    private Boolean deleteFlag;

    @Version
    private Integer version;

    public Long getId() {
        return id;
    }

    public TblMatchLive setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getMatchId() {
        return matchId;
    }

    public TblMatchLive setMatchId(Long matchId) {
        this.matchId = matchId;
        return this;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public TblMatchLive setEventTime(Date eventTime) {
        this.eventTime = eventTime;
        return this;
    }

    public String getEventTeam() {
        return eventTeam;
    }

    public TblMatchLive setEventTeam(String eventTeam) {
        this.eventTeam = eventTeam;
        return this;
    }

    public String getEventType() {
        return eventType;
    }

    public TblMatchLive setEventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    public String getEventPlayer() {
        return eventPlayer;
    }

    public TblMatchLive setEventPlayer(String eventPlayer) {
        this.eventPlayer = eventPlayer;
        return this;
    }

    public String getMatchPrediction() {
        return matchPrediction;
    }

    public TblMatchLive setMatchPrediction(String matchPrediction) {
        this.matchPrediction = matchPrediction;
        return this;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public TblMatchLive setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public TblMatchLive setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public TblMatchLive setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public TblMatchLive setVersion(Integer version) {
        this.version = version;
        return this;
    }
}
