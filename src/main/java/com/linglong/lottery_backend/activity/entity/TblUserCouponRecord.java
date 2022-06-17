package com.linglong.lottery_backend.activity.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity(name = "tbl_user_coupon_record")
@DynamicUpdate
@DynamicInsert
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TblUserCouponRecord implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer type;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "record_id")
	private Long recordId;

	@Column(name = "user_coupon_id")
	private Long userCouponId;

	@Column(name = "coupon_id")
	private Long couponId;

	@Column(name = "coupon_type")
	private Integer couponType;

	@Column(name = "voucher_no")
	private Long voucherNo;

	private BigDecimal amount;

	@Column(name = "create_time")
	private java.util.Date createTime;

	@Column(name = "update_time")
	private java.util.Date updateTime;

}
