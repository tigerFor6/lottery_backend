package com.linglong.lottery_backend.activity.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "tbl_user_coupon")
@DynamicUpdate
@DynamicInsert
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TblUserCoupon implements Serializable,Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_coupon_id")
    private Long userCouponId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "activity_id")
    private Long activityId;

    @Column(name = "coupon_id")
    private Long couponId;

    @Column(name = "coupon_type")
    private Integer couponType;

    @Column(name = "coupon_name")
    private String couponName;

    @Column(name = "coupon_amount")
    private BigDecimal couponAmount;

    @Column(name = "coupon_rules")
    private String couponRules;

    @Column(name = "coupon_status")
    private Integer couponStatus;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

//    @Column(name = "created_time")
//    private Date createdTime;
//
//    @Column(name = "updated_time")
//    private Date updatedTime;

    @Column(name = "version")
    @Version
    private Long version;

    @Transient
    private String formatStartDate;

    @Transient
    private String formatEndDate;

    public String getFormatStartDate() {
        return DateFormatUtils.format(this.startTime,"yyyy-MM-dd");
    }

    public void setFormatStartDate(String formatStartDate) {
        this.formatStartDate = formatStartDate;
    }

    public String getFormatEndDate() {
        return DateFormatUtils.format(this.endTime,"yyyy-MM-dd");
    }

    public void setFormatEndDate(String formatEndDate) {
        this.formatEndDate = formatEndDate;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
