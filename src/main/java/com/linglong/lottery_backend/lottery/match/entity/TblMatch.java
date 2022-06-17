package com.linglong.lottery_backend.lottery.match.entity;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang.time.DateFormatUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


@Entity(name = "tbl_match")
@DynamicUpdate
@DynamicInsert
public class TblMatch implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "host_id")
    private Long hostId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "host_id", referencedColumnName = "id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JSONField(serialize = false)
    TblSeasonTeam hostSeasonTeam;

    @Column(name = "away_id")
    private Long awayId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "away_id", referencedColumnName = "id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JSONField(serialize = false)
    TblSeasonTeam awaySeasonTeam;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(
//            name = "hostSeasonTeam.seasonId",referencedColumnName="")
//    @NotFound(action= NotFoundAction.IGNORE)
//    TblSeason season;

    @Column(name = "third_party_match_id")
    private String thirdPartyMatchId;

    @Column(name = "match_status")
    private Integer matchStatus;

    @Column(name = "period")
    private String period;

    @Column(name = "match_desc")
    private String matchDesc;

    @Column(name = "match_sn")
    private String matchSn;

    @Column(name = "match_week")
    private String matchWeek;

    @Column(name = "host_score")
    private String hostScore;

    @Column(name = "host_half_score")
    private String hostHalfScore;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "match_id")
    @NotFound(action = NotFoundAction.IGNORE)
    @OrderBy("id DESC")
    @JSONField(serialize = false)
    private List<TblMatchOdds> matchOdds;

    @Column(name = "away_score")
    private String awayScore;

    @Column(name = "away_half_score")
    private String awayHalfScore;

    @Column(name = "match_time")
    private Date matchTime;

    @Column(name = "extra_list")
    private String extraList;

    @Column(name = "live_statis")
    private String liveStatis;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;

    @Column(name = "delete_flag")
    private Boolean deleteFlag;

    @Version
    @Column(name = "version")
    private Integer version;

    @Column(name = "match_type")
    private Integer matchType;

    @Column(name = "match_results")
    private String matchResults;

    @Column(name = "sale_status")
    private Integer saleStatus;

    @Transient
    private String matchTimeFormat;

    @Transient
    private String matchIssueFormat;

    @Column(name = "hot_status")
    private Integer hotStatus;

    @Transient
    private Date endTime;
    @Transient
    private Integer level;
    //标签
    @Transient
    private String lable;
    //标签状态
    @Transient
    private Integer lable_status;

    public Long getId() {
        return id;
    }

    public TblMatch setId(Long id) {
        this.id = id;
        return this;
    }

    public TblSeasonTeam getHostSeasonTeam() {
        return hostSeasonTeam;
    }

    public TblMatch setHostSeasonTeam(TblSeasonTeam hostSeasonTeam) {
        this.hostSeasonTeam = hostSeasonTeam;
        return this;
    }

    public TblSeasonTeam getAwaySeasonTeam() {
        return awaySeasonTeam;
    }

    public TblMatch setAwaySeasonTeam(TblSeasonTeam awaySeasonTeam) {
        this.awaySeasonTeam = awaySeasonTeam;
        return this;
    }

    public String getThirdPartyMatchId() {
        return thirdPartyMatchId;
    }

    public TblMatch setThirdPartyMatchId(String thirdPartyMatchId) {
        this.thirdPartyMatchId = thirdPartyMatchId;
        return this;
    }

    public Integer getMatchStatus() {
        return matchStatus;
    }

    public TblMatch setMatchStatus(Integer matchStatus) {
        this.matchStatus = matchStatus;
        return this;
    }

    public String getMatchDesc() {
        return matchDesc;
    }

    public TblMatch setMatchDesc(String matchDesc) {
        this.matchDesc = matchDesc;
        return this;
    }

    public String getMatchSn() {
        return matchSn;
    }

    public TblMatch setMatchSn(String matchSn) {
        this.matchSn = matchSn;
        return this;
    }

    public String getMatchWeek() {
        return matchWeek;
    }

    public TblMatch setMatchWeek(String matchWeek) {
        this.matchWeek = matchWeek;
        return this;
    }

    public String getHostScore() {
        return hostScore;
    }

    public TblMatch setHostScore(String hostScore) {
        this.hostScore = hostScore;
        return this;
    }

    public String getHostHalfScore() {
        return hostHalfScore;
    }

    public TblMatch setHostHalfScore(String hostHalfScore) {
        this.hostHalfScore = hostHalfScore;
        return this;
    }

    public List<TblMatchOdds> getMatchOdds() {
        return matchOdds;
    }

    public TblMatch setMatchOdds(List<TblMatchOdds> matchOdds) {
        this.matchOdds = matchOdds;
        return this;
    }

    public String getAwayScore() {
        return awayScore;
    }

    public TblMatch setAwayScore(String awayScore) {
        this.awayScore = awayScore;
        return this;
    }

    public String getAwayHalfScore() {
        return awayHalfScore;
    }

    public TblMatch setAwayHalfScore(String awayHalfScore) {
        this.awayHalfScore = awayHalfScore;
        return this;
    }

    public Date getMatchTime() {
        return matchTime;
    }

    public TblMatch setMatchTime(Date matchTime) {
        this.matchTime = matchTime;
        return this;
    }

    public String getExtraList() {
        return extraList;
    }

    public TblMatch setExtraList(String extraList) {
        this.extraList = extraList;
        return this;
    }

    public String getLiveStatis() {
        return liveStatis;
    }

    public TblMatch setLiveStatis(String liveStatis) {
        this.liveStatis = liveStatis;
        return this;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public TblMatch setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public TblMatch setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public TblMatch setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public TblMatch setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public Integer getMatchType() {
        return matchType;
    }

    public TblMatch setMatchType(Integer matchType) {
        this.matchType = matchType;
        return this;
    }

    public TblMatch setMatchTimeFormat(String matchTimeFormat) {
        this.matchTimeFormat = matchTimeFormat;
        return this;
    }


    public Long getHostId() {
        return hostId;
    }

    public TblMatch setHostId(Long hostId) {
        this.hostId = hostId;
        return this;
    }

    public Long getAwayId() {
        return awayId;
    }

    public TblMatch setAwayId(Long awayId) {
        this.awayId = awayId;
        return this;
    }

    public String getMatchTimeFormat() {
        return DateFormatUtils.format(this.matchTime, "MM月dd", TimeZone.getTimeZone("America/New_York"));
    }

    public String getMatchIssueFormat() {
        return DateFormatUtils.format(this.matchTime, "yyyyMMdd", TimeZone.getTimeZone("America/New_York"));
    }

    public String getCreatedTimeFormat() {
        return DateFormatUtils.format(this.createdTime, "yyyy-MM-dd");
    }

    public String getMatchResults() {
        return matchResults;
    }

    public TblMatch setMatchResults(String matchResults) {
        this.matchResults = matchResults;
        return this;
    }

    public Integer getSaleStatus() {
        return saleStatus;
    }

    public TblMatch setSaleStatus(Integer saleStatus) {
        this.saleStatus = saleStatus;
        return this;
    }

    public Integer getHotStatus() {
        return hotStatus;
    }

    public TblMatch setHotStatus(Integer hotStatus) {
        this.hotStatus = hotStatus;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getLevel() {
        return level;
    }

    public TblMatch setLevel(Integer level) {
        this.level = level;
        return this;
    }

    public String getLable() {
        return lable;
    }

    public TblMatch setLable(String lable) {
        this.lable = lable;
        return this;
    }

    public Integer getLable_status() {
        return lable_status;
    }

    public TblMatch setLable_status(Integer lable_status) {
        this.lable_status = lable_status;
        return this;
    }

    public String getPeriod() {
        return period;
    }

    public TblMatch setPeriod(String period) {
        this.period = period;
        return this;
    }
}
