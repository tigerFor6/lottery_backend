package com.linglong.lottery_backend.common.error;

/**
 * @Author: qihua.li
 * @since: 2019-04-01
 */
public class RepetitionBindingException extends RuntimeException {
    public RepetitionBindingException() {
        super();
    }

    public RepetitionBindingException(String message) {
        super(message);
    }

    public RepetitionBindingException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepetitionBindingException(Throwable cause) {
        super(cause);
    }
}
