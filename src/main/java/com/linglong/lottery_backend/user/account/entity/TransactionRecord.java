package com.linglong.lottery_backend.user.account.entity;


import org.apache.commons.lang3.time.DateFormatUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "tbl_transaction_record")
@DynamicUpdate
@DynamicInsert
public class TransactionRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "voucher_no")
    private Long voucherNo;

    @Column(name = "record_no")
    private Long recordNo;

    @Column(name = "third_party_record_no")
    private String thirdPartyRecordNo;

    private BigDecimal price;

    private String type;

    private BigDecimal balance;

    @Column(name = "record_status")
    private String recordStatus;

    @Column(name = "record_create_time")
    private java.util.Date recordCreateTime;

    @Column(name = "record_response_time")
    private java.util.Date recordResponseTime;

    @Column(name = "record_finish_time")
    private java.util.Date recordFinishTime;

    @Column(name = "relate_record_id")
    private Long relateRecordId;

    @Column(name = "extend_info")
    private String extendInfo;

    @Version
    @Column(name = "version", columnDefinition = "integer DEFAULT 0", nullable = false)
    private Long version;

    @Column(name = "created_time")
    private java.util.Date createdTime;

    @Column(name = "updated_time")
    private java.util.Date updatedTime;

    @Column(name = "channel_no")
    private String channelNo;

    @Column(name = "recharge_deduct")
    private BigDecimal rechargeDeduct;

    @Column(name = "reward_deduct")
    private BigDecimal rewardDeduct;

    public Long getId() {
        return id;
    }

    public TransactionRecord setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getRecordNo() {
        return recordNo;
    }

    public TransactionRecord setRecordNo(Long recordNo) {
        this.recordNo = recordNo;
        return this;
    }

    public String getThirdPartyRecordNo() {
        return thirdPartyRecordNo;
    }

    public TransactionRecord setThirdPartyRecordNo(String thirdPartyRecordNo) {
        this.thirdPartyRecordNo = thirdPartyRecordNo;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public TransactionRecord setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public String getType() {
        return type;
    }

    public TransactionRecord setType(String type) {
        this.type = type;
        return this;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public TransactionRecord setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public TransactionRecord setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
        return this;
    }

    public Date getRecordCreateTime() {
        return recordCreateTime;
    }

    public TransactionRecord setRecordCreateTime(Date recordCreateTime) {
        this.recordCreateTime = recordCreateTime;
        return this;
    }

    public Date getRecordResponseTime() {
        return recordResponseTime;
    }

    public TransactionRecord setRecordResponseTime(Date recordResponseTime) {
        this.recordResponseTime = recordResponseTime;
        return this;
    }

    public String getRecordFinishTime() {
        return DateFormatUtils.format(this.recordFinishTime, "yyyy年MM月dd日 HH:mm");
    }

    public TransactionRecord setRecordFinishTime(Date recordFinishTime) {
        this.recordFinishTime = recordFinishTime;
        return this;
    }

    public Long getVersion() {
        return version;
    }

    public TransactionRecord setVersion(Long version) {
        this.version = version;
        return this;
    }

    public String getCreatedTime() {
        return DateFormatUtils.format(this.createdTime, "yyyy年MM月dd日 HH:mm");
    }

    public TransactionRecord setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public String getUpdatedTime() {
        return DateFormatUtils.format(this.updatedTime, "yyyy年MM月dd日 HH:mm");
    }

    public TransactionRecord setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public TransactionRecord setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Long getOrderId() {
        return orderId;
    }

    public TransactionRecord setOrderId(Long orderId) {
        this.orderId = orderId;
        return this;
    }

    public Long getVoucherNo() {
        return voucherNo;
    }

    public TransactionRecord setVoucherNo(Long voucherNo) {
        this.voucherNo = voucherNo;
        return this;
    }

    public Long getRelateRecordId() {
        return relateRecordId;
    }

    public void setRelateRecordId(Long relateRecordId) {
        this.relateRecordId = relateRecordId;
    }

    public String getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(String extendInfo) {
        this.extendInfo = extendInfo;
    }

    public String getChannelNo() {
        return channelNo;
    }

    public TransactionRecord setChannelNo(String channelNo) {
        this.channelNo = channelNo;
        return this;
    }

    public BigDecimal getRechargeDeduct() {
        return rechargeDeduct;
    }

    public TransactionRecord setRechargeDeduct(BigDecimal rechargeDeduct) {
        this.rechargeDeduct = rechargeDeduct;
        return this;
    }

    public BigDecimal getRewardDeduct() {
        return rewardDeduct;
    }

    public TransactionRecord setRewardDeduct(BigDecimal rewardDeduct) {
        this.rewardDeduct = rewardDeduct;
        return this;
    }
}
