package com.linglong.lottery_backend.prize.entity;

public enum PrizeStatus {

    none(-1),

    wait(0),

    losing(2),

    awarded(1),

    bigAwarded(99),

    refund(9);

    private Integer value;

    PrizeStatus(Integer index) {
        this.value = index;
    }

    public Integer getValue() {
        return value;
    }

}
