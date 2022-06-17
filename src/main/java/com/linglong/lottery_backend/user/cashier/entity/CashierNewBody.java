package com.linglong.lottery_backend.user.cashier.entity;

import java.io.Serializable;

public class CashierNewBody implements Serializable {

    private String appId;
    private String sign;
    private String orderId;
    private String productId;
    private String userId;
    private String userName;
    private String orderSuccessTime;
    private String code;
    private String amount;
    private String merchantId;
    private String outTradeNo;

    public String getAppId() {
        return appId;
    }

    public CashierNewBody setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getSign() {
        return sign;
    }

    public CashierNewBody setSign(String sign) {
        this.sign = sign;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public CashierNewBody setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public String getProductId() {
        return productId;
    }

    public CashierNewBody setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public CashierNewBody setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public CashierNewBody setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getOrderSuccessTime() {
        return orderSuccessTime;
    }

    public CashierNewBody setOrderSuccessTime(String orderSuccessTime) {
        this.orderSuccessTime = orderSuccessTime;
        return this;
    }

    public String getCode() {
        return code;
    }

    public CashierNewBody setCode(String code) {
        this.code = code;
        return this;
    }

    public String getAmount() {
        return amount;
    }

    public CashierNewBody setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public CashierNewBody setMerchantId(String merchantId) {
        this.merchantId = merchantId;
        return this;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public CashierNewBody setOutTradeNo(String outTradeNo) {
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
                ", orderSuccessTime='" + orderSuccessTime + '\'' +
                ", code='" + code + '\'' +
                ", amount='" + amount + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", outTradeNo='" + outTradeNo + '\'' +
                '}';
    }
}
