package com.linglong.lottery_backend.prize.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "tbl_betting_ticket_group")
@DynamicUpdate
@DynamicInsert
public class TblBettingTicketGroup implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "ticket_group_id")
	private Long ticketGroupId;

	@Column(name = "betting_ticket_id")
	private Long bettingTicketId;

	@Column(name = "detail")
	private String detail;

	@Column(name = "pre_tax_bonus")
	private BigDecimal preTaxBonus;

	@Column(name = "aft_tax_bonus")
	private BigDecimal aftTaxBonus;

	@Column(name = "status")
	private Integer status;

	@Column(name = "multiple")
	private Integer multiple;

	@Column(name = "created_time")
	private java.util.Date createdTime;

	@Column(name = "updated_time")
	private java.util.Date updatedTime;

	@Version
	@Column(name = "version")
	private Long version;

	public Long getId() {
		return id;
	}

	public TblBettingTicketGroup setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getTicketGroupId() {
		return ticketGroupId;
	}

	public TblBettingTicketGroup setTicketGroupId(Long ticketGroupId) {
		this.ticketGroupId = ticketGroupId;
		return this;
	}

	public Long getBettingTicketId() {
		return bettingTicketId;
	}

	public TblBettingTicketGroup setBettingTicketId(Long bettingTicketId) {
		this.bettingTicketId = bettingTicketId;
		return this;
	}

	public String getDetail() {
		return detail;
	}

	public TblBettingTicketGroup setDetail(String detail) {
		this.detail = detail;
		return this;
	}

	public BigDecimal getPreTaxBonus() {
		return preTaxBonus;
	}

	public TblBettingTicketGroup setPreTaxBonus(BigDecimal preTaxBonus) {
		this.preTaxBonus = preTaxBonus;
		return this;
	}

	public BigDecimal getAftTaxBonus() {
		return aftTaxBonus;
	}

	public TblBettingTicketGroup setAftTaxBonus(BigDecimal aftTaxBonus) {
		this.aftTaxBonus = aftTaxBonus;
		return this;
	}

	public Integer getStatus() {
		return status;
	}

	public Integer getMultiple() {
		return multiple;
	}

	public void setMultiple(Integer multiple) {
		this.multiple = multiple;
	}

	public TblBettingTicketGroup setStatus(Integer status) {
		this.status = status;
		return this;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public TblBettingTicketGroup setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
		return this;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public TblBettingTicketGroup setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
		return this;
	}

	public Long getVersion() {
		return version;
	}

	public TblBettingTicketGroup setVersion(Long version) {
		this.version = version;
		return this;
	}
}
