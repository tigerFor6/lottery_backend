//package com.linglong.lottery_backend.activity.entity;
//
//
//
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.hibernate.annotations.DynamicInsert;
//import org.hibernate.annotations.DynamicUpdate;
//
//import javax.persistence.*;
//import java.io.Serializable;
//
//@Entity(name = "tbl_coupon_count")
//@DynamicUpdate
//@DynamicInsert
//@Getter
//@Setter
//@NoArgsConstructor
//public class TblCouponCount implements Serializable {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long id;
//
//	@Column(name = "coupon_id")
//	private Long couponId;
//
//	private Integer total;
//
//	@Column(name = "draw_frequency")
//	private Integer drawFrequency;
//
//	@Column(name = "draw_number")
//	private Integer drawNumber;
//
//	@Column(name = "used_requency")
//	private Integer usedRequency;
//
//	@Column(name = "used_number")
//	private Integer usedNumber;
//
//	@Column(name = "created_time")
//	private java.util.Date createdTime;
//
//	@Column(name = "update_time")
//	private java.util.Date updateTime;
//
//	private Integer version;
//
//}
