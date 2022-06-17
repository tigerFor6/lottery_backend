package com.linglong.lottery_backend.ticket.platform.al;

import com.google.common.collect.Maps;
import com.linglong.lottery_backend.common.error.BusinessException;
import com.linglong.lottery_backend.ticket.enums.GameEnum;
import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.platform.PlatformUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * hua zeng guang
 *
 */
public enum AoliGameEnum {
    JCZQ_MIX_P(GameEnum.JCZQ_MIX_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getAoliJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCZQ_RQSPF_P(GameEnum.JCZQ_RQSPF_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getAoliJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCZQ_RQSPF_S(GameEnum.JCZQ_RQSPF_S.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getAoliJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCZQ_SPF_P(GameEnum.JCZQ_SPF_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getAoliJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCZQ_SPF_S(GameEnum.JCZQ_SPF_S.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getAoliJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCZQ_BF_P(GameEnum.JCZQ_BF_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getAoliJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCZQ_BF_S(GameEnum.JCZQ_BF_S.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getAoliJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCZQ_BQC_P(GameEnum.JCZQ_BQC_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getAoliJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCZQ_BQC_S(GameEnum.JCZQ_BQC_S.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getAoliJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCZQ_ZJQ_P(GameEnum.JCZQ_ZJQ_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getAoliJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCZQ_ZJQ_S(GameEnum.JCZQ_ZJQ_S.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getAoliJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },

    JCLQ_MIX_P(GameEnum.JCLQ_MIX_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getAoliJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCLQ_RFSF_P(GameEnum.JCLQ_RFSF_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getAoliJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCLQ_RFSF_S(GameEnum.JCLQ_RFSF_S.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getAoliJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCLQ_SF_P(GameEnum.JCLQ_SF_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getAoliJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCLQ_SF_S(GameEnum.JCLQ_SF_S.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getAoliJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCLQ_DXF_P(GameEnum.JCLQ_DXF_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getAoliJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCLQ_DXF_S(GameEnum.JCLQ_DXF_S.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getAoliJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCLQ_SFC_P(GameEnum.JCLQ_SFC_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getAoliJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCLQ_SFC_S(GameEnum.JCLQ_SFC_S.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getAoliJcCommonExtraBetCode(getGameEn(), ticket);
        }
    };

    public final static Map<String, Map<String, String>> CHAR_EXTRA_MAPPING_BET_CODE_BASKETBALL_MAP = new HashMap<>();
    public final static Map<String, String> SF_BET_CODE_MAP = Maps.newHashMap();
    public final static Map<String, String> RFSF_BET_CODE_MAP = Maps.newHashMap();
    public final static Map<String, String> DXF_BET_CODE_MAP = Maps.newHashMap();
    public final static Map<String, String> PAY_TYPE_BASKETBALL_CODE_MAP = Maps.newHashMap();

    public final static Map<String, Map<String, String>> CHAR_EXTRA_MAPPING_BET_CODE_FOOTBALL_MAP = new HashMap<>();
    public final static Map<String, String> SPF_BET_CODE_MAP = Maps.newHashMap();
    public final static Map<String, String> RQSPF_BET_CODE_MAP = Maps.newHashMap();
    public final static Map<String, String> JQS_BET_CODE_MAP = Maps.newHashMap();
    public final static Map<String, String> BQC_BET_CODE_MAP = Maps.newHashMap();
    public final static Map<String, String> BIFEN_BET_CODE_MAP = Maps.newHashMap();

    public final static Map<String, String> SHX115_PLAY_TYPE_CODE_MAP = Maps.newHashMap();
    public final static Map<String, String> SHX115_BET_TYPE_CODE_MAP = Maps.newHashMap();
    static {
        PAY_TYPE_BASKETBALL_CODE_MAP.put("SF","1");
        PAY_TYPE_BASKETBALL_CODE_MAP.put("RFSF","2");
        PAY_TYPE_BASKETBALL_CODE_MAP.put("DXF","4");

        SF_BET_CODE_MAP.put("1", "1");
        SF_BET_CODE_MAP.put("2", "2");
        CHAR_EXTRA_MAPPING_BET_CODE_BASKETBALL_MAP.put("sf", SF_BET_CODE_MAP);

        RFSF_BET_CODE_MAP.put("1", "1");
        RFSF_BET_CODE_MAP.put("2", "2");
        CHAR_EXTRA_MAPPING_BET_CODE_BASKETBALL_MAP.put("rfsf", RFSF_BET_CODE_MAP);

        DXF_BET_CODE_MAP.put("2", "2");
        DXF_BET_CODE_MAP.put("1", "1");
        CHAR_EXTRA_MAPPING_BET_CODE_BASKETBALL_MAP.put("dxf", DXF_BET_CODE_MAP);

        SPF_BET_CODE_MAP.put("3", "WIN");
        SPF_BET_CODE_MAP.put("1", "DRAW");
        SPF_BET_CODE_MAP.put("0", "LOSE");
        CHAR_EXTRA_MAPPING_BET_CODE_FOOTBALL_MAP.put("spf", SPF_BET_CODE_MAP);
        RQSPF_BET_CODE_MAP.put("3", "RQWIN");
        RQSPF_BET_CODE_MAP.put("1", "RQDRAW");
        RQSPF_BET_CODE_MAP.put("0", "RQLOSE");
        CHAR_EXTRA_MAPPING_BET_CODE_FOOTBALL_MAP.put("rqspf", RQSPF_BET_CODE_MAP);
        JQS_BET_CODE_MAP.put("0", "S0");
        JQS_BET_CODE_MAP.put("1", "S1");
        JQS_BET_CODE_MAP.put("2", "S2");
        JQS_BET_CODE_MAP.put("3", "S3");
        JQS_BET_CODE_MAP.put("4", "S4");
        JQS_BET_CODE_MAP.put("5", "S5");
        JQS_BET_CODE_MAP.put("6", "S6");
        JQS_BET_CODE_MAP.put("7", "S7");
        CHAR_EXTRA_MAPPING_BET_CODE_FOOTBALL_MAP.put("jqs", JQS_BET_CODE_MAP);
        BIFEN_BET_CODE_MAP.put("10", "WIN10");
        BIFEN_BET_CODE_MAP.put("20", "WIN20");
        BIFEN_BET_CODE_MAP.put("21", "WIN21");
        BIFEN_BET_CODE_MAP.put("30", "WIN30");
        BIFEN_BET_CODE_MAP.put("31", "WIN31");
        BIFEN_BET_CODE_MAP.put("32", "WIN32");
        BIFEN_BET_CODE_MAP.put("40", "WIN40");
        BIFEN_BET_CODE_MAP.put("41", "WIN41");
        BIFEN_BET_CODE_MAP.put("42", "WIN42");
        BIFEN_BET_CODE_MAP.put("50", "WIN50");
        BIFEN_BET_CODE_MAP.put("51", "WIN51");
        BIFEN_BET_CODE_MAP.put("52", "WIN52");
        BIFEN_BET_CODE_MAP.put("90", "WIN_OTHER");
        BIFEN_BET_CODE_MAP.put("00", "DRAW00");
        BIFEN_BET_CODE_MAP.put("11", "DRAW11");
        BIFEN_BET_CODE_MAP.put("22", "DRAW22");
        BIFEN_BET_CODE_MAP.put("33", "DRAW33");
        BIFEN_BET_CODE_MAP.put("99", "DRAW_OTHER");
        BIFEN_BET_CODE_MAP.put("01", "LOSE01");
        BIFEN_BET_CODE_MAP.put("02", "LOSE02");
        BIFEN_BET_CODE_MAP.put("12", "LOSE12");
        BIFEN_BET_CODE_MAP.put("03", "LOSE03");
        BIFEN_BET_CODE_MAP.put("13", "LOSE13");
        BIFEN_BET_CODE_MAP.put("23", "LOSE23");
        BIFEN_BET_CODE_MAP.put("04", "LOSE04");
        BIFEN_BET_CODE_MAP.put("14", "LOSE14");
        BIFEN_BET_CODE_MAP.put("24", "LOSE24");
        BIFEN_BET_CODE_MAP.put("05", "LOSE05");
        BIFEN_BET_CODE_MAP.put("15", "LOSE15");
        BIFEN_BET_CODE_MAP.put("25", "LOSE25");
        BIFEN_BET_CODE_MAP.put("09", "LOSE_OTHER");
        CHAR_EXTRA_MAPPING_BET_CODE_FOOTBALL_MAP.put("bifen", BIFEN_BET_CODE_MAP);
        BQC_BET_CODE_MAP.put("33", "WIN_WIN");
        BQC_BET_CODE_MAP.put("31", "WIN_DRAW");
        BQC_BET_CODE_MAP.put("30", "WIN_LOSE");
        BQC_BET_CODE_MAP.put("13", "DRAW_WIN");
        BQC_BET_CODE_MAP.put("11", "DRAW_DRAW");
        BQC_BET_CODE_MAP.put("10", "DRAW_LOSE");
        BQC_BET_CODE_MAP.put("03", "LOSE_WIN");
        BQC_BET_CODE_MAP.put("01", "LOSE_DRAW");
        BQC_BET_CODE_MAP.put("00", "LOSE_LOSE");
        //CHAR_EXTRA_MAPPING_BET_CODE_FOOTBALL_MAP.put("bifen", BQC_BET_CODE_MAP);
        CHAR_EXTRA_MAPPING_BET_CODE_FOOTBALL_MAP.put("bqc", BQC_BET_CODE_MAP);

        SHX115_PLAY_TYPE_CODE_MAP.put("1-1","TopOne");
        SHX115_PLAY_TYPE_CODE_MAP.put("2-1","TopTwoDirect");
        SHX115_PLAY_TYPE_CODE_MAP.put("3-1","TopThreeDirect");
        SHX115_PLAY_TYPE_CODE_MAP.put("2-2","TopTwoGroup");
        SHX115_PLAY_TYPE_CODE_MAP.put("3-2","TopThreeGroup");
        SHX115_PLAY_TYPE_CODE_MAP.put("2-3","EitherTwo");
        SHX115_PLAY_TYPE_CODE_MAP.put("3-3","EitherThree");
        SHX115_PLAY_TYPE_CODE_MAP.put("4-3","EitherFour");
        SHX115_PLAY_TYPE_CODE_MAP.put("5-3","EitherFive");
        SHX115_PLAY_TYPE_CODE_MAP.put("6-3","EitherSix");
        SHX115_PLAY_TYPE_CODE_MAP.put("7-3","EitherSenven");
        SHX115_PLAY_TYPE_CODE_MAP.put("8-3","EitherEight");

        SHX115_BET_TYPE_CODE_MAP.put("SINGLE","Single");
        SHX115_BET_TYPE_CODE_MAP.put("MULTIPLE","Double");
        SHX115_BET_TYPE_CODE_MAP.put("DANTUO","Towed");
        SHX115_BET_TYPE_CODE_MAP.put("DIRECT","Direct");
    }

    public final static Map<String, Map<String, String>> CHAR_BET_EXTRA_BB_FB_MAP = new HashMap<>();
    public final static Map<String, String> CHAR_BET_EXTRA_FB_MAP = new HashMap<>();
    public final static Map<String, String> CHAR_BET_EXTRA_BB_MAP = new HashMap<>();

    public final static Map<Integer, String> LOTTERY_CODE_MAP = new HashMap<>();
    public final static Map<String, String> DOUBLE_COLOR_PLAY_TYPE = new HashMap<>();

    public final static Map<String, String> DLT_PLAY_TYPE = new HashMap<>();
    public final static Map<String, String> DLT_PLAY_TYPE_CODE_MAP = new HashMap<>();
    public final static Map<String, String> DLT_RESULT_BONUS_MAP = new HashMap<>();

    public final static Map<String, String> PL3_PLAY_TYPE = new HashMap<>();

    static {

        CHAR_BET_EXTRA_FB_MAP.put("spf", AoliCaiInterfaceEnum.AOLI_SPF);
        CHAR_BET_EXTRA_FB_MAP.put("rqspf", AoliCaiInterfaceEnum.AOLI_RQSPF);
        CHAR_BET_EXTRA_FB_MAP.put("bifen", AoliCaiInterfaceEnum.AOLI_BF);
        CHAR_BET_EXTRA_FB_MAP.put("jqs", AoliCaiInterfaceEnum.AOLI_JQS);
        CHAR_BET_EXTRA_FB_MAP.put("bqc", AoliCaiInterfaceEnum.AOLI_BQC);

        CHAR_BET_EXTRA_BB_MAP.put("dxf", AoliCaiInterfaceEnum.AOLI_DXF);
        CHAR_BET_EXTRA_BB_MAP.put("rfsf", AoliCaiInterfaceEnum.AOLI_RFSF);
        CHAR_BET_EXTRA_BB_MAP.put("sf", AoliCaiInterfaceEnum.AOLI_SF);
        CHAR_BET_EXTRA_BB_MAP.put("sfc", AoliCaiInterfaceEnum.AOLI_SFC);

        CHAR_BET_EXTRA_BB_FB_MAP.put("1", CHAR_BET_EXTRA_FB_MAP);
        CHAR_BET_EXTRA_BB_FB_MAP.put("2", CHAR_BET_EXTRA_FB_MAP);

        LOTTERY_CODE_MAP.put(1, "JCZQ");
        LOTTERY_CODE_MAP.put(2, "JCLQ");
        LOTTERY_CODE_MAP.put(3, "SSQ");
        LOTTERY_CODE_MAP.put(4, "SHX115");
        LOTTERY_CODE_MAP.put(5, "DLT");
        LOTTERY_CODE_MAP.put(6, "PL3");
        LOTTERY_CODE_MAP.put(7, "PL5");

        DOUBLE_COLOR_PLAY_TYPE.put("SINGLE","Single");
        DOUBLE_COLOR_PLAY_TYPE.put("MULTIPLE","Double");
        DOUBLE_COLOR_PLAY_TYPE.put("DANTUO","Dantuo");

        DLT_PLAY_TYPE.put("SINGLE","NORMAL");
        DLT_PLAY_TYPE.put("MULTIPLE","DOUBLE");
        DLT_PLAY_TYPE.put("DANTUO","DANTUO");
        DLT_PLAY_TYPE_CODE_MAP.put("0","General");
        DLT_PLAY_TYPE_CODE_MAP.put("1","Additional");

        DLT_RESULT_BONUS_MAP.put("5+2","10000");
        DLT_RESULT_BONUS_MAP.put("5+1","10000");
        DLT_RESULT_BONUS_MAP.put("5+0","10000");
        DLT_RESULT_BONUS_MAP.put("4+2","3000");
        DLT_RESULT_BONUS_MAP.put("4+1","300");
        DLT_RESULT_BONUS_MAP.put("3+2","200");
        DLT_RESULT_BONUS_MAP.put("4+0","100");
        DLT_RESULT_BONUS_MAP.put("3+1","15");
        DLT_RESULT_BONUS_MAP.put("2+2","15");
        DLT_RESULT_BONUS_MAP.put("3+0","5");
        DLT_RESULT_BONUS_MAP.put("1+2","5");
        DLT_RESULT_BONUS_MAP.put("2+1","5");
        DLT_RESULT_BONUS_MAP.put("0+2","5");

        PL3_PLAY_TYPE.put("6-1", "P3Direct");
        PL3_PLAY_TYPE.put("6-2", "Group3");
        PL3_PLAY_TYPE.put("6-3", "Group6");
    }

    private String gameEn;

    public String getGameEn() {
        return gameEn;
    }

    AoliGameEnum(String gameEn) {
        this.gameEn = gameEn;
    }

    public String formBetCode(TicketDto tickets) {
        throw new BusinessException("not implements formBetCode");
    }
//
//    public String getGid() {
//        //竞彩只分为竞彩足球和竞彩篮球
//        if (gameEn.startsWith("jclq")) {
//            return AoliCaiInterfaceEnum.BJGONGCAI_JCLQ_GID;
//        } else if (gameEn.startsWith("jczq")) {
//            return AoliCaiInterfaceEnum.BJGONGCAI_JCZQ_GID;
//        }
//        throw new BusinessException("not implements this method. getGid()");
//    }

    public String getPlay() {
        if (gameEn.startsWith("jclq")) {
            return "5";
        } else if (gameEn.startsWith("jczq")) {
            return "6";
        }
        throw new BusinessException("not implements this method. getGid()");
    }

    public static AoliGameEnum getByGameEn(String gameEn) {
        for (AoliGameEnum entity : AoliGameEnum.values()) {
            if (entity.getGameEn().equals(gameEn)) {
                return entity;
            }
        }
        throw new BusinessException("gameEn is not correct, value provided for sdMyHome is : [" + gameEn + "]");
    }

    /**
     * aoli封装请求数据
     * @param interfaceName
     * @param data
     * @return
     */
    public static String createReqBody(String interfaceName, String data) {
        StringBuffer reqBody = new StringBuffer("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webService.only.com/\">");
        reqBody.append("<soapenv:Header/>");
        reqBody.append("<soapenv:Body>");
        reqBody.append("<web:"+interfaceName+">");
        reqBody.append("<data>"+ data +"</data>");
        reqBody.append("</web:"+interfaceName+">");
        reqBody.append("</soapenv:Body>");
        reqBody.append("</soapenv:Envelope>");
        return reqBody.toString();
    }
}
