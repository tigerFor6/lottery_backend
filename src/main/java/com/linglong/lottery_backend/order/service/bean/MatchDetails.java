package com.linglong.lottery_backend.order.service.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: qihua.li
 * @since: 2019-04-17
 */
@Data
public class MatchDetails implements Serializable {

    @JSONField(name = "order_id")
    private Long orderId;
    @JSONField(name = "chuan_guan")
    private String chuanGuan;
    private BigDecimal bonus;
    private Integer multiple;
    @JSONField(name = "bet_number")
    private Integer betNumber;
    private String deadline;
    @JSONField(name = "deadline_match")
    private String deadlineMatch;
    @JSONField(name = "delivery_prize_status")
    private Integer deliveryPrizeStatus;
    @JSONField(name = "hit_prize_status")
    private Integer hitPrizeStatus;
    @JSONField(name = "open_prize_status")
    private Integer openPrizeStatus;
    @JSONField(name = "bill_status")
    private Integer billStatus;
    @JSONField(name = "pay_status")
    private Integer payStatus;
    private List<Match> matchs;
}
