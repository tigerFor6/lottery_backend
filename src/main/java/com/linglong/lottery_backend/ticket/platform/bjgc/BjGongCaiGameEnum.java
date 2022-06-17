package com.linglong.lottery_backend.ticket.platform.bjgc;

import com.google.common.collect.Maps;
import com.linglong.lottery_backend.common.error.BusinessException;
import com.linglong.lottery_backend.ticket.enums.GameEnum;
import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.platform.PlatformUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ynght on 2019-04-26
 */
public enum BjGongCaiGameEnum {
    JCZQ_MIX_P(GameEnum.JCZQ_MIX_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCZQ_RQSPF_P(GameEnum.JCZQ_RQSPF_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCZQ_RQSPF_S(GameEnum.JCZQ_RQSPF_S.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCZQ_SPF_P(GameEnum.JCZQ_SPF_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCZQ_SPF_S(GameEnum.JCZQ_SPF_S.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCZQ_BF_P(GameEnum.JCZQ_BF_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCZQ_BF_S(GameEnum.JCZQ_BF_S.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCZQ_BQC_P(GameEnum.JCZQ_BQC_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCZQ_BQC_S(GameEnum.JCZQ_BQC_S.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCZQ_ZJQ_P(GameEnum.JCZQ_ZJQ_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCZQ_ZJQ_S(GameEnum.JCZQ_ZJQ_S.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },

    JCLQ_MIX_P(GameEnum.JCLQ_MIX_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCLQ_RFSF_P(GameEnum.JCLQ_RFSF_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCLQ_RFSF_S(GameEnum.JCLQ_RFSF_S.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCLQ_SF_P(GameEnum.JCLQ_SF_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCLQ_SF_S(GameEnum.JCLQ_SF_S.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCLQ_DXF_P(GameEnum.JCLQ_DXF_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCLQ_DXF_S(GameEnum.JCLQ_DXF_S.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCLQ_SFC_P(GameEnum.JCLQ_SFC_P.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    JCLQ_SFC_S(GameEnum.JCLQ_SFC_S.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    DLT(GameEnum.DLT.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    PL3(GameEnum.PL3.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    },
    PL5(GameEnum.PL5.getGameEn()) {
        @Override
        public String formBetCode(TicketDto ticket) {
            return PlatformUtil.getBjGongCaiJcCommonExtraBetCode(getGameEn(), ticket);
        }
    };

    public final static Map<String, Map<String, String>> CHAR_EXTRA_MAPPING_BET_CODE_BASKETBALL_MAP = new HashMap<>();
    public final static Map<String, String> SF_BET_CODE_MAP = Maps.newHashMap();
    public final static Map<String, String> RFSF_BET_CODE_MAP = Maps.newHashMap();
    public final static Map<String, String> DXF_BET_CODE_MAP = Maps.newHashMap();

    public final static Map<String, Map<String, String>> CHAR_EXTRA_MAPPING_BET_CODE_FOOTBALL_MAP = new HashMap<>();
    public final static Map<String, String> SPF_BET_CODE_MAP = Maps.newHashMap();
    public final static Map<String, String> RQSPF_BET_CODE_MAP = Maps.newHashMap();
    public final static Map<String, String> JQS_BET_CODE_MAP = Maps.newHashMap();
    public final static Map<String, String> BQC_BET_CODE_MAP = Maps.newHashMap();
    public final static Map<String, String> BIFEN_BET_CODE_MAP = Maps.newHashMap();

    static {
        SF_BET_CODE_MAP.put("1", "1");
        SF_BET_CODE_MAP.put("2", "2");
        RFSF_BET_CODE_MAP.put("1", "1");
        RFSF_BET_CODE_MAP.put("2", "2");
        DXF_BET_CODE_MAP.put("2", "2");
        DXF_BET_CODE_MAP.put("1", "1");
        CHAR_EXTRA_MAPPING_BET_CODE_BASKETBALL_MAP.put("sf", SF_BET_CODE_MAP);
        CHAR_EXTRA_MAPPING_BET_CODE_BASKETBALL_MAP.put("rfsf", RFSF_BET_CODE_MAP);
        CHAR_EXTRA_MAPPING_BET_CODE_BASKETBALL_MAP.put("dxf", DXF_BET_CODE_MAP);

        SPF_BET_CODE_MAP.put("3", "3");
        SPF_BET_CODE_MAP.put("1", "1");
        SPF_BET_CODE_MAP.put("0", "0");
        CHAR_EXTRA_MAPPING_BET_CODE_FOOTBALL_MAP.put("spf", SPF_BET_CODE_MAP);
        RQSPF_BET_CODE_MAP.put("3", "3");
        RQSPF_BET_CODE_MAP.put("1", "1");
        RQSPF_BET_CODE_MAP.put("0", "0");
        CHAR_EXTRA_MAPPING_BET_CODE_FOOTBALL_MAP.put("rqspf", RQSPF_BET_CODE_MAP);
        JQS_BET_CODE_MAP.put("0", "0");
        JQS_BET_CODE_MAP.put("1", "1");
        JQS_BET_CODE_MAP.put("2", "2");
        JQS_BET_CODE_MAP.put("3", "3");
        JQS_BET_CODE_MAP.put("4", "4");
        JQS_BET_CODE_MAP.put("5", "5");
        JQS_BET_CODE_MAP.put("6", "6");
        JQS_BET_CODE_MAP.put("7", "7");
        CHAR_EXTRA_MAPPING_BET_CODE_FOOTBALL_MAP.put("jqs", JQS_BET_CODE_MAP);
        BIFEN_BET_CODE_MAP.put("10", "10");
        BIFEN_BET_CODE_MAP.put("20", "20");
        BIFEN_BET_CODE_MAP.put("21", "21");
        BIFEN_BET_CODE_MAP.put("30", "30");
        BIFEN_BET_CODE_MAP.put("31", "31");
        BIFEN_BET_CODE_MAP.put("32", "32");
        BIFEN_BET_CODE_MAP.put("40", "40");
        BIFEN_BET_CODE_MAP.put("41", "41");
        BIFEN_BET_CODE_MAP.put("42", "42");
        BIFEN_BET_CODE_MAP.put("50", "50");
        BIFEN_BET_CODE_MAP.put("51", "51");
        BIFEN_BET_CODE_MAP.put("52", "52");
        BIFEN_BET_CODE_MAP.put("90", "90");
        BIFEN_BET_CODE_MAP.put("00", "00");
        BIFEN_BET_CODE_MAP.put("11", "11");
        BIFEN_BET_CODE_MAP.put("22", "22");
        BIFEN_BET_CODE_MAP.put("33", "33");
        BIFEN_BET_CODE_MAP.put("99", "99");
        BIFEN_BET_CODE_MAP.put("01", "01");
        BIFEN_BET_CODE_MAP.put("02", "02");
        BIFEN_BET_CODE_MAP.put("12", "12");
        BIFEN_BET_CODE_MAP.put("03", "03");
        BIFEN_BET_CODE_MAP.put("13", "13");
        BIFEN_BET_CODE_MAP.put("23", "23");
        BIFEN_BET_CODE_MAP.put("04", "04");
        BIFEN_BET_CODE_MAP.put("14", "14");
        BIFEN_BET_CODE_MAP.put("24", "24");
        BIFEN_BET_CODE_MAP.put("05", "05");
        BIFEN_BET_CODE_MAP.put("15", "15");
        BIFEN_BET_CODE_MAP.put("25", "25");
        BIFEN_BET_CODE_MAP.put("09", "09");
        CHAR_EXTRA_MAPPING_BET_CODE_FOOTBALL_MAP.put("bifen", BIFEN_BET_CODE_MAP);
        BQC_BET_CODE_MAP.put("33", "33");
        BQC_BET_CODE_MAP.put("31", "31");
        BQC_BET_CODE_MAP.put("30", "30");
        BQC_BET_CODE_MAP.put("13", "13");
        BQC_BET_CODE_MAP.put("11", "11");
        BQC_BET_CODE_MAP.put("10", "10");
        BQC_BET_CODE_MAP.put("03", "03");
        BQC_BET_CODE_MAP.put("01", "01");
        BQC_BET_CODE_MAP.put("00", "00");
        //CHAR_EXTRA_MAPPING_BET_CODE_FOOTBALL_MAP.put("bifen", BQC_BET_CODE_MAP);
        CHAR_EXTRA_MAPPING_BET_CODE_FOOTBALL_MAP.put("bqc", BQC_BET_CODE_MAP);
    }

    public final static Map<String, String> CHAR_BET_EXTRA_MAP = new HashMap<>();
    public final static Map<String, Map<String, String>> PLAY_CODE_MAP = new HashMap<>();
    public final static Map<String, String> PL3_PLAY_CODE_MAP = new HashMap<>();
    public final static Map<String, String> DLT_PLAY_CODE_MAP = new HashMap<>();

    static {
        CHAR_BET_EXTRA_MAP.put("spf", BjGongCaiInterfaceEnum.GJGONGCAI_SPF);
        CHAR_BET_EXTRA_MAP.put("rqspf", BjGongCaiInterfaceEnum.GJGONGCAI_RQSPF);
        CHAR_BET_EXTRA_MAP.put("bifen", BjGongCaiInterfaceEnum.GJGONGCAI_BF);
        CHAR_BET_EXTRA_MAP.put("jqs", BjGongCaiInterfaceEnum.GJGONGCAI_JQS);
        CHAR_BET_EXTRA_MAP.put("bqc", BjGongCaiInterfaceEnum.GJGONGCAI_BQC);

        CHAR_BET_EXTRA_MAP.put("dxf", BjGongCaiInterfaceEnum.GJGONGCAI_DXF);
        CHAR_BET_EXTRA_MAP.put("rfsf", BjGongCaiInterfaceEnum.GJGONGCAI_RFSF);
        CHAR_BET_EXTRA_MAP.put("sf", BjGongCaiInterfaceEnum.GJGONGCAI_SF);
        CHAR_BET_EXTRA_MAP.put("sfc", BjGongCaiInterfaceEnum.GJGONGCAI_SFC);

        DLT_PLAY_CODE_MAP.put("SINGLE0", "101");
        DLT_PLAY_CODE_MAP.put("MULTIPLE0", "102");
        DLT_PLAY_CODE_MAP.put("DANTUO0", "103");
        DLT_PLAY_CODE_MAP.put("SINGLE1", "201");
        DLT_PLAY_CODE_MAP.put("MULTIPLE1", "202");
        DLT_PLAY_CODE_MAP.put("DANTUO1", "203");

        PL3_PLAY_CODE_MAP.put("SINGLE6-1", "101");
        PL3_PLAY_CODE_MAP.put("MULTIPLE6-1", "102");
        PL3_PLAY_CODE_MAP.put("SINGLE6-2", "201");
        PL3_PLAY_CODE_MAP.put("MULTIPLE6-2", "202");
        PL3_PLAY_CODE_MAP.put("SINGLE6-3", "301");
        PL3_PLAY_CODE_MAP.put("MULTIPLE6-3", "302");
        PL3_PLAY_CODE_MAP.put("SINGLE7-1", "101");
        PL3_PLAY_CODE_MAP.put("MULTIPLE7-1", "102");

        PLAY_CODE_MAP.put("101", DLT_PLAY_CODE_MAP);
        PLAY_CODE_MAP.put("102", PL3_PLAY_CODE_MAP);
    }

    private String gameEn;

    public String getGameEn() {
        return gameEn;
    }

    BjGongCaiGameEnum(String gameEn) {
        this.gameEn = gameEn;
    }

    public String formBetCode(TicketDto tickets) {
        throw new BusinessException("not implements formBetCode");
    }

    public String getGid() {
        //竞彩只分为竞彩足球和竞彩篮球
        if (gameEn.startsWith("jclq")) {
            return BjGongCaiInterfaceEnum.BJGONGCAI_JCLQ_GID;
        } else if (gameEn.startsWith("jczq")) {
            return BjGongCaiInterfaceEnum.BJGONGCAI_JCZQ_GID;
        }else if (gameEn.equals("DLT")) {
            return BjGongCaiInterfaceEnum.BJGONGCAI_DLT_GID;
        }else if (gameEn.equals("PL3")) {
            return BjGongCaiInterfaceEnum.BJGONGCAI_PL3_GID;
        }else if (gameEn.equals("PL5")) {
            return BjGongCaiInterfaceEnum.BJGONGCAI_PL5_GID;
        }
        throw new BusinessException("not implements this method. getGid()");
    }

    public String getPlay() {
        if (gameEn.startsWith("jclq")) {
            return "5";
        } else if (gameEn.startsWith("jczq")) {
            return "6";
        }
        throw new BusinessException("not implements this method. getGid()");
    }

    public static BjGongCaiGameEnum getByGameEn(String gameEn) {
        for (BjGongCaiGameEnum entity : BjGongCaiGameEnum.values()) {
            if (entity.getGameEn().equals(gameEn)) {
                return entity;
            }
        }
        throw new BusinessException("gameEn is not correct, value provided for sdMyHome is : [" + gameEn + "]");
    }
}
