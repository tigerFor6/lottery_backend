package com.linglong.lottery_backend.activity.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "tbl_coupon")
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
@NoArgsConstructor
public class TblCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_id")
    private Long couponId;
    private Integer type;
    private String name;
    private BigDecimal amount;
    private Long number;
    private BigDecimal budget;
    @Column(name = "start_time")
    private Date startTime;
    @Column(name = "end_time")
    private Date endTime;
    private String rules;
    private String remarks;
    private Boolean enabled;
    @Column(name = "created_time")
    private Date createdTime;
    @Column(name = "updated_time")
    private Date updatedTime;

    @Version
    private Long version;

}
