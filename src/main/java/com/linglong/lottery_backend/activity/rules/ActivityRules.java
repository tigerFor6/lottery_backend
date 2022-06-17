package com.linglong.lottery_backend.activity.rules;

import java.io.Serializable;

/**
 * 活动规则定义表
 */
public class ActivityRules implements Serializable {

    //活动规则 10001:22718, 22718代表定义异常的比赛matchId
    public final static String draw001 = "10001";

    //活动规则 10002:2, 2代表2串1
    public final static String draw002 = "10002";
}
