package com.linglong.lottery_backend.order.model;

public enum StatusCode {
    OK(20000),         //结果成功
    ERROR(20001),      //结果失败
    LOGIN_ERROR(20002), //用户名或密码错误
    ACCESS_ERROR(20003),    //权限不足
    NOTFOUND_ERROR(20004), //资源获取不到
    INPUT_ERROR(20006),
    CHECK_ERROR(20005);

    private int code;

    StatusCode(int code) {
        this.code = code;
    }

    StatusCode() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
