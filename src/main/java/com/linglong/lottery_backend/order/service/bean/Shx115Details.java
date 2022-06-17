package com.linglong.lottery_backend.order.service.bean;

import net.sf.json.JSONArray;

import java.util.List;

/**
 * 陕11选5 details
 */
public class Shx115Details {

    private Integer bet_number;

    private Integer bill_status;

    private Integer bonus;

    private Integer delivery_prize_status;

    private Integer hit_prize_status;

    private Integer multiple;

    private Integer open_prize_status;

    private String order_id;

    private Integer pay_status;

    private String elevenChoosefivePeriods;

    private List<ElevenChoosefiveDetail> elevenChoosefiveDetails;

    public Integer getBet_number() {
        return bet_number;
    }

    public void setBet_number(Integer bet_number) {
        this.bet_number = bet_number;
    }

    public Integer getBill_status() {
        return bill_status;
    }

    public void setBill_status(Integer bill_status) {
        this.bill_status = bill_status;
    }

    public Integer getBonus() {
        return bonus;
    }

    public void setBonus(Integer bonus) {
        this.bonus = bonus;
    }

    public Integer getDelivery_prize_status() {
        return delivery_prize_status;
    }

    public void setDelivery_prize_status(Integer delivery_prize_status) {
        this.delivery_prize_status = delivery_prize_status;
    }

    public Integer getHit_prize_status() {
        return hit_prize_status;
    }

    public void setHit_prize_status(Integer hit_prize_status) {
        this.hit_prize_status = hit_prize_status;
    }

    public Integer getMultiple() {
        return multiple;
    }

    public void setMultiple(Integer multiple) {
        this.multiple = multiple;
    }

    public Integer getOpen_prize_status() {
        return open_prize_status;
    }

    public void setOpen_prize_status(Integer open_prize_status) {
        this.open_prize_status = open_prize_status;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public Integer getPay_status() {
        return pay_status;
    }

    public void setPay_status(Integer pay_status) {
        this.pay_status = pay_status;
    }

    public String getElevenChoosefivePeriods() {
        return elevenChoosefivePeriods;
    }

    public void setElevenChoosefivePeriods(String elevenChoosefivePeriods) {
        this.elevenChoosefivePeriods = elevenChoosefivePeriods;
    }

    public List<ElevenChoosefiveDetail> getElevenChoosefiveDetails() {
        return elevenChoosefiveDetails;
    }

    public void setElevenChoosefiveDetails(List<ElevenChoosefiveDetail> elevenChoosefiveDetails) {
        this.elevenChoosefiveDetails = elevenChoosefiveDetails;
    }
}
