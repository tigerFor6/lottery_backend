package com.linglong.lottery_backend.order.model.order_model;

/**
 * @Author: qihua.li
 * @since: 2019-04-03
 */
public enum PageFloorOrderStatus {
    NOT_ABORT("未截止"),//0
    CANCEL("已取消"),//1
    NOT_ISSUE("待出票"),//2
    ISSUE_FAIL("出票失败"),//3
    NOT_OPEN_LOTTERY("待开奖"),//4
    PART_ISSUE_NOT_OPEN_LOTTERY("部分出票，未开奖"),//5
    PART_LOTTERY_WIN("部分开奖，已中奖"),//6
    PART_ISSUE_PART_LOTTERY_WIN("部分出票，部分开奖，已中奖"),//7
    PART_ISSUE_ALL_LOTTERY_WIN("部分出票，全部开奖，已中奖"),//8
    ALL_LOTTERY_WIN("全部开奖，已中奖"),//9
    LOST("未中奖"),//10
    ORDER_FAIL("订单失败"),//11
    DELIVERY_VERIFYING("派奖审核中");//12

    private String status;

    PageFloorOrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    PageFloorOrderStatus() {
    }}
