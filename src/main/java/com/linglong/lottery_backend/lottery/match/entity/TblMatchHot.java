package com.linglong.lottery_backend.lottery.match.entity;


import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity(name = "tbl_match_hot")
@DynamicUpdate
@DynamicInsert
@Data
public class TblMatchHot implements Serializable {

	@Id
	private Long id;

	@Column(name = "match_id")
	private Long matchId;

	private String period;

	private Integer level;

	private String lable;

	@Column(name = "lable_status")
	private Integer lableStatus;

	@Column(name = "created_time")
	private java.util.Date createdTime;

	@Column(name = "updated_time")
	private java.util.Date updatedTime;

}
