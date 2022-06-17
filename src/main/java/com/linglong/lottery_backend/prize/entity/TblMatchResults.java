package com.linglong.lottery_backend.prize.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;


@Entity(name = "tbl_match_results")
@DynamicUpdate
@DynamicInsert
public class TblMatchResults implements Serializable {

	@Id
	private Long id;

	@Column(name = "source_type")
	private Integer sourceType;

	@Column(name = "match_id")
	private Long matchId;

	@Column(name = "match_sn")
	private String matchSn;

	@Column(name = "match_time")
	private java.util.Date matchTime;

	@Column(name = "match_result")
	private String matchResult;

	@Column(name = "created_time")
	private java.util.Date createdTime;

	@Column(name = "updated_time")
	private java.util.Date updatedTime;

	public Long getId() {
		return id;
	}

	public TblMatchResults setId(Long id) {
		this.id = id;
		return this;
	}

	public Integer getSourceType() {
		return sourceType;
	}

	public TblMatchResults setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
		return this;
	}

	public Long getMatchId() {
		return matchId;
	}

	public TblMatchResults setMatchId(Long matchId) {
		this.matchId = matchId;
		return this;
	}

	public String getMatchSn() {
		return matchSn;
	}

	public TblMatchResults setMatchSn(String matchSn) {
		this.matchSn = matchSn;
		return this;
	}

	public Date getMatchTime() {
		return matchTime;
	}

	public TblMatchResults setMatchTime(Date matchTime) {
		this.matchTime = matchTime;
		return this;
	}

	public String getMatchResult() {
		return matchResult;
	}

	public TblMatchResults setMatchResult(String matchResult) {
		this.matchResult = matchResult;
		return this;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public TblMatchResults setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
		return this;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public TblMatchResults setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
		return this;
	}
}
