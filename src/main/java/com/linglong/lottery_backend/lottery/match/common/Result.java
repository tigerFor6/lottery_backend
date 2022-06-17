package com.linglong.lottery_backend.lottery.match.common;

import com.google.gson.Gson;

/**
 * 化增光
 *
 * @param <T>
 */
public class Result<T> {
    private Integer code = ResultCode.CODE_COMMON_ERROR;
    private String msg;
    private T resp;

    public Result setCode(Integer code) {
        this.code = code;
        return this;
    }

    public boolean isSuccess() {
        if (this.getCode().equals(ResultCode.CODE_SUCCESS)) {
            return true;
        } else {
            return false;
        }
    }

    public Result setMsg(String bizMessage) {
        this.msg = bizMessage;
        return this;
    }

    public T getResp() {
        return resp;
    }

    public Result setResp(T data) {
        this.resp = data;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
