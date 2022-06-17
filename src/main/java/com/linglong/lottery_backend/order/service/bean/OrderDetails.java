package com.linglong.lottery_backend.order.service.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.util.List;

/**
 * @Author: qihua.li
 * @since: 2019-04-22
 */
@Data
public class OrderDetails implements Serializable {
    @JSONField(name = "open_prize_status")
    private int openPrizeStatus;
    @JSONField(name = "hit_prize_status")
    private int hitPrizeStatus;
    @JSONField(name = "pay_status")
    private int payStatus;
    @JSONField(name = "bill_status")
    private int billStatus;
    @JSONField(name = "delivery_prize_status")
    private int deliveryPrizeStatus;
    private BigDecimal bonus;
    @JSONField(name = "chuan_guan")
    private String chuanGuan;
    @JSONField(name = "multiple")
    private Integer multiple;
    @JSONField(name = "bet_number")
    private Long betNumber;
    @JSONField(name = "deadline_match")
    private String deadlineMatch;
    private String deadline;
    @JSONField(name = "order_id")
    private Long orderId;
    @JSONField(name = "chasing")
    private String chasing;
    @JSONField(name = "twoColorBallPeriods")
    private String twoColorBallPeriods;
    @JSONField(name = "elevenChooseFivePeriods")
    private String elevenChooseFivePeriods;
    @JSONField(name = "superLottoPeriods")
    private String superLottoPeriods;
    @JSONField(name = "rankThreePeriods")
    private String rankThreePeriods;
    @JSONField(name = "rankFivePeriods")
    private String rankFivePeriods;
    /**
     * 期次
     */
    @JSONField(name = "periods")
    private String periods;
    private List<Match> matchs;
    private List<TwoColorBallDetails> twoColorBallDetails;
    private List<ElevenChooseFiveDetails> elevenChooseFiveDetails;
    private List<SuperLottoDetails> superLottoDetails;
    private List<RankThreeDetails> rankThreeDetails;
    private List<RankFiveDetails> rankFiveDetails;
}
