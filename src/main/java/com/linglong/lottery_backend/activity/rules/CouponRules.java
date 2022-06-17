package com.linglong.lottery_backend.activity.rules;

import java.io.Serializable;

/**
 * 红包规则定义表
 */
public class CouponRules implements Serializable {

    //领取规则 10001:1,1 默认没个用户可以领1次，每次领1个
    public final static String draw001 = "10001";

    //使用规则 20001:600 满减使用6元
    public final static String use001 = "20001";

    //使用规则 20002:2,3,4 可以用彩种无彩种限制默认-1
    public final static String use002 = "20002";
    //使用规则 20003:2019-01-01 12:45:10,2019-01-02 12:45:10 使用时间控制
    public final static String use003 = "20003";
    //使用规则 20004:1,2 领取1天后生效 红包有效期 2 天
    public final static String use004 = "20004";
    //领取规则 30001:100,200 中奖金额大于等于100，小于200
    public final static String use005 = "30001";

    private String draw;

    private String use;

    public String getDraw() {
        return draw;
    }

    public void setDraw(String draw) {
        this.draw = draw;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }
}
