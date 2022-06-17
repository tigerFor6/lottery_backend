package com.linglong.lottery_backend.prize.entity;

public enum OpenPrizeStatus {

    none(-1),

    wait(0),

    opened(2),

    openedPart(1);

    private Integer value;

    OpenPrizeStatus(Integer index) {
        this.value = index;
    }

    public Integer getValue() {
        return value;
    }

}
