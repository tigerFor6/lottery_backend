package com.linglong.lottery_backend.common.error;

/**
 * Created by ynght on 2019-04-20
 */
public class BusinessException extends RuntimeException {

    public BusinessException() {
        super();
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(Object message) {
        super(message.toString());
    }
}
