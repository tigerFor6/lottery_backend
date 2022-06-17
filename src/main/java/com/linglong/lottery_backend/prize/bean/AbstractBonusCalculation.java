package com.linglong.lottery_backend.prize.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public abstract class AbstractBonusCalculation implements Serializable {

    private Integer period;

    private Integer lotteryType;

}
