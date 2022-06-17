package com.linglong.lottery_backend.order.model.order_model;

public enum OrderDetailStatus {
    WAIT_PAY(0, "已下单","待支付"),
    CANCEL_NOT_PAY(1, "已取消","未支付"),
    BILL_FAIL_WITHDRAWRLING(2, "出票失败","xx元已退款"),
    BILLING_PAIED(3, "待出票","已支付"),
    OPENING_PAIED(4, "待开奖","已支付"),
    NOT_PRIZING_PAIED(5, "未中奖","已支付"),
    PRIZED_VERIFYING(6, "已中奖","派奖审核中"),
    PRIZED_DELIVERED(7, "已中奖","xx元已派奖"),
    PART_BILLING_WITHDRAWALED(8, "部分出票，待开奖","xx元已退款"),
    PART_BILLING_NOT_PRIZED(9, "部分出票，未中奖","xxx元已退款"),
    PART_BILLING_PRIZED_VERIFYING(10, "部分出票，已中奖","派奖审核中，xx元已退款"),
    PART_BILLING_PRIZED_DELIVERED(11, "部分出票，已中奖","xxx元已派奖，xx元已退款"),
    PROCESSING_AND_PAIED(12,"出票中","已支付")
    ;

    private final Integer code;

    private final String orderStatus;

    private final String payStatus;

    public Integer getCode() {
        return code;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getPayStatus() {
        return payStatus;
    }

    OrderDetailStatus(Integer code, String orderStatus, String payStatus) {
        this.code = code;
        this.orderStatus = orderStatus;
        this.payStatus = payStatus;
    }
}
