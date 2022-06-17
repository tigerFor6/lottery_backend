package com.linglong.lottery_backend.user.cashier.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/7/26
 */
@Entity(name = "tbl_payment_configuration")
@DynamicUpdate
@DynamicInsert
@Data
public class TblPaymentConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "picture")
    private String picture;

    @Column(name = "weight")
    private Long weight;

    @Column(name = "min_amount")
    private BigDecimal minAmount;

    @Column(name = "max_amount")
    private BigDecimal maxAmount;

    @Column(name = "discount_status")
    private Integer discountStatus;

    @Column(name = "discount_min_amount")
    private BigDecimal discountMinAmount;

    @Column(name = "discount_max_amount")
    private BigDecimal discountMaxAmount;

    @Column(name = "description")
    private String description;

    @Column(name = "remark")
    private String remark;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;

    @Version
    private Integer version;
}
