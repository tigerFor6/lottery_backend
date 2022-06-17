package com.linglong.lottery_backend.common.error;

/**
 * @Author: qihua.li
 * @since: 2019-03-29
 */
public class IllegalPhoneExcetion extends RuntimeException {
    public IllegalPhoneExcetion() {
        super();
    }

    public IllegalPhoneExcetion(String message) {
        super(message);
    }

    public IllegalPhoneExcetion(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalPhoneExcetion(Throwable cause) {
        super(cause);
    }
}
