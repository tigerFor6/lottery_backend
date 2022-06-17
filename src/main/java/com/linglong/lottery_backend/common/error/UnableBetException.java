package com.linglong.lottery_backend.common.error;

/**
 * @Author: qihua.li
 * @since: 2019-03-26
 */
public class UnableBetException extends RuntimeException {
    public UnableBetException() {
        super();
    }

    public UnableBetException(String message) {
        super(message);
    }

    public UnableBetException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnableBetException(Throwable cause) {
        super(cause);
    }
}
