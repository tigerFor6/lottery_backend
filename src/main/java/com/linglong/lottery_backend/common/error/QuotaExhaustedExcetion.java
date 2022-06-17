package com.linglong.lottery_backend.common.error;

/**
 * @Author: qihua.li
 * @since: 2019-03-30
 */
public class QuotaExhaustedExcetion extends RuntimeException {
    public QuotaExhaustedExcetion() {
        super();
    }

    public QuotaExhaustedExcetion(String message) {
        super(message);
    }

    public QuotaExhaustedExcetion(String message, Throwable cause) {
        super(message, cause);
    }

    public QuotaExhaustedExcetion(Throwable cause) {
        super(cause);
    }
}
