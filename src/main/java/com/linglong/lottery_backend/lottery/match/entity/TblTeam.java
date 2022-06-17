package com.linglong.lottery_backend.lottery.match.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity(name = "tbl_team")
@DynamicUpdate
@DynamicInsert
public class TblTeam implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_type")
    private Integer teamType;

    @Column(name = "team_name")
    private String teamName;

    @Column(name = "team_short_name")
    private String teamShortName;

    @Column(name = "team_icon")
    private String teamIcon;

    @Column(name = "third_team_id")
    private String thirdTeamId;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;

    @Column(name = "delete_flag")
    private Integer deleteFlag;

    @Version
    private Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTeamType() {
        return teamType;
    }

    public TblTeam setTeamType(Integer teamType) {
        this.teamType = teamType;
        return this;
    }

    public String getTeamName() {
        return teamName;
    }

    public TblTeam setTeamName(String teamName) {
        this.teamName = teamName;
        return this;
    }

    public String getTeamShortName() {
        return teamShortName;
    }

    public TblTeam setTeamShortName(String teamShortName) {
        this.teamShortName = teamShortName;
        return this;
    }

    public String getTeamIcon() {
        return teamIcon;
    }

    public TblTeam setTeamIcon(String teamIcon) {
        this.teamIcon = teamIcon;
        return this;
    }

    public String getThirdTeamId() {
        return thirdTeamId;
    }

    public TblTeam setThirdTeamId(String thirdTeamId) {
        this.thirdTeamId = thirdTeamId;
        return this;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public TblTeam setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public TblTeam setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public TblTeam setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public TblTeam setVersion(Integer version) {
        this.version = version;
        return this;
    }
}
