package com.linglong.lottery_backend.user.cashier.entity;

import java.io.Serializable;

public class CashierBody implements Serializable {

    private String appId;
    private String sign;
    private String orderId;
    private String productId;
    private String userId;
    private String userName;
    private String orderCreateTime;
    private String code;
    private String amount;
    private String merchantId;
    private String outTradeNo;

    public String getAppId() {
        return appId;
    }

    public CashierBody setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getSign() {
        return sign;
    }

    public CashierBody setSign(String sign) {
        this.sign = sign;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public CashierBody setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public String getProductId() {
        return productId;
    }

    public CashierBody setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public CashierBody setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public CashierBody setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getOrderCreateTime() {
        return orderCreateTime;
    }

    public CashierBody setOrderCreateTime(String orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
        return this;
    }

    public String getCode() {
        return code;
    }

    public CashierBody setCode(String code) {
        this.code = code;
        return this;
    }

    public String getAmount() {
        return amount;
    }

    public CashierBody setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public CashierBody setMerchantId(String merchantId) {
        this.merchantId = merchantId;
        return this;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public CashierBody setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
        return this;
    }

    @Override
    public String toString() {
        return "CashierBody{" +
                "appId='" + appId + '\'' +
                ", sign='" + sign + '\'' +
                ", orderId='" + orderId + '\'' +
                ", productId='" + productId + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", orderCreateTime='" + orderCreateTime + '\'' +
                ", code='" + code + '\'' +
                ", amount='" + amount + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", outTradeNo='" + outTradeNo + '\'' +
                '}';
    }
}
