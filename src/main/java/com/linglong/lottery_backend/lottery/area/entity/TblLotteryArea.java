package com.linglong.lottery_backend.lottery.area.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity(name = "tbl_lottery_area")
@DynamicUpdate
@DynamicInsert
@Data
public class TblLotteryArea implements Serializable {

	@Id
	private Long id;

	@Column(name = "lottery_img_url")
	private String lotteryImgUrl;

	@Column(name = "lable_img_url")
	private String lableImgUrl;

	@Column(name = "jump_url")
	private String jumpUrl;

	@Column(name = "text_plan")
	private String textPlan;

	@Column(name = "game_name")
	private String gameName;

	@Column(name = "game_type")
	private Integer gameType;

	private Integer enable;

	private Integer level;

	@Column(name = "created_time")
	private java.util.Date createdTime;

	@Column(name = "updated_time")
	private java.util.Date updatedTime;

}
