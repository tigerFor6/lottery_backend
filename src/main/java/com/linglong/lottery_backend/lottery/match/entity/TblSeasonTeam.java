package com.linglong.lottery_backend.lottery.match.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "tbl_season_team")
@DynamicUpdate
@DynamicInsert
public class TblSeasonTeam implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "season_id")
    private Long seasonId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "season_id", referencedColumnName = "id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    TblSeason season;

    @Column(name = "team_rank")
    private Integer teamRank;

    @Column(name = "team_point")
    private Integer teamPoint;

    private Integer win;

    private Integer drew;

    private Integer lose;

    private Double goal;

    @Column(name = "loss_goal")
    private Double lossGoal;

    @Column(name = "third_party_round_id")
    private String thirdPartyRoundId;

    @Column(name = "third_party_round_name")
    private String thirdPartyRoundName;

    @Column(name = "third_party_round_start_date")
    private String thirdPartyRoundStartDate;

    @Column(name = "third_party_round_end_date")
    private String thirdPartyRoundEndDate;

    @Column(name = "third_party_round_type")
    private String thirdPartyRoundType;

    @Column(name = "third_party_round_groups")
    private String thirdPartyRoundGroups;

    @Column(name = "third_party_round_has_outgroup_matches")
    private String thirdPartyRoundHasOutgroupMatches;

    @Column(name = "third_party_round_status")
    private Integer thirdPartyRoundStatus;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;

    @Column(name = "delete_flag")
    private Boolean deleteFlag;

    @Version
    private Integer version;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    @NotFound(action = NotFoundAction.IGNORE)
    private TblTeam team;

    public Long getId() {
        return id;
    }

    public TblSeasonTeam setId(Long id) {
        this.id = id;
        return this;
    }

    public TblSeason getSeason() {
        return season;
    }

    public TblSeasonTeam setSeason(TblSeason season) {
        this.season = season;
        return this;
    }

    public Integer getTeamRank() {
        return teamRank;
    }

    public TblSeasonTeam setTeamRank(Integer teamRank) {
        this.teamRank = teamRank;
        return this;
    }

    public Integer getTeamPoint() {
        return teamPoint;
    }

    public TblSeasonTeam setTeamPoint(Integer teamPoint) {
        this.teamPoint = teamPoint;
        return this;
    }

    public Integer getWin() {
        return win;
    }

    public TblSeasonTeam setWin(Integer win) {
        this.win = win;
        return this;
    }

    public Integer getDrew() {
        return drew;
    }

    public TblSeasonTeam setDrew(Integer drew) {
        this.drew = drew;
        return this;
    }

    public Integer getLose() {
        return lose;
    }

    public TblSeasonTeam setLose(Integer lose) {
        this.lose = lose;
        return this;
    }

    public Double getGoal() {
        return goal;
    }

    public TblSeasonTeam setGoal(Double goal) {
        this.goal = goal;
        return this;
    }

    public Double getLossGoal() {
        return lossGoal;
    }

    public TblSeasonTeam setLossGoal(Double lossGoal) {
        this.lossGoal = lossGoal;
        return this;
    }

    public String getThirdPartyRoundId() {
        return thirdPartyRoundId;
    }

    public TblSeasonTeam setThirdPartyRoundId(String thirdPartyRoundId) {
        this.thirdPartyRoundId = thirdPartyRoundId;
        return this;
    }

    public String getThirdPartyRoundName() {
        return thirdPartyRoundName;
    }

    public TblSeasonTeam setThirdPartyRoundName(String thirdPartyRoundName) {
        this.thirdPartyRoundName = thirdPartyRoundName;
        return this;
    }

    public String getThirdPartyRoundStartDate() {
        return thirdPartyRoundStartDate;
    }

    public TblSeasonTeam setThirdPartyRoundStartDate(String thirdPartyRoundStartDate) {
        this.thirdPartyRoundStartDate = thirdPartyRoundStartDate;
        return this;
    }

    public String getThirdPartyRoundEndDate() {
        return thirdPartyRoundEndDate;
    }

    public TblSeasonTeam setThirdPartyRoundEndDate(String thirdPartyRoundEndDate) {
        this.thirdPartyRoundEndDate = thirdPartyRoundEndDate;
        return this;
    }

    public String getThirdPartyRoundType() {
        return thirdPartyRoundType;
    }

    public TblSeasonTeam setThirdPartyRoundType(String thirdPartyRoundType) {
        this.thirdPartyRoundType = thirdPartyRoundType;
        return this;
    }

    public String getThirdPartyRoundGroups() {
        return thirdPartyRoundGroups;
    }

    public TblSeasonTeam setThirdPartyRoundGroups(String thirdPartyRoundGroups) {
        this.thirdPartyRoundGroups = thirdPartyRoundGroups;
        return this;
    }

    public String getThirdPartyRoundHasOutgroupMatches() {
        return thirdPartyRoundHasOutgroupMatches;
    }

    public TblSeasonTeam setThirdPartyRoundHasOutgroupMatches(String thirdPartyRoundHasOutgroupMatches) {
        this.thirdPartyRoundHasOutgroupMatches = thirdPartyRoundHasOutgroupMatches;
        return this;
    }

    public Integer getThirdPartyRoundStatus() {
        return thirdPartyRoundStatus;
    }

    public TblSeasonTeam setThirdPartyRoundStatus(Integer thirdPartyRoundStatus) {
        this.thirdPartyRoundStatus = thirdPartyRoundStatus;
        return this;
    }


    public Date getCreatedTime() {
        return createdTime;
    }

    public TblSeasonTeam setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public TblSeasonTeam setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public TblSeasonTeam setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public TblSeasonTeam setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public TblTeam getTeam() {
        return team;
    }

    public TblSeasonTeam setTeam(TblTeam team) {
        this.team = team;
        return this;
    }

    public Long getSeasonId() {
        return seasonId;
    }

    public TblSeasonTeam setSeasonId(Long seasonId) {
        this.seasonId = seasonId;
        return this;
    }
}
