package com.linglong.lottery_backend.ticket.platform.al;

/**
 *
 */
public enum AoliInterfaceEnum {
    SPORT_GAME_DO_BET("1002"), TICKET_NOTIFY("3003"), TICKET_STATUS_QUERY("1003");

    public static final String GJGONGCAI_SPF = "1";
    public static final String GJGONGCAI_RQSPF = "2";
    public static final String GJGONGCAI_BF = "3";
    public static final String GJGONGCAI_JQS = "4";
    public static final String GJGONGCAI_BQC = "5";
    public static final String GJGONGCAI_ZQHH = "6";
    public static final String GJGONGCAI_DXF = "3";
    public static final String GJGONGCAI_SF = "1";
    public static final String GJGONGCAI_RFSF = "2";
    public static final String GJGONGCAI_SFC = "4";
    public static final String GJGONGCAI_LQHH = "5";
    public static final String BJGONGCAI_JCZQ_GID = "300";
    public static final String BJGONGCAI_JCLQ_GID = "301";

    public static final String DO_BET_RECEIVE_SUCCESS_CODE = "0000";
    public static final String DO_BET_TICKET_REPEAT_COMMIT = "0006";

    public static final int TICKET_STATUS_QUERY_SUCCESS_CODE = 1;

    private String cmd;

    AoliInterfaceEnum(String cmd) {
        this.cmd = cmd;
    }

}
