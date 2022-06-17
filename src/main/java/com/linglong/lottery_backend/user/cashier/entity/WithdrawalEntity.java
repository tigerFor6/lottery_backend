package com.linglong.lottery_backend.user.cashier.entity;

import java.io.Serializable;

public class WithdrawalEntity implements Serializable {

    private String sign;

    private String orderId;

    private String code;

    private String amount;

    private String merchantId;

    private String outTradeNo;

    private String appId;

    private String msg;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "WithdrawalEntity{" +
                "sign='" + sign + '\'' +
                ", orderId='" + orderId + '\'' +
                ", code='" + code + '\'' +
                ", amount='" + amount + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", outTradeNo='" + outTradeNo + '\'' +
                ", appId='" + appId + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
