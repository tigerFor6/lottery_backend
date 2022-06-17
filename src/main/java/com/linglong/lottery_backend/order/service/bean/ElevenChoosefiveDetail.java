package com.linglong.lottery_backend.order.service.bean;

import java.util.Objects;

public class ElevenChoosefiveDetail {

    private String number;

    private String betNumber;

    private Integer orderFee;

    private String elevenChoosefiveType;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBetNumber() {
        return betNumber;
    }

    public void setBetNumber(String betNumber) {
        this.betNumber = betNumber;
    }

    public Integer getOrderFee() {
        return orderFee;
    }

    public void setOrderFee(Integer orderFee) {
        this.orderFee = orderFee;
    }

    public String getElevenChoosefiveType() {
        return elevenChoosefiveType;
    }

    public void setElevenChoosefiveType(String elevenChoosefiveType) {
        this.elevenChoosefiveType = elevenChoosefiveType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElevenChoosefiveDetail that = (ElevenChoosefiveDetail) o;
        return Objects.equals(number, that.number) &&
                Objects.equals(betNumber, that.betNumber) &&
                Objects.equals(orderFee, that.orderFee) &&
                Objects.equals(elevenChoosefiveType, that.elevenChoosefiveType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, betNumber, orderFee, elevenChoosefiveType);
    }
}
