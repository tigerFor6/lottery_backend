package com.linglong.lottery_backend.ticket.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by ynght on 2019-04-26
 */
@Data
@NoArgsConstructor
public class BetResultWrap {
    String ioType;
    public static final String SYNC_IO_TYPE = "SYNC_IO_TYPE";
    public static final String ASYNC_IO_TYPE = "ASYNC_IO_TYPE";

    Boolean isSuccess;

    public BetResultWrap(String ioType, Boolean isSuccess) {
        this.ioType = ioType;
        this.isSuccess = isSuccess;
    }
}
