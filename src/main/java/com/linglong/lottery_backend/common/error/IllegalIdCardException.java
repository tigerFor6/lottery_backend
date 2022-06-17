package com.linglong.lottery_backend.common.error;

/**
 * @Author: qihua.li
 * @since: 2019-03-31
 */
public class IllegalIdCardException extends RuntimeException {
    public IllegalIdCardException() {
        super();
    }

    public IllegalIdCardException(String message) {
        super(message);
    }

    public IllegalIdCardException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalIdCardException(Throwable cause) {
        super(cause);
    }

    protected IllegalIdCardException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
