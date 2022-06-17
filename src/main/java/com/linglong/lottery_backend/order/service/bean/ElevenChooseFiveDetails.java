package com.linglong.lottery_backend.order.service.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/5/31
 */
@Data
public class ElevenChooseFiveDetails {

    @JSONField(name = "elevenChoosefiveType")
    private String elevenChoosefiveType;
    @JSONField(name = "betNumber")
    private String betNumber;
    @Column(name = "order_fee")
    private BigDecimal orderFee;
    @JSONField(name = "number")
    private String number;
}
