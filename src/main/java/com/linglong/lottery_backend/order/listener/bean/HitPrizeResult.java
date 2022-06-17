package com.linglong.lottery_backend.order.listener.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: qihua.li
 * @since: 2019-04-19
 */
@Data
public class HitPrizeResult implements Serializable {
    private Long orderId;
    @JSONField(name = "amount")
    private BigDecimal bonus;
    @JSONField(name = "status")
    private int hitPrizeStatus;
}
