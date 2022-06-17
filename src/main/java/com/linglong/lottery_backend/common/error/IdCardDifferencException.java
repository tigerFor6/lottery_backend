package com.linglong.lottery_backend.common.error;

/**
 * @Author: qihua.li
 * @since: 2019-03-29
 */
public class IdCardDifferencException extends RuntimeException {
    public IdCardDifferencException() {
        super();
    }

    public IdCardDifferencException(String message) {
        super(message);
    }

    public IdCardDifferencException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdCardDifferencException(Throwable cause) {
        super(cause);
    }
}
