package com.linglong.lottery_backend.common.error;

/**
 * @Author: qihua.li
 * @since: 2019-03-20
 */
public final class NotCaptchaException extends RuntimeException {

    public NotCaptchaException() {
        super();
    }

    public NotCaptchaException(String message) {
        super(message);
    }

    public NotCaptchaException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotCaptchaException(Throwable cause) {
        super(cause);
    }
}
