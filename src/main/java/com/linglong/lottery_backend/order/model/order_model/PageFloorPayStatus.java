package com.linglong.lottery_backend.order.model.order_model;

/**
 * @Author: qihua.li
 * @since: 2019-04-03
 */
public enum PageFloorPayStatus {
    WAIT_PAY("待支付"),//0
    NOT_PAY("未支付"),//1
    OVERTIME_XX_REFUNDING("支付超时，XX元退款中"),//2
    OVERTIME_XX_REFUNDED("支付超时，XX元已退款"),//3
    PAID("已支付"),//4
    XX_REFUNDING("XX元退款中"),//5
    XX_REFUNDED("XX元已退款"),//6
    XX_GRANT("YY元已派奖"),//7
    YY_GRANT_XX_REFUNDING("YY元已派奖，XX元退款中"),//8
    YY_GRANT_XX_REFUNDED("YY元已派奖，XX元已退款"),//9
    XX_REFUNDED_TO_BALANCE("XX元退款到账户余额"),//10
    BIG_REWARD_VERIFYING("派奖审核中");//11

    private String status;

    PageFloorPayStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    PageFloorPayStatus() {
    }}
