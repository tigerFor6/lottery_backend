package com.linglong.lottery_backend.lottery.match.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import java.io.Serializable;

@Entity(name = "tbl_season_round")
@DynamicUpdate
@DynamicInsert
public class TblSeasonRound implements Serializable {

	@Id
	private Long id;

	@Column(name = "season_id")
	private Long seasonId;

	@Column(name = "third_party_round_id")
	private String thirdPartyRoundId;

	@Column(name = "round_name")
	private String roundName;

	@Version
	private Integer version;

	public Long getId() {
		return id;
	}

	public TblSeasonRound setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getSeasonId() {
		return seasonId;
	}

	public TblSeasonRound setSeasonId(Long seasonId) {
		this.seasonId = seasonId;
		return this;
	}

	public String getThirdPartyRoundId() {
		return thirdPartyRoundId;
	}

	public TblSeasonRound setThirdPartyRoundId(String thirdPartyRoundId) {
		this.thirdPartyRoundId = thirdPartyRoundId;
		return this;
	}

	public String getRoundName() {
		return roundName;
	}

	public TblSeasonRound setRoundName(String roundName) {
		this.roundName = roundName;
		return this;
	}

	public Integer getVersion() {
		return version;
	}

	public TblSeasonRound setVersion(Integer version) {
		this.version = version;
		return this;
	}
}
