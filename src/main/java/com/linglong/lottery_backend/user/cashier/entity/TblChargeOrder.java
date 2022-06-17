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
@Entity(name = "tbl_charge_order")
@DynamicUpdate
@DynamicInsert
@Data
public class TblChargeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "record_no")
    private Long recordNo;

    @Column(name = "third_party_record_no")
    private String thirdPartyRecordNo;

    @Column(name = "code")
    private String code;

    @Column(name = "recharge_amount")
    private BigDecimal rechargeAmount;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "reality_amount")
    private BigDecimal realityAmount;

    @Column(name = "type")
    private String type;

    @Column(name = "record_status")
    private String recordStatus;

    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "opening_bank")
    private String openingBank;

    @Column(name = "channel_no")
    private String channelNo;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;

    @Version
    private Integer version;
}
