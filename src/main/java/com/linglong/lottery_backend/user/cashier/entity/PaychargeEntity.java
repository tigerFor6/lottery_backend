package com.linglong.lottery_backend.user.cashier.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PaychargeEntity implements Serializable {

    private String code;

    private String msg;

    private String orderId;

    private String amount;

    private String expireTime;

    private  String mchIncome;

    private String createTime;

    private String mchOrderNo;

    private String paySuccTime;

    private String channelOrderNo;

    private String status;

    private String remark;
}
