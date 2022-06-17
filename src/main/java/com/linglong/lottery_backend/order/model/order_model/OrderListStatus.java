package com.linglong.lottery_backend.order.model.order_model;

public enum OrderListStatus {
    WAIT_PAY("0", "待支付"),
    CANCEL("1", "已取消"),
    NOT_ISSUE("2", "待出票"),
    ORDER_FAIL("3", "出票失败，已退款"),
    NOT_LOTTERY("4", "待开奖"),
    PART_NOT_LOTTERY("5", "部分出票，待开奖"),
    BILL_FAIL("6", "未中奖"),
    WIN_BONUS("7", "已中奖yy元"),
    PROCESSING_ISSUE("8","出票中");

    private final String code;

    private final String value;

    OrderListStatus(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
