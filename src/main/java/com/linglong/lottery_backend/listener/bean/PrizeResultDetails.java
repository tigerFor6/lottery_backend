package com.linglong.lottery_backend.listener.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;

public class PrizeResultDetails implements Serializable {

    private String code;

    private String result;

    private BigDecimal odds;

    @JsonProperty("is_danguan")
    private Integer isDanguan;

    public String getCode() {
        return code;
    }

    public PrizeResultDetails setCode(String code) {
        this.code = code;
        return this;
    }

    public String getResult() {
        return result;
    }

    public PrizeResultDetails setResult(String result) {
        this.result = result;
        return this;
    }

    public BigDecimal getOdds() {
        return odds;
    }

    public PrizeResultDetails setOdds(BigDecimal odds) {
        this.odds = odds;
        return this;
    }

    public Integer getIsDanguan() {
        return isDanguan;
    }

    public PrizeResultDetails setIsDanguan(Integer isDanguan) {
        this.isDanguan = isDanguan;
        return this;
    }
}
