package com.linglong.lottery_backend.common.error;

/**
 * @Author: qihua.li
 * @since: 2019-03-20
 */
public class IllegalCaptchaException extends RuntimeException {
    public IllegalCaptchaException() {
        super();
    }

    public IllegalCaptchaException(String message) {
        super(message);
    }

    public IllegalCaptchaException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalCaptchaException(Throwable cause) {
        super(cause);
    }
}
