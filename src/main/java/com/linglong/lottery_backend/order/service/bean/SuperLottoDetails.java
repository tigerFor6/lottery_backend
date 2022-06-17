package com.linglong.lottery_backend.order.service.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SuperLottoDetails {
    @JSONField(name = "blueNumber")
    private String blueNumber;
    @JSONField(name = "redNumber")
    private String redNumber;
    @JSONField(name = "betNumber")
    private String betNumber;
    @JSONField(name = "order_fee")
    private BigDecimal orderFee;
    @JSONField(name = "superLottoType")
    private String superLottoType;

}
