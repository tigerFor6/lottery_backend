package com.linglong.lottery_backend.listener.bean;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.linglong.lottery_backend.common.bean.LockKey;

import java.io.Serializable;
import java.math.BigDecimal;

public class CommonRecord implements Serializable {

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("voucher_no")
    private Long voucherNo;
    @JsonProperty("amount")
    private BigDecimal amount;

    public Long getOrderId() {
        return orderId;
    }

    public CommonRecord setOrderId(Long orderId) {
        this.orderId = orderId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public CommonRecord setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Long getVoucherNo() {
        return voucherNo;
    }

    public CommonRecord setVoucherNo(Long voucherNo) {
        this.voucherNo = voucherNo;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public CommonRecord setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

}
