package com.linglong.lottery_backend.lottery.match.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity(name = "tbl_season")
@DynamicUpdate
@DynamicInsert
public class TblSeason implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "current_season")
    private Boolean currentSeason;

    @Column(name = "season_category")
    private Integer seasonCategory;

    @Column(name = "season_type")
    private Integer seasonType;

    @Column(name = "season_time")
    private String seasonTime;

    @Column(name = "season_name")
    private String seasonName;

    @Column(name = "season_full_name")
    private String seasonFullName;

    @Column(name = "third_party_season_id")
    private String thirdPartySeasonId;

    @Column(name = "third_party_compete_id")
    private String thirdPartyCompeteId;

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

    public TblSeason setId(Long id) {
        this.id = id;
        return this;
    }

    public Boolean getCurrentSeason() {
        return currentSeason;
    }

    public TblSeason setCurrentSeason(Boolean currentSeason) {
        this.currentSeason = currentSeason;
        return this;
    }

    public Integer getSeasonCategory() {
        return seasonCategory;
    }

    public TblSeason setSeasonCategory(Integer seasonCategory) {
        this.seasonCategory = seasonCategory;
        return this;
    }

    public Integer getSeasonType() {
        return seasonType;
    }

    public TblSeason setSeasonType(Integer seasonType) {
        this.seasonType = seasonType;
        return this;
    }

    public String getSeasonTime() {
        return seasonTime;
    }

    public TblSeason setSeasonTime(String seasonTime) {
        this.seasonTime = seasonTime;
        return this;
    }

    public String getSeasonName() {
        return seasonName;
    }

    public TblSeason setSeasonName(String seasonName) {
        this.seasonName = seasonName;
        return this;
    }

    public String getSeasonFullName() {
        return seasonFullName;
    }

    public TblSeason setSeasonFullName(String seasonFullName) {
        this.seasonFullName = seasonFullName;
        return this;
    }

    public String getThirdPartySeasonId() {
        return thirdPartySeasonId;
    }

    public TblSeason setThirdPartySeasonId(String thirdPartySeasonId) {
        this.thirdPartySeasonId = thirdPartySeasonId;
        return this;
    }

    public String getThirdPartyCompeteId() {
        return thirdPartyCompeteId;
    }

    public TblSeason setThirdPartyCompeteId(String thirdPartyCompeteId) {
        this.thirdPartyCompeteId = thirdPartyCompeteId;
        return this;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public TblSeason setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public TblSeason setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public TblSeason setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public TblSeason setVersion(Integer version) {
        this.version = version;
        return this;
    }
}
