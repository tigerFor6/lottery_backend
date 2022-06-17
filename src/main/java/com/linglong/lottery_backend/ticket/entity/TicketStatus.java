package com.linglong.lottery_backend.ticket.entity;

public enum TicketStatus {

    unknown(-3),

    mfail(-1),

    wait(0),

    processing(1),

    success(2),

    fail(-2),

    successPart(3);

    private Integer value;
    TicketStatus(Integer index) {
        this.value = index;
    }

    public Integer getValue() {
        return value;
    }

}
