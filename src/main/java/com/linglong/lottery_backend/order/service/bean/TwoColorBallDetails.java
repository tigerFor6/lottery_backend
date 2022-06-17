package com.linglong.lottery_backend.order.service.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/5/20
 */
@Data
public class TwoColorBallDetails {
    @JSONField(name = "twoColorBallType")
    private String twoColorBallType;
    @JSONField(name = "betNumber")
    private String betNumber;
    @Column(name = "order_fee")
    private BigDecimal orderFee;
    @JSONField(name = "RedNumber")
    private String RedNumber;
    @JSONField(name = "BlueNumber")
    private String BlueNumber;
}
