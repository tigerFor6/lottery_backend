package com.linglong.lottery_backend.lottery.digital.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Entity(name = "tbl_digital_results")
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
public class TblDigitalResults implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "source")
	private String source;

	@Column(name = "period")
	private Integer period;

	@Column(name = "lottery_type")
	private Integer lotteryType;

	@Column(name = "lottery_time")
	private String lotteryTime;

	@Column(name = "lottery_sn")
	private String lotterySn;

	@Column(name = "lottery_result")
	private String lotteryResult;

	@Column(name = "sales_amount")
	private BigDecimal salesAmount;

	@Column(name = "prize_pool_amount")
	private BigDecimal prizePoolAmount;

	@Column(name = "lottery_detail")
	private String lotteryDetail;

	@Column(name = "created_time")
	private Date createdTime;

	@Column(name = "updated_time")
	private Date updatedTime;

	@Column(name = "version")
	private Integer version;

}
