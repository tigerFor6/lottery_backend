package com.linglong.lottery_backend.common.error;

public class CashierException extends RuntimeException {

    public CashierException() {
        super();
    }

    public CashierException(String message) {
        super(message);
    }

    public CashierException(String message, Throwable cause) {
        super(message, cause);
    }

    public CashierException(Throwable cause) {
        super(cause);
    }
}
