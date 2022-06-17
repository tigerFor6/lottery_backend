package com.linglong.lottery_backend.ticket.platform.bjgc;

import com.linglong.lottery_backend.ticket.enums.PlatformEnum;

/**
 * Created by ynght on 2019-04-26
 */
public enum BjGongCaiInterfaceEnum {
    SPORT_GAME_DO_BET("1002"), TICKET_NOTIFY("3003"), TICKET_STATUS_QUERY("1003");
    public static final String BJ_GONG_CAI_REQUEST_XML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    public static final String BJ_GONG_CAI_REQUEST_PART1 = BJ_GONG_CAI_REQUEST_XML_HEAD + "<message  version=\"1" +
            ".0\"><header><sid>" + PlatformEnum.BJGONGCAI.getPlatform().getPlatformAccount() + "</sid><timetag>";
    public static final String BJ_GONG_CAI_REQUEST_PART2 = "</timetag><digest>";

    public static final String BJ_GONG_CAI_REQUEST_PART3 = "\" timestamp=\"\" memo=\"\"/><body>";
    public static final String BJ_GONG_CAI_REQUEST_PART4 = "</body></request>";
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
    public static final String BJGONGCAI_DLT_GID = "101";
    public static final String BJGONGCAI_PL3_GID = "102";
    public static final String BJGONGCAI_PL5_GID = "103";

    public static final String DO_BET_RECEIVE_SUCCESS_CODE = "0000";
    public static final String DO_BET_TICKET_REPEAT_COMMIT = "0006";

    public static final int TICKET_STATUS_QUERY_SUCCESS_CODE = 1;

    private String cmd;

    BjGongCaiInterfaceEnum(String cmd) {
        this.cmd = cmd;
    }

    public String getCmd() {
        return this.cmd;
    }

    public static String getXmlPre(Integer gameType, String time) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(BjGongCaiInterfaceEnum.BJ_GONG_CAI_REQUEST_PART1).append(time)
                .append(BjGongCaiInterfaceEnum.BJ_GONG_CAI_REQUEST_PART2);
        return buffer.toString();
    }
}
