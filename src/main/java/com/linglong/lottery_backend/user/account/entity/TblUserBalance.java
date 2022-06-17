package com.linglong.lottery_backend.user.account.entity;


import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "tbl_user_balance")
@DynamicUpdate
@DynamicInsert
public class TblUserBalance implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "recharge_amount")
    private BigDecimal rechargeAmount;

    @Column(name = "reward_amount")
    private BigDecimal rewardAmount;

    @Column(name = "total_balance")
    private BigDecimal totalBalance;

    @Version
    @Column(name = "version", columnDefinition = "integer DEFAULT 0", nullable = false)
    private Long version;

    @Column(name = "created_time")
    private java.util.Date createdTime;

    @Column(name = "updated_time")
    private java.util.Date updatedTime;

    public Long getId() {
        return id;
    }

    public TblUserBalance setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public TblUserBalance setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public BigDecimal getRechargeAmount() {
        return rechargeAmount;
    }

    public TblUserBalance setRechargeAmount(BigDecimal rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
        return this;
    }

    public BigDecimal getRewardAmount() {
        return rewardAmount;
    }

    public TblUserBalance setRewardAmount(BigDecimal rewardAmount) {
        this.rewardAmount = rewardAmount;
        return this;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public TblUserBalance setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
        return this;
    }

    public Long getVersion() {
        return version;
    }

    public TblUserBalance setVersion(Long version) {
        this.version = version;
        return this;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public TblUserBalance setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public TblUserBalance setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }
}
