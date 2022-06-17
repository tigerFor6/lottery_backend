package com.linglong.lottery_backend.user.cashier.entity;

import java.util.Date;

public class SubstituteRecord {

    private String recordNo;

    private String amount;

    private String userName;

    private String userId;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private Date createdTime;

    private String phoneNumber;

    public String getRecordNo() {
        return recordNo;
    }

    public void setRecordNo(String recordNo) {
        this.recordNo = recordNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getUserId() {
        return userId;
    }

    public SubstituteRecord setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public SubstituteRecord() {
    }

    public SubstituteRecord(String recordNo, String amount, String userName, Date createdTime, String phoneNumber) {
        this.recordNo = recordNo;
        this.amount = amount;
        this.userName = userName;
        this.createdTime = createdTime;
        this.phoneNumber = phoneNumber;
    }
}
