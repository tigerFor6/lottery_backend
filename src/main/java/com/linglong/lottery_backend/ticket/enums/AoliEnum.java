package com.linglong.lottery_backend.ticket.enums;

public enum AoliEnum {
    SUC_TICKET(2,"SUC_TICKET"),
    TICKET_CANCLE(-2,"TICKET_CANCLE"),
    SUC_ENTRUST(1,"SUC_ENTRUST"),
    ING_ENTRUST(0, "ING_ENTRUST"),

    JCZQ(1, "JCZQ"),
    JCLQ(2, "JCLQ"),
    SSQ(3, "SSQ"),
    SHX115(4, "SHX115"),
    DLT(5, "DLT"),
    PL3(6, "PL3"),
    PL5(7, "PL5"),

    ISSUE_EXIT_ERROR(-2, "ISSUE_EXIT_ERROR"),
    ISSUE_OPEN_ERROR(-2, "ISSUE_OPEN_ERROR"),
    SUCCESS(2, "SUCCESS"),

    TOP_ONE(13, "1-1"),
    TOP_TWO_DIRECT(130, "2-1"),
    TOP_THREE_DIRECT(1170, "3-1"),
    TOP_TWO_GROUP(65, "2-2"),
    TOP_THREE_GROUP(195, "3-2"),
    EITHER_TWO(6, "2-3"),
    EITHER_THREE(19, "3-3"),
    EITHER_FOUR(78, "4-3"),
    EITHER_FIVE(540, "5-3"),
    EITHER_SIX(90, "6-3"),
    EITHER_SENVEN(26, "7-3"),
    EITHER_EIGHT(9, "8-3"),

    P3DIRECT(1040, "6-1"),
    GROUP3(346, "6-2"),
    GROUP6(173, "6-3");


    private Integer code;
    private String status;

    AoliEnum(Integer code, String status) {
        this.code = code;
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public static Integer getCodeByStatus(String status) {
        switch (status){
            case "1-1" :
                return TOP_ONE.getCode();
            case "2-1" :
                return TOP_TWO_DIRECT.getCode();
            case "3-1" :
                return TOP_THREE_DIRECT.getCode();
            case "2-2" :
                return TOP_TWO_GROUP.getCode();
            case "3-2" :
                return TOP_THREE_GROUP.getCode();
            case "2-3" :
                return EITHER_TWO.getCode();
            case "3-3" :
                return EITHER_THREE.getCode();
            case "4-3" :
                return EITHER_FOUR.getCode();
            case "5-3" :
                return EITHER_FIVE.getCode();
            case "6-3" :
                return EITHER_SIX.getCode();
            case "7-3" :
                return EITHER_SENVEN.getCode();
            case "8-3" :
                return EITHER_EIGHT.getCode();
            default:
                return 0;
        }
    }
}
