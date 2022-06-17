package com.linglong.lottery_backend.prize.entity.custom;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class DigitalDetail implements Serializable {
    private int level;
    private int bet;
    private BigDecimal bonus;
}
