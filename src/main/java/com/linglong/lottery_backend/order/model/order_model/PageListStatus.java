package com.linglong.lottery_backend.order.model.order_model;

/**
 * @Author: qihua.li
 * @since: 2019-04-03
 */
public enum PageListStatus {
    WAIT_PAY("待支付"),//0
    CANCEL("已取消"),//1
    NOT_ISSUE("待出票"),//2
    ORDER_FAIL("订单失败"),//8
    NOT_LOTTERY("待开奖"),//4
    PART_NOT_LOTTERY("部分出票，待开奖"),//5
    WIN_BONUS("已中奖yy元"),//6
    BILL_FAIL("出票失败"),//3
    LOST("未中奖"),//7
    BIG_REWARD_VERIFYING("派奖审核中");//8

    private String status;

    PageListStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    PageListStatus() {
    }}
