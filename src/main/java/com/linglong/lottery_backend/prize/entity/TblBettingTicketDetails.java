package com.linglong.lottery_backend.prize.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "tbl_betting_ticket_details")
@DynamicUpdate
@DynamicInsert
public class TblBettingTicketDetails implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "ticket_details_id")
	private Long ticketDetailsId;

	@Column(name = "betting_ticket_id")
	private Long bettingTicketId;

	@Column(name = "order_id")
	private Long orderId;

	@Column(name = "match_id")
	private Long matchId;

	@Column(name = "match_issue")
	private String matchIssue;

	@Column(name = "match_sn")
	private String matchSn;

	@Column(name = "play_code")
	private String playCode;

	@Column(name = "odds_code")
	private String oddsCode;

	@Column(name = "odds")
	private BigDecimal odds;

	@Column(name = "result")
	private Integer result;

	@Column(name = "created_time")
	private java.util.Date createdTime;

	@Column(name = "updated_time")
	private java.util.Date updatedTime;

	@Version
	@Column(name = "version")
	private Integer version;

	@Transient
	private String chuanguan;

	public Long getId() {
		return id;
	}

	public TblBettingTicketDetails setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getTicketDetailsId() {
		return ticketDetailsId;
	}

	public TblBettingTicketDetails setTicketDetailsId(Long ticketDetailsId) {
		this.ticketDetailsId = ticketDetailsId;
		return this;
	}

	public Long getBettingTicketId() {
		return bettingTicketId;
	}

	public TblBettingTicketDetails setBettingTicketId(Long bettingTicketId) {
		this.bettingTicketId = bettingTicketId;
		return this;
	}

	public Long getOrderId() {
		return orderId;
	}

	public TblBettingTicketDetails setOrderId(Long orderId) {
		this.orderId = orderId;
		return this;
	}

	public Long getMatchId() {
		return matchId;
	}

	public TblBettingTicketDetails setMatchId(Long matchId) {
		this.matchId = matchId;
		return this;
	}

	public String getMatchIssue() {
		return matchIssue;
	}

	public TblBettingTicketDetails setMatchIssue(String matchIssue) {
		this.matchIssue = matchIssue;
		return this;
	}

	public String getMatchSn() {
		return matchSn;
	}

	public TblBettingTicketDetails setMatchSn(String matchSn) {
		this.matchSn = matchSn;
		return this;
	}

	public String getPlayCode() {
		return playCode;
	}

	public TblBettingTicketDetails setPlayCode(String playCode) {
		this.playCode = playCode;
		return this;
	}

	public String getOddsCode() {
		return oddsCode;
	}

	public TblBettingTicketDetails setOddsCode(String oddsCode) {
		this.oddsCode = oddsCode;
		return this;
	}

	public BigDecimal getOdds() {
		return odds;
	}

	public TblBettingTicketDetails setOdds(BigDecimal odds) {
		this.odds = odds;
		return this;
	}

	public Integer getResult() {
		return result;
	}

	public TblBettingTicketDetails setResult(Integer result) {
		this.result = result;
		return this;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public TblBettingTicketDetails setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
		return this;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public TblBettingTicketDetails setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
		return this;
	}

	public Integer getVersion() {
		return version;
	}

	public TblBettingTicketDetails setVersion(Integer version) {
		this.version = version;
		return this;
	}

	public String getChuanguan() {
		return chuanguan;
	}

	public TblBettingTicketDetails setChuanguan(String chuanguan) {
		this.chuanguan = chuanguan;
		return this;
	}

	public TblBettingTicketDetails() {
	}


	public TblBettingTicketDetails(Long ticketDetailsId, Long bettingTicketId, Long orderId, Long matchId, String matchIssue,
								   String matchSn, String playCode, String oddsCode, BigDecimal odds,String chuanguan) {
		this.ticketDetailsId = ticketDetailsId;
		this.bettingTicketId = bettingTicketId;
		this.orderId = orderId;
		this.matchId = matchId;
		this.matchIssue = matchIssue;
		this.matchSn = matchSn;
		this.playCode = playCode;
		this.oddsCode = oddsCode;
		this.odds = odds;
		this.chuanguan = chuanguan;
	}
}
