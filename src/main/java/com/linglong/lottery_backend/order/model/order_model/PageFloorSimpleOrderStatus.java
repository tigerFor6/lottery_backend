package com.linglong.lottery_backend.order.model.order_model;

/**
 * @Author: qihua.li
 * @since: 2019-04-13
 */
public enum PageFloorSimpleOrderStatus {
    CONTINUE_PAY("继续支付"),//0
    CANCEL("已取消"),//1
    WAIT_BILL("待出票"),//2
    BILL_FAIL("出票失败"),//3
    WAIT_LOTTERY("待开奖"),//4
    WIN_LOTTERY("已中奖"),//5
    LOST_LOTTERY("未中奖"),//6
    ORDER_FAIL("订单失败");//7

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    PageFloorSimpleOrderStatus(String status) {
        this.status = status;
    }

    PageFloorSimpleOrderStatus() {
    }}
