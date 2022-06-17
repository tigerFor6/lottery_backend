package com.linglong.lottery_backend.ticket.platform.al;

/**
 *
 */
public enum AoliCaiInterfaceEnum {
    SPORT_GAME_DO_BET("1002"), TICKET_NOTIFY("3003"), TICKET_STATUS_QUERY("1003");

    public static final String AOLI_SPF = "1";
    public static final String AOLI_RQSPF = "2";
    public static final String AOLI_BF = "3";
    public static final String AOLI_JQS = "4";
    public static final String AOLI_BQC = "5";

    public static final String AOLI_DXF = "3";
    public static final String AOLI_SF = "1";
    public static final String AOLI_RFSF = "2";
    public static final String AOLI_SFC = "4";
    private String cmd;

    AoliCaiInterfaceEnum(String cmd) {
        this.cmd = cmd;
    }

    public String getCmd() {
        return this.cmd;
    }

}
