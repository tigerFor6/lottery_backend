package com.linglong.lottery_backend.order.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result implements Serializable {

    private Integer code;  //返回状态码
    private String message;//返回信息
    private Object data;   //返回数据

    public Result(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(Integer code, Object data) {
        this.code = code;
        this.data = data;
    }
}
