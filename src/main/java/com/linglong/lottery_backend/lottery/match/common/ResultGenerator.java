package com.linglong.lottery_backend.lottery.match.common;

/**
 * 化增光
 */
public class ResultGenerator {
    private static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";

    public static Result genSuccessResult() {
        return new Result()
                .setCode(ResultCode.CODE_SUCCESS)
                .setMsg(DEFAULT_SUCCESS_MESSAGE);
    }

    public static <T> Result<T> genSuccessResult(T data) {
        return new Result<T>()
                .setCode(ResultCode.CODE_SUCCESS)
                .setMsg(DEFAULT_SUCCESS_MESSAGE)
                .setResp(data);
    }

    public static <T> Result<T> genProcessingResult(T data) {
        return new Result<T>()
                .setCode(ResultCode.CODE_COMMON_PROCESSING)
                .setMsg(DEFAULT_SUCCESS_MESSAGE)
                .setResp(data);
    }

    public static Result genFailResult(String message) {
        return new Result<Void>()
                .setCode(ResultCode.CODE_COMMON_ERROR)
                .setMsg(message);
    }


    public static Result genExceptionResult(Integer code, String message) {
        return new Result<Void>()
                .setCode(code)
                .setMsg(message);
    }
}
