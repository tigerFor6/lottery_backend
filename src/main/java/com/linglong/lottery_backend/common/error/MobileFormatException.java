package com.linglong.lottery_backend.common.error;

/**
 * @Author: qihua.li
 * @since: 2019-03-27
 */
public class MobileFormatException extends RuntimeException {
    public MobileFormatException() {
        super();
    }

    public MobileFormatException(String message) {
        super(message);
    }

    public MobileFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public MobileFormatException(Throwable cause) {
        super(cause);
    }
}
