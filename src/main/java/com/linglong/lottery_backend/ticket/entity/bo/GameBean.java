package com.linglong.lottery_backend.ticket.entity.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * Created by ynght on 2019-04-20
 */
@Data
@NoArgsConstructor
public class GameBean implements Cloneable {
    private int betNumber;// 注数
    private String playType; // 方式 单式 复式 胆拖
    private String lotteryNumber; // 投注号码
    private BigDecimal price; // 价格
    private String extra; // 额外字段 玩法
    private BigDecimal betTimes = BigDecimal.ONE;
    private Date endTime;
    private int period;

    public GameBean(GameBean gb) {
        this.betNumber = gb.getBetNumber();
        this.playType = gb.getPlayType();
        this.lotteryNumber = gb.getLotteryNumber();
        this.price = gb.getPrice();
        this.extra = gb.getExtra();
        this.betTimes = gb.getBetTimes();
        this.period = gb.getPeriod();
    }

    public Object clone() {
        //克隆方法只克隆最简单的bean，不克隆那种未拆的bean
        Object o = null;
        try {
            o = super.clone();
        } catch (CloneNotSupportedException ce) {
        }
        return o;
    }

    public GameBean cloneGameBean() {
        Object o;
        try {
            o = super.clone();
        } catch (CloneNotSupportedException ce) {
            o = new GameBean(this);
        }
        return (GameBean) o;
    }

    public GameBean(String lotteryNumber, Integer betNumber, String playType, String extra) {
        this.lotteryNumber = lotteryNumber;
        this.betNumber = betNumber;
        this.playType = playType;
        this.extra = extra;
    }

    public GameBean(int betNumber, String playType, String lotteryNumber, BigDecimal price, String extra,  Date endTime,int period) {
        this.betNumber = betNumber;
        this.playType = playType;
        this.lotteryNumber = lotteryNumber;
        this.price = price;
        this.extra = extra;
        //this.betTimes = betTimes;
        this.endTime = endTime;
        this.period = period;
    }

    @Override
    public int hashCode() {
        return this.lotteryNumber.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GameBean)) {
            return false;
        }
        GameBean gb = (GameBean) o;
        if (Objects.equals(this.lotteryNumber, gb.getLotteryNumber()) && this.betTimes == gb.getBetTimes() && this
                .price.compareTo(gb.getPrice()) == 0) {
            return true;
        } else {
            return false;
        }
    }
}
