package com.linglong.lottery_backend.ticket.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ynght on 2019-04-20
 */
public class WordConstant {
    public static final String SINGLE = "SINGLE";
    public static final String MULTIPLE = "MULTIPLE";
    public static final String DANTUO = "DANTUO";
    public static final String DIRECT = "DIRECT";

    public final static Map<String, String> PLAY_MAP = new HashMap<String, String>();

    static {
        PLAY_MAP.put(WordConstant.SINGLE, "单式玩法");
        PLAY_MAP.put(WordConstant.MULTIPLE, "复式玩法");
        PLAY_MAP.put(WordConstant.DANTUO, "胆拖玩法");
        PLAY_MAP.put(WordConstant.DIRECT, "直选玩法");
    }
}
