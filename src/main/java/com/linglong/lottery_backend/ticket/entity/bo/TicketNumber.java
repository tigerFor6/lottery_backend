package com.linglong.lottery_backend.ticket.entity.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by ynght on 2019-04-20
 */
@Data
@NoArgsConstructor
public class TicketNumber implements Cloneable {
    private String lotteryNumber;
    private Integer betNumber;
    private String playType;
    private String extra;
    private Integer times;
    private int period;

    public TicketNumber(TicketNumber ln) {
        this.lotteryNumber = ln.getLotteryNumber();
        this.betNumber = ln.getBetNumber();
        this.playType = ln.getPlayType();
        this.extra = ln.getExtra();
        this.times = ln.getTimes();
        this.period = ln.getPeriod();
    }

    public Object clone() {
        Object o = null;
        try {
            o = super.clone();
        } catch (CloneNotSupportedException ce) {
        }
        return o;
    }

    public TicketNumber cloneNumber() {
        Object o;
        try {
            o = super.clone();
        } catch (CloneNotSupportedException ce) {
            o = new TicketNumber(this);
        }
        return (TicketNumber) o;
    }

    public TicketNumber(String lotteryNumber, Integer betNumber, String playType, String extra, Integer times,int period) {
        this.lotteryNumber = lotteryNumber;
        this.betNumber = betNumber;
        this.playType = playType;
        this.extra = extra;
        this.times = times;
        this.period = period;
    }
}
