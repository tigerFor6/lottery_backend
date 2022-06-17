package com.linglong.lottery_backend.lottery.match.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity(name = "tbl_match_odds")
@DynamicUpdate
@DynamicInsert
public class TblMatchOdds implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "match_id")
    private Long matchId;

    @Column(name = "third_party_id")
    private String thirdPartyId;

    @Column(name = "play_code")
    private String playCode;

    @Column(name = "play_name")
    private String playName;

    @Column(name = "play_num")
    private String playNum;

    @Column(name = "play_details")
    private String playDetails;

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

    public TblMatchOdds setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getMatchId() {
        return matchId;
    }

    public TblMatchOdds setMatchId(Long matchId) {
        this.matchId = matchId;
        return this;
    }

    public String getThirdPartyId() {
        return thirdPartyId;
    }

    public TblMatchOdds setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
        return this;
    }

    public String getPlayCode() {
        return playCode;
    }

    public TblMatchOdds setPlayCode(String playCode) {
        this.playCode = playCode;
        return this;
    }

    public String getPlayName() {
        return playName;
    }

    public TblMatchOdds setPlayName(String playName) {
        this.playName = playName;
        return this;
    }

    public String getPlayDetails() {
        return playDetails;
    }

    public TblMatchOdds setPlayDetails(String playDetails) {
        this.playDetails = playDetails;
        return this;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public TblMatchOdds setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public TblMatchOdds setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public TblMatchOdds setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public TblMatchOdds setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public String getPlayNum() {
        return playNum;
    }

    public TblMatchOdds setPlayNum(String playNum) {
        this.playNum = playNum;
        return this;
    }
}
