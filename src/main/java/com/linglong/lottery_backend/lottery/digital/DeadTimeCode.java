package com.linglong.lottery_backend.lottery.digital;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/6/3
 */
public enum DeadTimeCode {

    TWOCOLORBALL(" 19:25:00"),
    SUPERLOTTO(" 19:25:00"),
    RANKTHREE(" 19:25:00"),
    RANKFIVE(" 19:25:00");

    private String code;

    DeadTimeCode(String code) {
        this.code = code;
    }
    DeadTimeCode(){

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }}
