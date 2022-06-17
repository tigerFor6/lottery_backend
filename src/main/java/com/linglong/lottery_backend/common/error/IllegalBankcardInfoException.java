package com.linglong.lottery_backend.common.error;

/**
 * @Author: qihua.li
 * @since: 2019-03-31
 */
public class IllegalBankcardInfoException extends RuntimeException {
    public IllegalBankcardInfoException() {
        super();
    }

    public IllegalBankcardInfoException(String message) {
        super(message);
    }

    public IllegalBankcardInfoException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalBankcardInfoException(Throwable cause) {
        super(cause);
    }
}
