package com.linglong.lottery_backend.ticket.bean.sport.common;

import com.google.common.collect.Maps;
import com.linglong.lottery_backend.ticket.constant.GameConstant;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class RuleConstant {
    /**
     * 胆的值为1，拖的值为0
     */
    //竞彩
    public final static String FORMAT_RULE_ID_REGEXP = "\\d{4}\\d{2}\\d{2}\\d{3}";
    public final static int FORMAT_RULE_ID_LENGTH = 11;
    //单场
    public final static String FORMAT_RULE_ID_REGEXP_SINGLE = "\\d+";
    public final static int FORMAT_RULE_ID_LENGTH_SINGLE = 4;

    public final static String CHAR_FORMAT_DAN = "1";
    public final static String CHAR_FORMAT_TUO = "0";

    public final static String CHAR_FORMAT_WIN = "3";
    public final static String CHAR_FORMAT_TIE = "1";
    public final static String CHAR_FORMAT_LOSS = "0";

    public final static String CHAR_CANCEL = "*";
    public final static String CHAR_CANCEL_CN = "取消";

    /**
     * 一注的长度，目前系统为3，由赛事编号，投注信息，和胆拖信息组成
     */
    public final static int LENGTH_INNER_BET = 3;
    public final static int LENGTH_INNER_BET_NO_DAN = 2;

    /**
     * 至少要有1种过关方式
     */
    public final static int LENGTH_PASS_WAY = 1;

    public final static String[] GAME_ORDER =
            {GameConstant.JCZQ_SPF_P, GameConstant.JCZQ_SPF_S, GameConstant.JCZQ_RQSPF_P, GameConstant.JCZQ_RQSPF_S,
                    GameConstant.JCZQ_BF_P, GameConstant.JCZQ_ZJQ_P, GameConstant.JCZQ_ZJQ_S, GameConstant.JCZQ_BQC_P,
                    GameConstant.JCZQ_BQC_S, GameConstant.JCLQ_SF_P, GameConstant.JCLQ_SF_S, GameConstant.JCLQ_RFSF_P,
                    GameConstant.JCLQ_RFSF_S, GameConstant.JCLQ_SFC_P, GameConstant.JCLQ_DXF_P, GameConstant
                    .JCLQ_DXF_S};

    public static final int[][][] RULE_TABLE_BASKETBALL_VS_S =
            {
                    {//只允许单关
                            {1},
                            {1}}};

    public static final int[][][] RULE_TABLE_BASKETBALL_VS_P =
            {
                    //ex.3关可以有3_1,3_3,3_4;其中3_1可以由3_1组合,3_3可以由2_1组合,3_4可以由2_1和3_1组合
                    {//不允许单关
                            {1},
                            {1}},
                    {//2串
                            {1},
                            {2}},
                    {//3串
                            {1, 3, 4},
                            {3},
                            {2},
                            {2, 3}},
                    {//4串
                            {1, 4, 5, 6, 11},
                            {4},
                            {3},
                            {3, 4},
                            {2},
                            {2, 3, 4}},
                    {//5串
                            {1, 5, 6, 10, 16, 20, 26},
                            {5},
                            {4},
                            {4, 5},
                            {2},
                            {3, 4, 5},
                            {2, 3},
                            {2, 3, 4, 5}},
                    {//6串
                            {1, 6, 7, 15, 20, 22, 35, 42, 50, 57},
                            {6},
                            {5},
                            {5, 6},
                            {2},
                            {3},
                            {4, 5, 6},
                            {2, 3},
                            {3, 4, 5, 6},
                            {2, 3, 4},
                            {2, 3, 4, 5, 6}},
                    {//7串
                            {1, 7, 8, 21, 35, 120},
                            {7},
                            {6},
                            {6, 7},
                            {5},
                            {4},
                            {2, 3, 4, 5, 6, 7}},
                    {//8串
                            {1, 8, 9, 28, 56, 70, 247},
                            {8},
                            {7},
                            {7, 8},
                            {6},
                            {5},
                            {4},
                            {2, 3, 4, 5, 6, 7, 8}}};

    public static final int[][][] RULE_TABLE_BASKETBALL_LETVS_S =
            {
                    {//只允许单关
                            {1},
                            {1}}};

    public static final int[][][] RULE_TABLE_BASKETBALL_LETVS_P =
            {
                    //ex.3关可以有3_1,3_3,3_4;其中3_1可以由3_1组合,3_3可以由2_1组合,3_4可以由2_1和3_1组合
                    {//不允许单关
                            {1},
                            {1}},
                    {//2串
                            {1},
                            {2}},
                    {//3串
                            {1, 3, 4},
                            {3},
                            {2},
                            {2, 3}},
                    {//4串
                            {1, 4, 5, 6, 11},
                            {4},
                            {3},
                            {3, 4},
                            {2},
                            {2, 3, 4}},
                    {//5串
                            {1, 5, 6, 10, 16, 20, 26},
                            {5},
                            {4},
                            {4, 5},
                            {2},
                            {3, 4, 5},
                            {2, 3},
                            {2, 3, 4, 5}},
                    {//6串
                            {1, 6, 7, 15, 20, 22, 35, 42, 50, 57},
                            {6},
                            {5},
                            {5, 6},
                            {2},
                            {3},
                            {4, 5, 6},
                            {2, 3},
                            {3, 4, 5, 6},
                            {2, 3, 4},
                            {2, 3, 4, 5, 6}},
                    {//7串
                            {1, 7, 8, 21, 35, 120},
                            {7},
                            {6},
                            {6, 7},
                            {5},
                            {4},
                            {2, 3, 4, 5, 6, 7}},
                    {//8串
                            {1, 8, 9, 28, 56, 70, 247},
                            {8},
                            {7},
                            {7, 8},
                            {6},
                            {5},
                            {4},
                            {2, 3, 4, 5, 6, 7, 8}}};

    public static final int[][][] RULE_TABLE_BASKETBALL_BS_S =
            {
                    {//只允许单关
                            {1},
                            {1}}};

    public static final int[][][] RULE_TABLE_BASKETBALL_BS_P =
            {
                    //ex.3关可以有3_1,3_3,3_4;其中3_1可以由3_1组合,3_3可以由2_1组合,3_4可以由2_1和3_1组合
                    {//不允许单关
                            {1},
                            {1}},
                    {//2串
                            {1},
                            {2}},
                    {//3串
                            {1, 3, 4},
                            {3},
                            {2},
                            {2, 3}},
                    {//4串
                            {1, 4, 5, 6, 11},
                            {4},
                            {3},
                            {3, 4},
                            {2},
                            {2, 3, 4}},
                    {//5串
                            {1, 5, 6, 10, 16, 20, 26},
                            {5},
                            {4},
                            {4, 5},
                            {2},
                            {3, 4, 5},
                            {2, 3},
                            {2, 3, 4, 5}},
                    {//6串
                            {1, 6, 7, 15, 20, 22, 35, 42, 50, 57},
                            {6},
                            {5},
                            {5, 6},
                            {2},
                            {3},
                            {4, 5, 6},
                            {2, 3},
                            {3, 4, 5, 6},
                            {2, 3, 4},
                            {2, 3, 4, 5, 6}},
                    {//7串
                            {1, 7, 8, 21, 35, 120},
                            {7},
                            {6},
                            {6, 7},
                            {5},
                            {4},
                            {2, 3, 4, 5, 6, 7}},
                    {//8串
                            {1, 8, 9, 28, 56, 70, 247},
                            {8},
                            {7},
                            {7, 8},
                            {6},
                            {5},
                            {4},
                            {2, 3, 4, 5, 6, 7, 8}}};

    public static final int[][][] RULE_TABLE_BASKETBALL_DIFF_P =
            {
                    //ex.3关可以有3_1,3_3,3_4;其中3_1可以由3_1组合,3_3可以由2_1组合,3_4可以由2_1和3_1组合
                    {//允许单关
                            {1},
                            {1}},
                    {//2串
                            {1},
                            {2}},
                    {//3串
                            {1, 3, 4},
                            {3},
                            {2},
                            {2, 3}},
                    {//4串
                            {1, 4, 5, 6, 11},
                            {4},
                            {3},
                            {3, 4},
                            {2},
                            {2, 3, 4}}};

    public static final int[][][] RULE_TABLE_BASKETBALL_DIFF_S =
            {
                    {//只允许单关
                            {1},
                            {1}}};

    /*****************************************************************************/

    public final static int[][][] RULE_TABLE_FOOTBALL_SPF_S =
            {
                    {//只允许单关
                            {1},
                            {1}}};

    public final static int[][][] RULE_TABLE_FOOTBALL_SPF_P =
            {
                    //ex.3关可以有3_1,3_3,3_4;其中3_1可以由3_1组合,3_3可以由2_1组合,3_4可以由2_1和3_1组合
                    {//TODO in the future, it will allow single fix bonus mode. edited by mwlv 20140922 JcSingleFix
                            {1},
                            {1}},
                    {//2串
                            {1},
                            {2}},
                    {//3串
                            {1, 3, 4},
                            {3},
                            {2},
                            {2, 3}},
                    {//4串
                            {1, 4, 5, 6, 11},
                            {4},
                            {3},
                            {3, 4},
                            {2},
                            {2, 3, 4}},
                    {//5串
                            {1, 5, 6, 10, 16, 20, 26},
                            {5},
                            {4},
                            {4, 5},
                            {2},
                            {3, 4, 5},
                            {2, 3},
                            {2, 3, 4, 5}},
                    {//6串
                            {1, 6, 7, 15, 20, 22, 35, 42, 50, 57},
                            {6},
                            {5},
                            {5, 6},
                            {2},
                            {3},
                            {4, 5, 6},
                            {2, 3},
                            {3, 4, 5, 6},
                            {2, 3, 4},
                            {2, 3, 4, 5, 6}},
                    {//7串
                            {1, 7, 8, 21, 35, 120},
                            {7},
                            {6},
                            {6, 7},
                            {5},
                            {4},
                            {2, 3, 4, 5, 6, 7}},
                    {//8串
                            {1, 8, 9, 28, 56, 70, 247},
                            {8},
                            {7},
                            {7, 8},
                            {6},
                            {5},
                            {4},
                            {2, 3, 4, 5, 6, 7, 8}}};

    public final static int[][][] RULE_TABLE_FOOTBALL_GOAL_S =
            {
                    {//只允许单关
                            {1},
                            {1}}};

    public final static int[][][] RULE_TABLE_FOOTBALL_GOAL_P =
            {
                    //ex.3关可以有3_1,3_3,3_4;其中3_1可以由3_1组合,3_3可以由2_1组合,3_4可以由2_1和3_1组合

                    {//TODO in the future, it will allow single fix bonus mode. edited by mwlv 20140922 JcSingleFix
                            {1},
                            {1}},
                    {//2串
                            {1},
                            {2}},
                    {//3串
                            {1, 3, 4},
                            {3},
                            {2},
                            {2, 3}},
                    {//4串
                            {1, 4, 5, 6, 11},
                            {4},
                            {3},
                            {3, 4},
                            {2},
                            {2, 3, 4}},
                    {//5串
                            {1, 5, 6, 10, 16, 20, 26},
                            {5},
                            {4},
                            {4, 5},
                            {2},
                            {3, 4, 5},
                            {2, 3},
                            {2, 3, 4, 5}},
                    {//6串
                            {1, 6, 7, 15, 20, 22, 35, 42, 50, 57},
                            {6},
                            {5},
                            {5, 6},
                            {2},
                            {3},
                            {4, 5, 6},
                            {2, 3},
                            {3, 4, 5, 6},
                            {2, 3, 4},
                            {2, 3, 4, 5, 6}}};

    public final static int[][][] RULE_TABLE_FOOTBALL_SCORE_P =
            {
                    //ex.3关可以有3_1,3_3,3_4;其中3_1可以由3_1组合,3_3可以由2_1组合,3_4可以由2_1和3_1组合
                    {//允许单关
                            {1},
                            {1}},
                    {//2串
                            {1},
                            {2}},
                    {//3串
                            {1, 3, 4},
                            {3},
                            {2},
                            {2, 3}},
                    {//4串
                            {1, 4, 5, 6, 11},
                            {4},
                            {3},
                            {3, 4},
                            {2},
                            {2, 3, 4}}};

    public final static int[][][] RULE_TABLE_FOOTBALL_HALF_P =
            {
                    //ex.3关可以有3_1,3_3,3_4;其中3_1可以由3_1组合,3_3可以由2_1组合,3_4可以由2_1和3_1组合
                    {
                            {1},
                            {1}},
                    {//2串
                            {1},
                            {2}},
                    {//3串
                            {1, 3, 4},
                            {3},
                            {2},
                            {2, 3}},
                    {//4串
                            {1, 4, 5, 6, 11},
                            {4},
                            {3},
                            {3, 4},
                            {2},
                            {2, 3, 4}}};

    public final static int[][][] RULE_TABLE_FOOTBALL_HALF_S =
            {
                    {//只允许单关
                            {1},
                            {1}}};
    /**
     * 让球
     */
    //单场上下单双玩法，半全场胜平负玩法，单场总进球数玩法
    public final static int[][][] RULE_TABLE_DANCHANG_FOOTBALL_COMMON =
            {
                    //ex.3关可以有3_1,3_4,3_7;其中3_1可以由3_1组合,3_4可以由2_1和3_1组合,3_7可以由单关、2_1和3_1组合
                    {//单关
                            {1},
                            {1}},
                    {//2串
                            {1, 3},
                            {2},
                            {1, 2}},
                    {//3串
                            {1, 4, 7},
                            {3},
                            {2, 3},
                            {1, 2, 3}},
                    {//4串
                            {1, 5, 11, 15},
                            {4},
                            {3, 4},
                            {2, 3, 4},
                            {1, 2, 3, 4}},
                    {//5串
                            {1, 6, 16, 26, 31},
                            {5},
                            {4, 5},
                            {3, 4, 5},
                            {2, 3, 4, 5},
                            {1, 2, 3, 4, 5}},
                    {//6串
                            {1, 7, 22, 42, 57, 63},
                            {6},
                            {5, 6},
                            {4, 5, 6},
                            {3, 4, 5, 6},
                            {2, 3, 4, 5, 6},
                            {1, 2, 3, 4, 5, 6}}};

    //单场比分玩法
    public final static int[][][] RULE_TABLE_DANCHANG_FOOTBALL_BF =
            {
                    //ex.3关可以有3_1,3_4,3_7;其中3_1可以由3_1组合,3_4可以由2_1和3_1组合,3_7可以由单关、2_1和3_1组合
                    {//单关
                            {1},
                            {1}},
                    {//2串
                            {1, 3},
                            {2},
                            {1, 2}},
                    {//3串
                            {1, 4, 7},
                            {3},
                            {2, 3},
                            {1, 2, 3}}};

    //让球胜平负玩法
    public final static int[][][] RULE_TABLE_DANCHANG_FOOTBALL_SPF =
            {
                    //ex.3关可以有3_1,3_4,3_7;其中3_1可以由3_1组合,3_4可以由2_1和3_1组合,3_7可以由单关、2_1和3_1组合
                    {//单关
                            {1},
                            {1}},
                    {//2串
                            {1, 3},
                            {2},
                            {1, 2}},
                    {//3串
                            {1, 4, 7},
                            {3},
                            {2, 3},
                            {1, 2, 3}},
                    {//4串
                            {1, 5, 11, 15},
                            {4},
                            {3, 4},
                            {2, 3, 4},
                            {1, 2, 3, 4}},
                    {//5串
                            {1, 6, 16, 26, 31},
                            {5},
                            {4, 5},
                            {3, 4, 5},
                            {2, 3, 4, 5},
                            {1, 2, 3, 4, 5}},
                    {//6串
                            {1, 7, 22, 42, 57, 63},
                            {6},
                            {5, 6},
                            {4, 5, 6},
                            {3, 4, 5, 6},
                            {2, 3, 4, 5, 6},
                            {1, 2, 3, 4, 5, 6}},
                    {//7关
                            {1},
                            {7}},
                    {//8关
                            {1},
                            {8}},
                    {//9关
                            {1},
                            {9}},
                    {//10关
                            {1},
                            {10}},
                    {//11关
                            {1},
                            {11}},
                    {//12关
                            {1},
                            {12}},
                    {//13关
                            {1},
                            {13}},
                    {//14关
                            {1},
                            {14}},
                    {//15关
                            {1},
                            {15}},};

    //胜负过关玩法
    public final static int[][][] RULE_TABLE_DANCHANG_FOOTBALL_SFGG =
            {
                    //ex.3关可以有3_1,3_4,3_7;其中3_1可以由3_1组合,3_4可以由2_1和3_1组合,3_7可以由单关、2_1和3_1组合
                    {//不允许单关
                            {0},
                            {1}},
                    {//2串
                            {0},
                            {2}},
                    {//3串
                            {1},
                            {3}},
                    {//4串
                            {1, 5},
                            {4},
                            {3, 4}},
                    {//5串
                            {1, 6, 16},
                            {5},
                            {4, 5},
                            {3, 4, 5}},
                    {//6串
                            {1, 7, 22, 42},
                            {6},
                            {5, 6},
                            {4, 5, 6},
                            {3, 4, 5, 6}},
                    {//7关
                            {1},
                            {7}},
                    {//8关
                            {1},
                            {8}},
                    {//9关
                            {1},
                            {9}},
                    {//10关
                            {1},
                            {10}},
                    {//11关
                            {1},
                            {11}},
                    {//12关
                            {1},
                            {12}},
                    {//13关
                            {1},
                            {13}},
                    {//14关
                            {1},
                            {14}},
                    {//15关
                            {1},
                            {15}},};

    //注意：下面的彩果表示方法是网易的表示方法，和恒鹏不同，在恒鹏常量类中有转换！！！

    /**
     * 足球让球
     */
    public final static String CHAR_BET_PARAM_FOOTBALL_LETPOINT = "cb";
    /**
     * 篮球让分
     */
    public final static String CHAR_BET_PARAM_BASKETBALL_LETPOINT = "cp";
    /**
     * 预设总分 大小分
     */
    public final static String CHAR_BET_PARAM_BASKETBALL_BASEPOINT = "tp";
    public final static String CHAR_BET_PARAM_HISTORY_ADDITIONAL_PREFIX = "h";
    public final static int HISTORY_ADDITIONAL_SIZE = 15;

    public final static String CHAR_RESULT_MATCH_FOOTBALL_FINAL = "rf";
    public final static String CHAR_RESULT_MATCH_FOOTBALL_HALF = "rh";
    /**
     * 最终比分 result bid
     */
    public final static String CHAR_RESULT_MATCH_BASKETBALL_FINAL = "rb";
    public final static String CHAR_RESULT_MATCH_SINGLE_FOOTBALL = "rs";

    //预先是保存让球后的彩果，目前暂时没用，可视为足球的保留字段，下载彩果接口里直接忽略了此字段
    public final static String CHAR_RESULT_LOTT_FOOTBALL_VS_S = "z1";
    public final static String CHAR_RESULT_LOTT_FOOTBALL_GOAL_S = "z2";
    public final static String CHAR_RESULT_LOTT_FOOTBALL_SCORE_S = "z3";
    public final static String CHAR_RESULT_LOTT_FOOTBALL_HALF_S = "z4";
    public final static String CHAR_RESULT_LOTT_FOOTBALL_VS_P = "z5";
    public final static String CHAR_RESULT_LOTT_FOOTBALL_GOAL_P = "z6";
    public final static String CHAR_RESULT_LOTT_FOOTBALL_SCORE_P = "z7";
    public final static String CHAR_RESULT_LOTT_FOOTBALL_HALF_P = "z8";

    public final static String CHAR_RESULT_LOTT_FOOTBALL_BONUS_NVS = "b8";
    public final static String CHAR_RESULT_LOTT_FOOTBALL_BONUS_VS = "b0";
    public final static String CHAR_RESULT_LOTT_FOOTBALL_BONUS_GOAL = "b1";
    public final static String CHAR_RESULT_LOTT_FOOTBALL_BONUS_SCORE = "b2";
    public final static String CHAR_RESULT_LOTT_FOOTBALL_BONUS_HALF = "b3";

    //单场
    public final static String CHAR_RESULT_LOTT_DANCHANG_FOOTBALL_BONUS_VS = "d0";//让球胜平负玩法
    public final static String CHAR_RESULT_LOTT_DANCHANG_FOOTBALL_BONUS_GOAL = "d1";//单场总进球数玩法
    public final static String CHAR_RESULT_LOTT_DANCHANG_FOOTBALL_BONUS_SCORE = "d2";//单场比分玩法
    public final static String CHAR_RESULT_LOTT_DANCHANG_FOOTBALL_BONUS_HALF = "d3";//半全场胜平负玩法
    public final static String CHAR_RESULT_LOTT_DANCHANG_FOOTBALL_BONUS_ODD_EVEN = "d4";//单场上下单双玩法
    public final static String CHAR_RESULT_LOTT_DANCHANG_FOOTBALL_BONUS_SFGG = "d5";

    //预先是保存让分后的彩果，目前暂时没用，可视为篮球的保留字段，下载彩果接口里直接忽略了此字段
    public final static String CHAR_RESULT_LOTT_BASKETBALL_VS_S = "l1";
    public final static String CHAR_RESULT_LOTT_BASKETBALL_LETVS_S = "l2";
    public final static String CHAR_RESULT_LOTT_BASKETBALL_DIFF_S = "l3";
    public final static String CHAR_RESULT_LOTT_BASKETBALL_BS_S = "l4";
    public final static String CHAR_RESULT_LOTT_BASKETBALL_VS_P = "l5";
    public final static String CHAR_RESULT_LOTT_BASKETBALL_LETVS_P = "l6";
    public final static String CHAR_RESULT_LOTT_BASKETBALL_DIFF_P = "l7";
    public final static String CHAR_RESULT_LOTT_BASKETBALL_BS_P = "l8";

    public final static String CHAR_RESULT_LOTT_BASKETBALL_BONUS_VS = "b4";
    public final static String CHAR_RESULT_LOTT_BASKETBALL_BONUS_LETVS = "b5";
    public final static String CHAR_RESULT_LOTT_BASKETBALL_BONUS_DIFF = "b6";
    public final static String CHAR_RESULT_LOTT_BASKETBALL_BONUS_BS = "b7";

    //生成的查询scheme方案所需的参数
    public final static String CHAR_SCHEME_OPTION = "optionCn";
    public final static String CHAR_SCHEME_DAN = "dan";
    public final static String CHAR_SCHEME_VIEW = "view";
    public final static String CHAR_SCHEME_REALSP = "realSp";
    public final static String CHAR_SCHEME_BONUS = "forcast";
    //开奖，投注页面需要的比赛信息
    public final static String CHAR_JC_GUIDE = "jcGuide";
    public final static String CHAR_SCHEME_MATCH = "matchList";
    public final static String CHAR_SCHEME_GAME = "contestGame";
    public final static String CHAR_SCHEME_DATE = "matchDate";

    public final static String CHAR_KEY_LOTTRES_CN = "lottResCn";
    public final static String CHAR_KEY_LOTT_SP = "lottSp";

    public final static String[][] CHAR_BET_CODE_TUO =
            {
                    {"0", "拖"}};

    public final static String[][] CHAR_BET_CODE_DANTUO =
            {
                    {"0", "拖"},
                    {"1", "胆"}};

    public final static String[] SET_RESULT_MATCH_FOOTBALL =
            {CHAR_RESULT_MATCH_FOOTBALL_FINAL, CHAR_RESULT_MATCH_FOOTBALL_HALF};
    public final static String[] SET_RESULT_MATCH_BASKETBALL =
            {CHAR_RESULT_MATCH_BASKETBALL_FINAL};

    public final static String[][] CHAR_BET_CODE_FOOTBALL_VS =
            {
                    {"3", "主胜"},
                    {"1", "平"},
                    {"0", "主负"}};
    public final static String[][] CHAR_BET_CODE_FOOTBALL_RQ_VS =
            {
                    {"10003", "让球胜"},
                    {"10001", "让球平"},
                    {"10000", "让球负"}};

    public final static String[] CHAR_BET_OPTION_FOOTBALL_VS =
            {CHAR_BET_PARAM_FOOTBALL_LETPOINT, "让球"};

    public final static String[][] CHAR_BET_CODE_FOOTBALL_ODD_EVEN =
            {
                    {"11", "上+单"},
                    {"10", "上+双"},
                    {"01", "下+单"},
                    {"00", "下+双"}};

    public final static String[][] CHAR_BET_CODE_FOOTBALL_SFGG =
            {
                    {"3", "胜"},
                    {"0", "负"}};

    public final static Map<String, String> BET_CODE_SINGLE_FOOTBALL_SXDS_MAP = new HashMap<String, String>();

    static {
        BET_CODE_SINGLE_FOOTBALL_SXDS_MAP.put("11", "上单");
        BET_CODE_SINGLE_FOOTBALL_SXDS_MAP.put("10", "上双");
        BET_CODE_SINGLE_FOOTBALL_SXDS_MAP.put("01", "下单");
        BET_CODE_SINGLE_FOOTBALL_SXDS_MAP.put("00", "下双");
    }

    public final static String[] SET_RESULT_LOTT_FOOTBALL_VS_S =
            {CHAR_BET_PARAM_FOOTBALL_LETPOINT, CHAR_RESULT_LOTT_FOOTBALL_VS_S /*, CHAR_RESULT_LOTT_FOOTBALL_BONUS_VS*/};
    public final static String[] SET_RESULT_LOTT_FOOTBALL_VS_P =
            {CHAR_BET_PARAM_FOOTBALL_LETPOINT, CHAR_RESULT_LOTT_FOOTBALL_VS_P /*, CHAR_RESULT_LOTT_FOOTBALL_BONUS_VS
            */};

    public final static String[] SET_RESULT_LOTT_FOOTBALL_NVS_S =
            {CHAR_BET_PARAM_FOOTBALL_LETPOINT, CHAR_RESULT_LOTT_FOOTBALL_VS_S /*, CHAR_RESULT_LOTT_FOOTBALL_BONUS_NVS
             */};
    public final static String[] SET_RESULT_LOTT_FOOTBALL_NVS_P =
            {CHAR_BET_PARAM_FOOTBALL_LETPOINT, CHAR_RESULT_LOTT_FOOTBALL_VS_P /*,
            CHAR_RESULT_LOTT_FOOTBALL_BONUS_NVS*/};

    public final static String[] SET_RESULT_LOTT_FOOTBALL_MIX_P =
            {CHAR_BET_PARAM_FOOTBALL_LETPOINT, CHAR_RESULT_LOTT_FOOTBALL_VS_P};

    /**
     * 暂定
     */
    public final static String[] SET_RESULT_LOTT_BASKETBALL_MIX_P =
            {CHAR_BET_PARAM_BASKETBALL_LETPOINT, CHAR_BET_PARAM_BASKETBALL_BASEPOINT, CHAR_RESULT_LOTT_BASKETBALL_VS_P};

    public final static String[][] CHAR_BET_CODE_FOOTBALL_SCORE =
            {
                    {"10", "1:0"},
                    {"20", "2:0"},
                    {"21", "2:1"},
                    {"30", "3:0"},
                    {"31", "3:1"},
                    {"32", "3:2"},
                    {"40", "4:0"},
                    {"41", "4:1"},
                    {"42", "4:2"},
                    {"50", "5:0"},
                    {"51", "5:1"},
                    {"52", "5:2"},
                    {"90", "胜其他"},
                    {"00", "0:0"},
                    {"11", "1:1"},
                    {"22", "2:2"},
                    {"33", "3:3"},
                    {"99", "平其他"},
                    {"01", "0:1"},
                    {"02", "0:2"},
                    {"12", "1:2"},
                    {"03", "0:3"},
                    {"13", "1:3"},
                    {"23", "2:3"},
                    {"04", "0:4"},
                    {"14", "1:4"},
                    {"24", "2:4"},
                    {"05", "0:5"},
                    {"15", "1:5"},
                    {"25", "2:5"},
                    {"09", "负其他"}};

    public final static String[][] CHAR_BET_CODE_SINGLE_FOOTBALL_SCORE =
            {
                    {"90", "胜其他"},
                    {"10", "1:0"},
                    {"20", "2:0"},
                    {"21", "2:1"},
                    {"30", "3:0"},
                    {"31", "3:1"},
                    {"32", "3:2"},
                    {"40", "4:0"},
                    {"41", "4:1"},
                    {"42", "4:2"},
                    {"99", "平其他"},
                    {"00", "0:0"},
                    {"11", "1:1"},
                    {"22", "2:2"},
                    {"33", "3:3"},
                    {"09", "负其他"},
                    {"01", "0:1"},
                    {"02", "0:2"},
                    {"12", "1:2"},
                    {"03", "0:3"},
                    {"13", "1:3"},
                    {"23", "2:3"},
                    {"04", "0:4"},
                    {"14", "1:4"},
                    {"24", "2:4"}};

    public final static String[] CHAR_BET_CODE_SINGLE_FOOTBALL_SCORE_FOR_DISPLY =
            {"10", "20", "21", "30", "31", "32", "40", "41", "42", "90", "00", "11", "22", "33", "99", "01", "02",
                    "12", "03",
                    "13", "23", "04", "14", "24", "09"};

    public final static Map<String, String> BET_CODE_SINGLE_FOOTBALL_SCORE_MAP = new HashMap<String, String>();

    static {
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("90", "胜其他");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("10", "1:0");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("20", "2:0");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("21", "2:1");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("30", "3:0");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("31", "3:1");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("32", "3:2");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("40", "4:0");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("41", "4:1");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("42", "4:2");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("99", "平其他");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("00", "0:0");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("11", "1:1");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("22", "2:2");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("33", "3:3");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("09", "负其他");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("01", "0:1");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("02", "0:2");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("12", "1:2");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("03", "0:3");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("13", "1:3");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("23", "2:3");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("04", "0:4");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("14", "1:4");
        BET_CODE_SINGLE_FOOTBALL_SCORE_MAP.put("24", "2:4");
    }

    public final static String[] SET_RESULT_LOTT_FOOTBALL_SCORE_S =
            {CHAR_RESULT_LOTT_FOOTBALL_SCORE_S /*, CHAR_RESULT_LOTT_FOOTBALL_BONUS_SCORE */};
    public final static String[] SET_RESULT_LOTT_FOOTBALL_SCORE_P =
            {CHAR_RESULT_LOTT_FOOTBALL_SCORE_P /*, CHAR_RESULT_LOTT_FOOTBALL_BONUS_SCORE*/};

    public final static String[][] CHAR_BET_CODE_FOOTBALL_GOAL =
            {
                    {"100", "0球"},
                    {"101", "1球"},
                    {"102", "2球"},
                    {"103", "3球"},
                    {"104", "4球"},
                    {"105", "5球"},
                    {"106", "6球"},
                    {"107", "7+球"}};

    public final static Map<String, String> BET_CODE_SINGLE_FOOTBALL_ZJQ_MAP = new HashMap<String, String>();

    static {
        BET_CODE_SINGLE_FOOTBALL_ZJQ_MAP.put("0", "0");
        BET_CODE_SINGLE_FOOTBALL_ZJQ_MAP.put("1", "1");
        BET_CODE_SINGLE_FOOTBALL_ZJQ_MAP.put("2", "2");
        BET_CODE_SINGLE_FOOTBALL_ZJQ_MAP.put("3", "3");
        BET_CODE_SINGLE_FOOTBALL_ZJQ_MAP.put("4", "4");
        BET_CODE_SINGLE_FOOTBALL_ZJQ_MAP.put("5", "5");
        BET_CODE_SINGLE_FOOTBALL_ZJQ_MAP.put("6", "6");
        BET_CODE_SINGLE_FOOTBALL_ZJQ_MAP.put("7", "7+");
    }

    //	public final static Map<String, String> BET_CODE_MIX_FOOTBALL_RQSPF_MAP = new HashMap<String, String>();
    //	static
    //	{
    //		BET_CODE_MIX_FOOTBALL_RQSPF_MAP.put("10003", "3");
    //		BET_CODE_MIX_FOOTBALL_RQSPF_MAP.put("10001", "1");
    //		BET_CODE_MIX_FOOTBALL_RQSPF_MAP.put("10000", "0");
    //	}

    //	public final static Map<String, String> BET_CODE_MIX_FOOTBALL_ZJQ_MAP = new HashMap<String, String>();
    //	static
    //	{
    //		BET_CODE_MIX_FOOTBALL_ZJQ_MAP.put("100", "0");
    //		BET_CODE_MIX_FOOTBALL_ZJQ_MAP.put("101", "1");
    //		BET_CODE_MIX_FOOTBALL_ZJQ_MAP.put("102", "2");
    //		BET_CODE_MIX_FOOTBALL_ZJQ_MAP.put("103", "3");
    //		BET_CODE_MIX_FOOTBALL_ZJQ_MAP.put("104", "4");
    //		BET_CODE_MIX_FOOTBALL_ZJQ_MAP.put("105", "5");
    //		BET_CODE_MIX_FOOTBALL_ZJQ_MAP.put("106", "6");
    //		BET_CODE_MIX_FOOTBALL_ZJQ_MAP.put("107", "7");
    //	}

    public final static String[] SET_RESULT_LOTT_FOOTBALL_GOAL_S =
            {CHAR_RESULT_LOTT_FOOTBALL_GOAL_S /*, CHAR_RESULT_LOTT_FOOTBALL_BONUS_GOAL*/};
    public final static String[] SET_RESULT_LOTT_FOOTBALL_GOAL_P =
            {CHAR_RESULT_LOTT_FOOTBALL_GOAL_P /*, CHAR_RESULT_LOTT_FOOTBALL_BONUS_GOAL*/};

    public final static String[][] CHAR_BET_CODE_FOOTBALL_HALF =
            {
                    {"1033", "胜胜"},
                    {"1031", "胜平"},
                    {"1030", "胜负"},
                    {"1013", "平胜"},
                    {"1011", "平平"},
                    {"1010", "平负"},
                    {"1003", "负胜"},
                    {"1001", "负平"},
                    {"1000", "负负"}};

    public final static String[][] CHAR_BET_CODE_FOOTBALL_MIX =
            {
                    {"3", "主胜"},
                    {"1", "平"},
                    {"0", "主负"},
                    {"10", "1:0"},
                    {"20", "2:0"},
                    {"21", "2:1"},
                    {"30", "3:0"},
                    {"31", "3:1"},
                    {"32", "3:2"},
                    {"40", "4:0"},
                    {"41", "4:1"},
                    {"42", "4:2"},
                    {"50", "5:0"},
                    {"51", "5:1"},
                    {"52", "5:2"},
                    {"90", "胜其他"},
                    {"00", "0:0"},
                    {"11", "1:1"},
                    {"22", "2:2"},
                    {"33", "3:3"},
                    {"99", "平其他"},
                    {"01", "0:1"},
                    {"02", "0:2"},
                    {"12", "1:2"},
                    {"03", "0:3"},
                    {"13", "1:3"},
                    {"23", "2:3"},
                    {"04", "0:4"},
                    {"14", "1:4"},
                    {"24", "2:4"},
                    {"05", "0:5"},
                    {"15", "1:5"},
                    {"25", "2:5"},
                    {"09", "负其他"},
                    {"100", "0"},
                    {"101", "1"},
                    {"102", "2"},
                    {"103", "3"},
                    {"104", "4"},
                    {"105", "5"},
                    {"106", "6"},
                    {"107", "7+"},
                    {"1033", "胜胜"},
                    {"1031", "胜平"},
                    {"1030", "胜负"},
                    {"1013", "平胜"},
                    {"1011", "平平"},
                    {"1010", "平负"},
                    {"1003", "负胜"},
                    {"1001", "负平"},
                    {"1000", "负负"},
                    {"10003", "让球胜"},
                    {"10001", "让球平"},
                    {"10000", "让球负"}};

    public final static Map<String, String> BET_OPTION_MIX_JCGAMEEN_MAP = new HashMap<String, String>();

    static {
        BET_OPTION_MIX_JCGAMEEN_MAP.put("3", GameConstant.JCZQ_SPF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("1", GameConstant.JCZQ_SPF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("0", GameConstant.JCZQ_SPF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("10", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("20", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("21", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("30", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("31", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("32", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("40", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("41", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("42", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("50", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("51", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("52", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("90", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("00", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("11", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("22", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("33", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("99", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("01", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("02", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("12", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("03", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("13", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("23", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("04", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("14", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("24", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("05", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("15", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("25", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("09", GameConstant.JCZQ_BF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("100", GameConstant.JCZQ_ZJQ_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("101", GameConstant.JCZQ_ZJQ_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("102", GameConstant.JCZQ_ZJQ_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("103", GameConstant.JCZQ_ZJQ_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("104", GameConstant.JCZQ_ZJQ_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("105", GameConstant.JCZQ_ZJQ_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("106", GameConstant.JCZQ_ZJQ_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("107", GameConstant.JCZQ_ZJQ_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("1033", GameConstant.JCZQ_BQC_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("1031", GameConstant.JCZQ_BQC_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("1030", GameConstant.JCZQ_BQC_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("1013", GameConstant.JCZQ_BQC_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("1011", GameConstant.JCZQ_BQC_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("1010", GameConstant.JCZQ_BQC_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("1003", GameConstant.JCZQ_BQC_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("1001", GameConstant.JCZQ_BQC_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("1000", GameConstant.JCZQ_BQC_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("10003", GameConstant.JCZQ_RQSPF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("10001", GameConstant.JCZQ_RQSPF_P);
        BET_OPTION_MIX_JCGAMEEN_MAP.put("10000", GameConstant.JCZQ_RQSPF_P);
    }

    public final static Map<String, String> BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP = new HashMap<String, String>();

    static {
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("3", GameConstant.JCZQ_SPF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("1", GameConstant.JCZQ_SPF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("0", GameConstant.JCZQ_SPF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("10", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("20", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("21", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("30", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("31", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("32", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("40", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("41", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("42", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("50", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("51", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("52", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("90", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("00", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("11", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("22", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("33", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("99", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("01", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("02", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("12", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("03", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("13", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("23", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("04", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("14", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("24", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("05", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("15", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("25", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("09", GameConstant.JCZQ_BF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("100", GameConstant.JCZQ_ZJQ_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("101", GameConstant.JCZQ_ZJQ_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("102", GameConstant.JCZQ_ZJQ_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("103", GameConstant.JCZQ_ZJQ_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("104", GameConstant.JCZQ_ZJQ_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("105", GameConstant.JCZQ_ZJQ_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("106", GameConstant.JCZQ_ZJQ_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("107", GameConstant.JCZQ_ZJQ_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("1033", GameConstant.JCZQ_BQC_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("1031", GameConstant.JCZQ_BQC_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("1030", GameConstant.JCZQ_BQC_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("1013", GameConstant.JCZQ_BQC_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("1011", GameConstant.JCZQ_BQC_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("1010", GameConstant.JCZQ_BQC_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("1003", GameConstant.JCZQ_BQC_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("1001", GameConstant.JCZQ_BQC_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("1000", GameConstant.JCZQ_BQC_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("10003", GameConstant.JCZQ_RQSPF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("10001", GameConstant.JCZQ_RQSPF_S);
        BET_OPTION_MIX_JC_SINGLE_GAMEEN_MAP.put("10000", GameConstant.JCZQ_RQSPF_S);
    }

    public final static Map<String, String> BET_OPTIONCN_MIX_JCGAMEEN_MAP = new HashMap<String, String>();

    static {
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("胜", GameConstant.JCZQ_SPF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("平", GameConstant.JCZQ_SPF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("负", GameConstant.JCZQ_SPF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("1:0", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("2:0", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("2:1", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("3:0", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("3:1", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("3:2", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("4:0", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("4:1", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("4:2", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("5:0", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("5:1", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("5:2", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("胜其他", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("0:0", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("1:1", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("2:2", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("3:3", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("平其他", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("0:1", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("0:2", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("1:2", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("0:3", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("1:3", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("2:3", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("0:4", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("1:4", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("2:4", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("0:5", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("1:5", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("2:5", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("负其他", GameConstant.JCZQ_BF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("0", GameConstant.JCZQ_ZJQ_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("1", GameConstant.JCZQ_ZJQ_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("2", GameConstant.JCZQ_ZJQ_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("3", GameConstant.JCZQ_ZJQ_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("4", GameConstant.JCZQ_ZJQ_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("5", GameConstant.JCZQ_ZJQ_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("6", GameConstant.JCZQ_ZJQ_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("7+", GameConstant.JCZQ_ZJQ_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("胜胜", GameConstant.JCZQ_BQC_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("胜平", GameConstant.JCZQ_BQC_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("胜负", GameConstant.JCZQ_BQC_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("平胜", GameConstant.JCZQ_BQC_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("平平", GameConstant.JCZQ_BQC_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("平负", GameConstant.JCZQ_BQC_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("负胜", GameConstant.JCZQ_BQC_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("负平", GameConstant.JCZQ_BQC_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("负负", GameConstant.JCZQ_BQC_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("让球胜", GameConstant.JCZQ_RQSPF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("让球平", GameConstant.JCZQ_RQSPF_P);
        BET_OPTIONCN_MIX_JCGAMEEN_MAP.put("让球负", GameConstant.JCZQ_RQSPF_P);
    }

    public final static Map<String, String> BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL = new HashMap<String, String>();

    static {
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("3", "3");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("1", "1");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("0", "0");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("10", "10");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("20", "20");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("21", "21");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("30", "30");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("31", "31");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("32", "32");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("40", "40");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("41", "41");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("42", "42");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("50", "50");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("51", "51");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("52", "52");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("90", "90");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("00", "00");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("11", "11");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("22", "22");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("33", "33");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("99", "99");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("01", "01");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("02", "02");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("12", "12");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("03", "03");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("13", "13");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("23", "23");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("04", "04");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("14", "14");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("24", "24");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("05", "05");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("15", "15");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("25", "25");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("09", "09");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("100", "0");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("101", "1");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("102", "2");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("103", "3");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("104", "4");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("105", "5");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("106", "6");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("107", "7");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("1033", "33");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("1031", "31");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("1030", "30");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("1013", "13");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("1011", "11");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("1010", "10");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("1003", "03");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("1001", "01");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("1000", "00");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("10003", "3");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("10001", "1");
        BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.put("10000", "0");
    }

    public final static String[][] CHAR_BET_CODE_BASKETBALL_MIX =
            {
                    {"3", "主胜"},
                    {"0", "主负"},
                    {"31", "主胜1-5"},
                    {"32", "主胜6-10"},
                    {"33", "主胜11-15"},
                    {"34", "主胜16-20"},
                    {"35", "主胜21-25"},
                    {"36", "主胜26+"},
                    {"01", "客胜1-5"},
                    {"02", "客胜6-10"},
                    {"03", "客胜11-15"},
                    {"04", "客胜16-20"},
                    {"05", "客胜21-25"},
                    {"06", "客胜26+"},
                    {"102", "大分"},
                    {"101", "小分"},
                    {"1003", "让胜"},
                    {"1000", "让负"}};

    public final static Map<String, String> BET_OPTION_MIX_JCLQGAMEEN_MAP = new HashMap<String, String>();

    static {
        BET_OPTION_MIX_JCLQGAMEEN_MAP.put("3", GameConstant.JCLQ_SF_P);
        BET_OPTION_MIX_JCLQGAMEEN_MAP.put("0", GameConstant.JCLQ_SF_P);
        BET_OPTION_MIX_JCLQGAMEEN_MAP.put("31", GameConstant.JCLQ_SFC_P);
        BET_OPTION_MIX_JCLQGAMEEN_MAP.put("32", GameConstant.JCLQ_SFC_P);
        BET_OPTION_MIX_JCLQGAMEEN_MAP.put("33", GameConstant.JCLQ_SFC_P);
        BET_OPTION_MIX_JCLQGAMEEN_MAP.put("34", GameConstant.JCLQ_SFC_P);
        BET_OPTION_MIX_JCLQGAMEEN_MAP.put("35", GameConstant.JCLQ_SFC_P);
        BET_OPTION_MIX_JCLQGAMEEN_MAP.put("36", GameConstant.JCLQ_SFC_P);
        BET_OPTION_MIX_JCLQGAMEEN_MAP.put("01", GameConstant.JCLQ_SFC_P);
        BET_OPTION_MIX_JCLQGAMEEN_MAP.put("02", GameConstant.JCLQ_SFC_P);
        BET_OPTION_MIX_JCLQGAMEEN_MAP.put("03", GameConstant.JCLQ_SFC_P);
        BET_OPTION_MIX_JCLQGAMEEN_MAP.put("04", GameConstant.JCLQ_SFC_P);
        BET_OPTION_MIX_JCLQGAMEEN_MAP.put("05", GameConstant.JCLQ_SFC_P);
        BET_OPTION_MIX_JCLQGAMEEN_MAP.put("06", GameConstant.JCLQ_SFC_P);
        BET_OPTION_MIX_JCLQGAMEEN_MAP.put("101", GameConstant.JCLQ_DXF_P);
        BET_OPTION_MIX_JCLQGAMEEN_MAP.put("102", GameConstant.JCLQ_DXF_P);
        BET_OPTION_MIX_JCLQGAMEEN_MAP.put("1003", GameConstant.JCLQ_RFSF_P);
        BET_OPTION_MIX_JCLQGAMEEN_MAP.put("1000", GameConstant.JCLQ_RFSF_P);
    }

    public final static Map<String, String> BET_OPTION_MIX_JCLQ_SINGLE_GAMEEN_MAP = new HashMap<String, String>();

    static {
        BET_OPTION_MIX_JCLQ_SINGLE_GAMEEN_MAP.put("3", GameConstant.JCLQ_SF_S);
        BET_OPTION_MIX_JCLQ_SINGLE_GAMEEN_MAP.put("0", GameConstant.JCLQ_SF_S);
        BET_OPTION_MIX_JCLQ_SINGLE_GAMEEN_MAP.put("31", GameConstant.JCLQ_SFC_S);
        BET_OPTION_MIX_JCLQ_SINGLE_GAMEEN_MAP.put("32", GameConstant.JCLQ_SFC_S);
        BET_OPTION_MIX_JCLQ_SINGLE_GAMEEN_MAP.put("33", GameConstant.JCLQ_SFC_S);
        BET_OPTION_MIX_JCLQ_SINGLE_GAMEEN_MAP.put("34", GameConstant.JCLQ_SFC_S);
        BET_OPTION_MIX_JCLQ_SINGLE_GAMEEN_MAP.put("35", GameConstant.JCLQ_SFC_S);
        BET_OPTION_MIX_JCLQ_SINGLE_GAMEEN_MAP.put("36", GameConstant.JCLQ_SFC_S);
        BET_OPTION_MIX_JCLQ_SINGLE_GAMEEN_MAP.put("01", GameConstant.JCLQ_SFC_S);
        BET_OPTION_MIX_JCLQ_SINGLE_GAMEEN_MAP.put("02", GameConstant.JCLQ_SFC_S);
        BET_OPTION_MIX_JCLQ_SINGLE_GAMEEN_MAP.put("03", GameConstant.JCLQ_SFC_S);
        BET_OPTION_MIX_JCLQ_SINGLE_GAMEEN_MAP.put("04", GameConstant.JCLQ_SFC_S);
        BET_OPTION_MIX_JCLQ_SINGLE_GAMEEN_MAP.put("05", GameConstant.JCLQ_SFC_S);
        BET_OPTION_MIX_JCLQ_SINGLE_GAMEEN_MAP.put("06", GameConstant.JCLQ_SFC_S);
        BET_OPTION_MIX_JCLQ_SINGLE_GAMEEN_MAP.put("101", GameConstant.JCLQ_DXF_S);
        BET_OPTION_MIX_JCLQ_SINGLE_GAMEEN_MAP.put("102", GameConstant.JCLQ_DXF_S);
        BET_OPTION_MIX_JCLQ_SINGLE_GAMEEN_MAP.put("1003", GameConstant.JCLQ_RFSF_S);
        BET_OPTION_MIX_JCLQ_SINGLE_GAMEEN_MAP.put("1000", GameConstant.JCLQ_RFSF_S);
    }

    public final static Map<String, String> BET_OPTIONCN_MIX_JCLQGAMEEN_MAP = new HashMap<String, String>();

    static {
        BET_OPTIONCN_MIX_JCLQGAMEEN_MAP.put("主胜", GameConstant.JCLQ_SF_P);
        BET_OPTIONCN_MIX_JCLQGAMEEN_MAP.put("主负", GameConstant.JCLQ_SF_P);
        BET_OPTIONCN_MIX_JCLQGAMEEN_MAP.put("主胜1-5", GameConstant.JCLQ_SFC_P);
        BET_OPTIONCN_MIX_JCLQGAMEEN_MAP.put("主胜6-10", GameConstant.JCLQ_SFC_P);
        BET_OPTIONCN_MIX_JCLQGAMEEN_MAP.put("主胜11-15", GameConstant.JCLQ_SFC_P);
        BET_OPTIONCN_MIX_JCLQGAMEEN_MAP.put("主胜16-20", GameConstant.JCLQ_SFC_P);
        BET_OPTIONCN_MIX_JCLQGAMEEN_MAP.put("主胜21-25", GameConstant.JCLQ_SFC_P);
        BET_OPTIONCN_MIX_JCLQGAMEEN_MAP.put("主胜26+", GameConstant.JCLQ_SFC_P);
        BET_OPTIONCN_MIX_JCLQGAMEEN_MAP.put("客胜1-5", GameConstant.JCLQ_SFC_P);
        BET_OPTIONCN_MIX_JCLQGAMEEN_MAP.put("客胜6-10", GameConstant.JCLQ_SFC_P);
        BET_OPTIONCN_MIX_JCLQGAMEEN_MAP.put("客胜11-15", GameConstant.JCLQ_SFC_P);
        BET_OPTIONCN_MIX_JCLQGAMEEN_MAP.put("客胜16-20", GameConstant.JCLQ_SFC_P);
        BET_OPTIONCN_MIX_JCLQGAMEEN_MAP.put("客胜21-25", GameConstant.JCLQ_SFC_P);
        BET_OPTIONCN_MIX_JCLQGAMEEN_MAP.put("客胜26+", GameConstant.JCLQ_SFC_P);
        BET_OPTIONCN_MIX_JCLQGAMEEN_MAP.put("大分", GameConstant.JCLQ_DXF_P);
        BET_OPTIONCN_MIX_JCLQGAMEEN_MAP.put("小分", GameConstant.JCLQ_DXF_P);
        BET_OPTIONCN_MIX_JCLQGAMEEN_MAP.put("让胜", GameConstant.JCLQ_RFSF_P);
        BET_OPTIONCN_MIX_JCLQGAMEEN_MAP.put("让负", GameConstant.JCLQ_RFSF_P);
    }

    public final static Map<String, String> BET_OPTION_MAPING_JCLQ_MIX_TO_ORIGINAL = new HashMap<String, String>();

    static {
        BET_OPTION_MAPING_JCLQ_MIX_TO_ORIGINAL.put("3", "3");
        BET_OPTION_MAPING_JCLQ_MIX_TO_ORIGINAL.put("0", "0");
        BET_OPTION_MAPING_JCLQ_MIX_TO_ORIGINAL.put("31", "31");
        BET_OPTION_MAPING_JCLQ_MIX_TO_ORIGINAL.put("32", "32");
        BET_OPTION_MAPING_JCLQ_MIX_TO_ORIGINAL.put("33", "33");
        BET_OPTION_MAPING_JCLQ_MIX_TO_ORIGINAL.put("34", "34");
        BET_OPTION_MAPING_JCLQ_MIX_TO_ORIGINAL.put("35", "35");
        BET_OPTION_MAPING_JCLQ_MIX_TO_ORIGINAL.put("36", "36");
        BET_OPTION_MAPING_JCLQ_MIX_TO_ORIGINAL.put("01", "01");
        BET_OPTION_MAPING_JCLQ_MIX_TO_ORIGINAL.put("02", "02");
        BET_OPTION_MAPING_JCLQ_MIX_TO_ORIGINAL.put("03", "03");
        BET_OPTION_MAPING_JCLQ_MIX_TO_ORIGINAL.put("04", "04");
        BET_OPTION_MAPING_JCLQ_MIX_TO_ORIGINAL.put("05", "05");
        BET_OPTION_MAPING_JCLQ_MIX_TO_ORIGINAL.put("06", "06");
        BET_OPTION_MAPING_JCLQ_MIX_TO_ORIGINAL.put("101", "1");
        BET_OPTION_MAPING_JCLQ_MIX_TO_ORIGINAL.put("102", "2");
        BET_OPTION_MAPING_JCLQ_MIX_TO_ORIGINAL.put("1003", "3");
        BET_OPTION_MAPING_JCLQ_MIX_TO_ORIGINAL.put("1000", "0");
    }

    public final static Map<String, Integer> BET_MAX_PASSWAY_JCGAME_MAP = new HashMap<String, Integer>();

    static {
        BET_MAX_PASSWAY_JCGAME_MAP.put(GameConstant.JCZQ_SPF_P, 8);
        BET_MAX_PASSWAY_JCGAME_MAP.put(GameConstant.JCZQ_RQSPF_P, 8);
        BET_MAX_PASSWAY_JCGAME_MAP.put(GameConstant.JCZQ_BF_P, 4);
        BET_MAX_PASSWAY_JCGAME_MAP.put(GameConstant.JCZQ_ZJQ_P, 6);
        BET_MAX_PASSWAY_JCGAME_MAP.put(GameConstant.JCZQ_BQC_P, 4);
        BET_MAX_PASSWAY_JCGAME_MAP.put(GameConstant.JCLQ_SF_P, 8);
        BET_MAX_PASSWAY_JCGAME_MAP.put(GameConstant.JCLQ_RFSF_P, 8);
        BET_MAX_PASSWAY_JCGAME_MAP.put(GameConstant.JCLQ_SFC_P, 4);
        BET_MAX_PASSWAY_JCGAME_MAP.put(GameConstant.JCLQ_DXF_P, 8);
    }

    public final static String[][] CHAR_BET_CODE_SINGLE_FOOTBALL_HALF =
            {
                    {"33", "胜-胜"},
                    {"31", "胜-平"},
                    {"30", "胜-负"},
                    {"13", "平-胜"},
                    {"11", "平-平"},
                    {"10", "平-负"},
                    {"03", "负-胜"},
                    {"01", "负-平"},
                    {"00", "负-负"}};

    public final static Map<String, String> BET_CODE_SINGLE_FOOTBALL_HALF_MAP = new HashMap<String, String>();

    static {
        BET_CODE_SINGLE_FOOTBALL_HALF_MAP.put("33", "胜胜");
        BET_CODE_SINGLE_FOOTBALL_HALF_MAP.put("31", "胜平");
        BET_CODE_SINGLE_FOOTBALL_HALF_MAP.put("30", "胜负");
        BET_CODE_SINGLE_FOOTBALL_HALF_MAP.put("13", "平胜");
        BET_CODE_SINGLE_FOOTBALL_HALF_MAP.put("11", "平平");
        BET_CODE_SINGLE_FOOTBALL_HALF_MAP.put("10", "平负");
        BET_CODE_SINGLE_FOOTBALL_HALF_MAP.put("03", "负胜");
        BET_CODE_SINGLE_FOOTBALL_HALF_MAP.put("01", "负平");
        BET_CODE_SINGLE_FOOTBALL_HALF_MAP.put("00", "负负");
    }

    public final static Map<String, String> BET_CODE_SINGLE_FOOTBALL_SPF_MAP = new HashMap<String, String>();

    static {
        BET_CODE_SINGLE_FOOTBALL_SPF_MAP.put("3", "胜");
        BET_CODE_SINGLE_FOOTBALL_SPF_MAP.put("1", "平");
        BET_CODE_SINGLE_FOOTBALL_SPF_MAP.put("0", "负");
    }

    //	public final static Map<String, String> BET_CODE_MIX_FOOTBALL_HALF_MAP = new HashMap<String, String>();
    //	static
    //	{
    //		BET_CODE_MIX_FOOTBALL_HALF_MAP.put("1033", "33");
    //		BET_CODE_MIX_FOOTBALL_HALF_MAP.put("1031", "31");
    //		BET_CODE_MIX_FOOTBALL_HALF_MAP.put("1030", "30");
    //		BET_CODE_MIX_FOOTBALL_HALF_MAP.put("1013", "13");
    //		BET_CODE_MIX_FOOTBALL_HALF_MAP.put("1011", "11");
    //		BET_CODE_MIX_FOOTBALL_HALF_MAP.put("1010", "10");
    //		BET_CODE_MIX_FOOTBALL_HALF_MAP.put("1003", "03");
    //		BET_CODE_MIX_FOOTBALL_HALF_MAP.put("1001", "01");
    //		BET_CODE_MIX_FOOTBALL_HALF_MAP.put("1000", "00");
    //	}

    public final static String[] SET_RESULT_LOTT_FOOTBALL_HALF_S =
            {CHAR_RESULT_LOTT_FOOTBALL_HALF_S /*, CHAR_RESULT_LOTT_FOOTBALL_BONUS_HALF*/};
    public final static String[] SET_RESULT_LOTT_FOOTBALL_HALF_P =
            {CHAR_RESULT_LOTT_FOOTBALL_HALF_P /*, CHAR_RESULT_LOTT_FOOTBALL_BONUS_HALF*/};

    public final static String[][] CHAR_BET_CODE_BASKETBALL_VS =
            {
                    {"3", "主胜"},
                    {"0", "主负"},};
    public final static String[] SET_RESULT_LOTT_BASKETBALL_VS_S =
            {CHAR_RESULT_LOTT_BASKETBALL_VS_S /*, CHAR_RESULT_LOTT_BASKETBALL_BONUS_VS */};
    public final static String[] SET_RESULT_LOTT_BASKETBALL_VS_P =
            {CHAR_RESULT_LOTT_BASKETBALL_VS_P /*, CHAR_RESULT_LOTT_BASKETBALL_BONUS_VS*/};

    public final static String[][] CHAR_BET_CODE_BASKETBALL_LETVS =
            {
                    {"1003", "让胜"},
                    {"1000", "让负"},};
    public final static String[] CHAR_BET_OPTION_BASKETBALL_LETVS =
            {CHAR_BET_PARAM_BASKETBALL_LETPOINT, "让分"};
    public final static String[] SET_RESULT_LOTT_BASKETBALL_LETVS_S =
            {CHAR_BET_PARAM_BASKETBALL_LETPOINT, CHAR_RESULT_LOTT_BASKETBALL_LETVS_S /*,
            CHAR_RESULT_LOTT_BASKETBALL_BONUS_LETVS*/};
    public final static String[] SET_RESULT_LOTT_BASKETBALL_LETVS_P =
            {CHAR_BET_PARAM_BASKETBALL_LETPOINT, CHAR_RESULT_LOTT_BASKETBALL_LETVS_P /*,
            CHAR_RESULT_LOTT_BASKETBALL_BONUS_LETVS*/};

    public final static String[][] CHAR_BET_CODE_BASKETBALL_BS =
            {
                    {"102", "大分"},
                    {"101", "小分"},};
    public final static String[] SET_RESULT_LOTT_BASKETBALL_BS_S =
            {CHAR_BET_PARAM_BASKETBALL_BASEPOINT, CHAR_RESULT_LOTT_BASKETBALL_BS_S /*,
            CHAR_RESULT_LOTT_BASKETBALL_BONUS_BS*/};
    public final static String[] SET_RESULT_LOTT_BASKETBALL_BS_P =
            {CHAR_BET_PARAM_BASKETBALL_BASEPOINT, CHAR_RESULT_LOTT_BASKETBALL_BS_P /*,
            CHAR_RESULT_LOTT_BASKETBALL_BONUS_BS*/};

    public final static String[][] CHAR_BET_CODE_BASKETBALL_DIFF =
            {
                    {"31", "主胜1-5"},
                    {"32", "主胜6-10"},
                    {"33", "主胜11-15"},
                    {"34", "主胜16-20"},
                    {"35", "主胜21-25"},
                    {"36", "主胜26+"},
                    {"01", "客胜1-5"},
                    {"02", "客胜6-10"},
                    {"03", "客胜11-15"},
                    {"04", "客胜16-20"},
                    {"05", "客胜21-25"},
                    {"06", "客胜26+"},};

    public static Map<String, String> basketballCnResultMap = new HashMap<String, String>();

    static {
        basketballCnResultMap.put("主胜1-5", "31");
        basketballCnResultMap.put("主胜6-10", "32");
        basketballCnResultMap.put("主胜11-15", "33");
        basketballCnResultMap.put("主胜16-20", "34");
        basketballCnResultMap.put("主胜21-25", "35");
        basketballCnResultMap.put("主胜26+", "36");

        basketballCnResultMap.put("客胜1-5", "01");
        basketballCnResultMap.put("客胜6-10", "02");
        basketballCnResultMap.put("客胜11-15", "03");
        basketballCnResultMap.put("客胜16-20", "04");
        basketballCnResultMap.put("客胜21-25", "05");
        basketballCnResultMap.put("客胜26+", "06");
    }

    public static String getSpByResult(String resCn, String sp) {
        //resCode是胜负差编码如31，32，01，02...
        String resCode = RuleConstant.basketballCnResultMap.get(resCn);
        for (String spcouple : sp.split(",")) {
            if (StringUtils.equals(resCode, spcouple.split(":")[0]))
                return spcouple.split(":")[1];
        }
        //没找到对应的值，返回null
        return null;

    }

    public final static String[] CHAR_BET_OPTION_BASKETBALL_BS =
            {CHAR_BET_PARAM_BASKETBALL_BASEPOINT, "预设总分"};
    public final static String[] SET_RESULT_LOTT_BASKETBALL_DIFF_S =
            {CHAR_RESULT_LOTT_BASKETBALL_DIFF_S /*, CHAR_RESULT_LOTT_BASKETBALL_BONUS_DIFF*/};
    public final static String[] SET_RESULT_LOTT_BASKETBALL_DIFF_P =
            {CHAR_RESULT_LOTT_BASKETBALL_DIFF_P /*, CHAR_RESULT_LOTT_BASKETBALL_BONUS_DIFF*/};

    /**
     * 篮球混合过关有两个额外的选项
     */
    public final static String[] CHAR_BET_OPTION_BASKETBALL_MIX =
            {CHAR_BET_PARAM_BASKETBALL_BASEPOINT, "预设总分", CHAR_BET_PARAM_BASKETBALL_LETPOINT, "让分"};

    public static Map<String, String[][]> lotteryBetCodeMap;

    public static void initBetOptionMap() {
        lotteryBetCodeMap = new HashMap<String, String[][]>();
        lotteryBetCodeMap.put(GameConstant.JCLQ_SF_P, CHAR_BET_CODE_BASKETBALL_VS);
        lotteryBetCodeMap.put(GameConstant.JCLQ_SF_S, CHAR_BET_CODE_BASKETBALL_VS);
        lotteryBetCodeMap.put(GameConstant.JCLQ_RFSF_P, CHAR_BET_CODE_BASKETBALL_LETVS);
        lotteryBetCodeMap.put(GameConstant.JCLQ_RFSF_S, CHAR_BET_CODE_BASKETBALL_LETVS);
        lotteryBetCodeMap.put(GameConstant.JCLQ_SFC_P, CHAR_BET_CODE_BASKETBALL_DIFF);
        lotteryBetCodeMap.put(GameConstant.JCLQ_SFC_S, CHAR_BET_CODE_BASKETBALL_DIFF);
        lotteryBetCodeMap.put(GameConstant.JCLQ_DXF_P, CHAR_BET_CODE_BASKETBALL_BS);
        lotteryBetCodeMap.put(GameConstant.JCLQ_DXF_S, CHAR_BET_CODE_BASKETBALL_BS);
        lotteryBetCodeMap.put(GameConstant.JCZQ_SPF_P, CHAR_BET_CODE_FOOTBALL_VS);
        lotteryBetCodeMap.put(GameConstant.JCZQ_SPF_S, CHAR_BET_CODE_FOOTBALL_VS);
        lotteryBetCodeMap.put(GameConstant.JCZQ_RQSPF_P, CHAR_BET_CODE_FOOTBALL_VS);
        lotteryBetCodeMap.put(GameConstant.JCZQ_RQSPF_S, CHAR_BET_CODE_FOOTBALL_VS);
        lotteryBetCodeMap.put(GameConstant.JCZQ_BF_P, CHAR_BET_CODE_FOOTBALL_SCORE);
        lotteryBetCodeMap.put(GameConstant.JCZQ_BF_S, CHAR_BET_CODE_FOOTBALL_SCORE);
        lotteryBetCodeMap.put(GameConstant.JCZQ_ZJQ_P, CHAR_BET_CODE_FOOTBALL_GOAL);
        lotteryBetCodeMap.put(GameConstant.JCZQ_ZJQ_S, CHAR_BET_CODE_FOOTBALL_GOAL);
        lotteryBetCodeMap.put(GameConstant.JCZQ_BQC_P, CHAR_BET_CODE_FOOTBALL_HALF);
        lotteryBetCodeMap.put(GameConstant.JCZQ_BQC_S, CHAR_BET_CODE_FOOTBALL_HALF);
        lotteryBetCodeMap.put(GameConstant.JCZQ_MIX_P, CHAR_BET_CODE_FOOTBALL_MIX);
        lotteryBetCodeMap.put(GameConstant.JCZQ_MIX_P, CHAR_BET_CODE_BASKETBALL_MIX);
    }

    public static Map<String, String[]> lotteryMatchResultCodeMap;

    public static void initMatchResultOptionMap() {
        lotteryMatchResultCodeMap = new HashMap<String, String[]>();
        lotteryMatchResultCodeMap.put(GameConstant.JCLQ_SF_P, SET_RESULT_MATCH_BASKETBALL);
        lotteryMatchResultCodeMap.put(GameConstant.JCLQ_SF_S, SET_RESULT_MATCH_BASKETBALL);
        lotteryMatchResultCodeMap.put(GameConstant.JCLQ_RFSF_P, SET_RESULT_MATCH_BASKETBALL);
        lotteryMatchResultCodeMap.put(GameConstant.JCLQ_RFSF_S, SET_RESULT_MATCH_BASKETBALL);
        lotteryMatchResultCodeMap.put(GameConstant.JCLQ_SFC_P, SET_RESULT_MATCH_BASKETBALL);
        lotteryMatchResultCodeMap.put(GameConstant.JCLQ_SFC_S, SET_RESULT_MATCH_BASKETBALL);
        lotteryMatchResultCodeMap.put(GameConstant.JCLQ_DXF_P, SET_RESULT_MATCH_BASKETBALL);
        lotteryMatchResultCodeMap.put(GameConstant.JCLQ_DXF_S, SET_RESULT_MATCH_BASKETBALL);
        lotteryMatchResultCodeMap.put(GameConstant.JCZQ_MIX_P, SET_RESULT_MATCH_BASKETBALL);
        lotteryMatchResultCodeMap.put(GameConstant.JCZQ_SPF_P, SET_RESULT_MATCH_FOOTBALL);
        lotteryMatchResultCodeMap.put(GameConstant.JCZQ_SPF_S, SET_RESULT_MATCH_FOOTBALL);
        lotteryMatchResultCodeMap.put(GameConstant.JCZQ_RQSPF_P, SET_RESULT_MATCH_FOOTBALL);
        lotteryMatchResultCodeMap.put(GameConstant.JCZQ_MIX_P, SET_RESULT_MATCH_FOOTBALL);
        lotteryMatchResultCodeMap.put(GameConstant.JCZQ_RQSPF_S, SET_RESULT_MATCH_FOOTBALL);
        lotteryMatchResultCodeMap.put(GameConstant.JCZQ_BF_P, SET_RESULT_MATCH_FOOTBALL);
        lotteryMatchResultCodeMap.put(GameConstant.JCZQ_BF_S, SET_RESULT_MATCH_FOOTBALL);
        lotteryMatchResultCodeMap.put(GameConstant.JCZQ_ZJQ_P, SET_RESULT_MATCH_FOOTBALL);
        lotteryMatchResultCodeMap.put(GameConstant.JCZQ_ZJQ_S, SET_RESULT_MATCH_FOOTBALL);
        lotteryMatchResultCodeMap.put(GameConstant.JCZQ_BQC_P, SET_RESULT_MATCH_FOOTBALL);
        lotteryMatchResultCodeMap.put(GameConstant.JCZQ_BQC_S, SET_RESULT_MATCH_FOOTBALL);
    }

    public static Map<String, String[]> lotteryLottResultCodeMap;

    public static void initLottResultOptionMap() {
        lotteryLottResultCodeMap = new HashMap<String, String[]>();
        lotteryLottResultCodeMap.put(GameConstant.JCLQ_SF_P, SET_RESULT_LOTT_BASKETBALL_VS_P);
        lotteryLottResultCodeMap.put(GameConstant.JCLQ_SF_S, SET_RESULT_LOTT_BASKETBALL_VS_S);
        lotteryLottResultCodeMap.put(GameConstant.JCLQ_RFSF_P, SET_RESULT_LOTT_BASKETBALL_LETVS_P);
        lotteryLottResultCodeMap.put(GameConstant.JCLQ_RFSF_S, SET_RESULT_LOTT_BASKETBALL_LETVS_S);
        lotteryLottResultCodeMap.put(GameConstant.JCLQ_SFC_P, SET_RESULT_LOTT_BASKETBALL_DIFF_P);
        lotteryLottResultCodeMap.put(GameConstant.JCLQ_SFC_S, SET_RESULT_LOTT_BASKETBALL_DIFF_S);
        lotteryLottResultCodeMap.put(GameConstant.JCLQ_DXF_P, SET_RESULT_LOTT_BASKETBALL_BS_P);
        lotteryLottResultCodeMap.put(GameConstant.JCLQ_DXF_S, SET_RESULT_LOTT_BASKETBALL_BS_S);
        lotteryLottResultCodeMap.put(GameConstant.JCZQ_MIX_P, SET_RESULT_LOTT_BASKETBALL_MIX_P);
        lotteryLottResultCodeMap.put(GameConstant.JCZQ_SPF_P, SET_RESULT_LOTT_FOOTBALL_VS_P);
        lotteryLottResultCodeMap.put(GameConstant.JCZQ_SPF_S, SET_RESULT_LOTT_FOOTBALL_VS_S);
        lotteryLottResultCodeMap.put(GameConstant.JCZQ_MIX_P, SET_RESULT_LOTT_FOOTBALL_MIX_P);
        lotteryLottResultCodeMap.put(GameConstant.JCZQ_RQSPF_P, SET_RESULT_LOTT_FOOTBALL_NVS_P);
        lotteryLottResultCodeMap.put(GameConstant.JCZQ_RQSPF_S, SET_RESULT_LOTT_FOOTBALL_NVS_S);
        lotteryLottResultCodeMap.put(GameConstant.JCZQ_BF_P, SET_RESULT_LOTT_FOOTBALL_SCORE_P);
        lotteryLottResultCodeMap.put(GameConstant.JCZQ_BF_S, SET_RESULT_LOTT_FOOTBALL_SCORE_S);
        lotteryLottResultCodeMap.put(GameConstant.JCZQ_ZJQ_P, SET_RESULT_LOTT_FOOTBALL_GOAL_P);
        lotteryLottResultCodeMap.put(GameConstant.JCZQ_ZJQ_S, SET_RESULT_LOTT_FOOTBALL_GOAL_S);
        lotteryLottResultCodeMap.put(GameConstant.JCZQ_BQC_P, SET_RESULT_LOTT_FOOTBALL_HALF_P);
        lotteryLottResultCodeMap.put(GameConstant.JCZQ_BQC_S, SET_RESULT_LOTT_FOOTBALL_HALF_S);
    }

    public static Map<String, BigDecimal> contestBetPriceMap;

    public static void initBetPriceMap() {
        BigDecimal price = new BigDecimal(2);
        contestBetPriceMap = new HashMap<>();
        contestBetPriceMap.put(GameConstant.JCLQ_SF_P, price);
        contestBetPriceMap.put(GameConstant.JCLQ_SF_S, price);
        contestBetPriceMap.put(GameConstant.JCLQ_RFSF_P, price);
        contestBetPriceMap.put(GameConstant.JCLQ_RFSF_S, price);
        contestBetPriceMap.put(GameConstant.JCLQ_SFC_P, price);
        contestBetPriceMap.put(GameConstant.JCLQ_SFC_S, price);
        contestBetPriceMap.put(GameConstant.JCLQ_DXF_P, price);
        contestBetPriceMap.put(GameConstant.JCLQ_DXF_S, price);
        contestBetPriceMap.put(GameConstant.JCZQ_SPF_P, price);
        contestBetPriceMap.put(GameConstant.JCZQ_SPF_S, price);
        contestBetPriceMap.put(GameConstant.JCZQ_RQSPF_P, price);
        contestBetPriceMap.put(GameConstant.JCZQ_RQSPF_S, price);
        contestBetPriceMap.put(GameConstant.JCZQ_BF_P, price);
        contestBetPriceMap.put(GameConstant.JCZQ_BF_S, price);
        contestBetPriceMap.put(GameConstant.JCZQ_ZJQ_P, price);
        contestBetPriceMap.put(GameConstant.JCZQ_ZJQ_S, price);
        contestBetPriceMap.put(GameConstant.JCZQ_BQC_P, price);
        contestBetPriceMap.put(GameConstant.JCZQ_BQC_S, price);
    }

    //全集，具体彩种需要具体区分
    public static final String F_0 = "0"; //固定投注方式
    public static final String F_1X1 = "1_1"; //单关
    public static final String F_2X1 = "2_1"; // 2串1
    public static final String F_3X1 = "3_1"; // 3串1
    public static final String F_3X3 = "3_3"; // 3串3
    public static final String F_3X4 = "3_4"; // 3串4
    public static final String F_4X1 = "4_1"; // 4串1
    public static final String F_4X4 = "4_4"; // 4串4
    public static final String F_4X5 = "4_5"; // 4串5
    public static final String F_4X6 = "4_6"; // 4串6
    public static final String F_4X11 = "4_11"; // 4串11
    public static final String F_5X1 = "5_1"; // 5串1
    public static final String F_5X5 = "5_5"; // 5串5
    public static final String F_5X6 = "5_6"; // 5串6
    public static final String F_5X10 = "5_10"; // 5串10
    public static final String F_5X16 = "5_16"; // 5串16
    public static final String F_5X20 = "5_20"; // 5串20
    public static final String F_5X26 = "5_26"; // 5串26
    public static final String F_6X1 = "6_1"; // 6串1
    public static final String F_6X6 = "6_6"; // 6串10
    public static final String F_6X7 = "6_7"; // 6串7
    public static final String F_6X15 = "6_15"; // 6串15
    public static final String F_6X20 = "6_20"; // 6串20
    public static final String F_6X22 = "6_22"; // 6串22
    public static final String F_6X35 = "6_35"; // 6串35
    public static final String F_6X42 = "6_42"; // 6串42
    public static final String F_6X50 = "6_50"; // 6串50
    public static final String F_6X57 = "6_57"; // 6串57
    public static final String F_7X1 = "7_1"; // 7串1
    public static final String F_7X7 = "7_7"; // 7串7
    public static final String F_7X8 = "7_8"; // 7串8
    public static final String F_7X21 = "7_21"; // 7串21
    public static final String F_7X35 = "7_35"; // 7串35
    public static final String F_7X120 = "7_120"; // 7串120
    public static final String F_8X1 = "8_1"; // 8串1
    public static final String F_8X8 = "8_8"; // 8串8
    public static final String F_8X9 = "8_9"; // 8串9
    public static final String F_8X28 = "8_28"; // 8串28
    public static final String F_8X56 = "8_56"; // 8串56
    public static final String F_8X70 = "8_70"; // 8串70
    public static final String F_8X247 = "8_247"; // 8串247

    public static final Map<String, String> JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP = new HashMap<>();

    static {
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_1X1, "单关");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_2X1, "2串1");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_3X1, "3串1");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_3X3, "3串3");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_3X4, "3串4");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_4X1, "4串1");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_4X4, "4串4");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_4X5, "4串5");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_4X6, "4串6");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_4X11, "4串11");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_5X1, "5串1");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_5X6, "5串6");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_5X5, "5串5");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_5X10, "5串10");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_5X16, "5串16");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_5X20, "5串20");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_5X26, "5串26");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_6X1, "6串1");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_6X6, "6串6");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_6X7, "6串7");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_6X15, "6串15");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_6X20, "6串20");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_6X22, "6串22");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_6X35, "6串35");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_6X42, "6串42");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_6X50, "6串50");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_6X57, "6串57");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_7X1, "7串1");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_7X7, "7串7");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_7X8, "7串8");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_7X21, "7串21");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_7X35, "7串35");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_7X120, "7串120");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_8X1, "8串1");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_8X8, "8串8");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_8X9, "8串9");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_8X28, "8串28");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_8X56, "8串56");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_8X70, "8串70");
        JC_BET_EXTRA_MAPPING_EXTRA_CN_MAP.put(F_8X247, "8串247");
    }

    /**
     * 竞彩玩法中文名
     */
    public final static Map<String, String> JC_PLAY_TYPE_NAME_CN_MAP = new HashMap<String, String>();

    static {
        JC_PLAY_TYPE_NAME_CN_MAP.put(GameConstant.JCZQ_BQC_P, "半全场");
        JC_PLAY_TYPE_NAME_CN_MAP.put(GameConstant.JCZQ_BF_P, "比分");
        JC_PLAY_TYPE_NAME_CN_MAP.put(GameConstant.JCZQ_ZJQ_P, "总进球");
        JC_PLAY_TYPE_NAME_CN_MAP.put(GameConstant.JCZQ_RQSPF_P, "让球胜平负");
        JC_PLAY_TYPE_NAME_CN_MAP.put(GameConstant.JCZQ_SPF_P, "胜平负");
        JC_PLAY_TYPE_NAME_CN_MAP.put(GameConstant.JCLQ_DXF_P, "大小分");
        JC_PLAY_TYPE_NAME_CN_MAP.put(GameConstant.JCLQ_RFSF_P, "让分胜负");
        JC_PLAY_TYPE_NAME_CN_MAP.put(GameConstant.JCLQ_SF_P, "胜负");
        JC_PLAY_TYPE_NAME_CN_MAP.put(GameConstant.JCLQ_SFC_P, "胜分差");
    }

    /**
     * 转换投注号码访问彩球计算奖金范围
     */
    public final static Map<String, String> JCZQ_LOTTERY_NUMBER_TO_CAIQIU = Maps.newHashMap();
    public final static Map<String, String> JCLQ_LOTTERY_NUMBER_TO_CAIQIU = Maps.newHashMap();

    static {
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("3", "001-3");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("1", "001-1");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("0", "001-0");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("10", "005-10");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("20", "005-20");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("21", "005-21");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("30", "005-30");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("31", "005-31");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("32", "005-32");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("40", "005-40");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("41", "005-41");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("42", "005-42");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("50", "005-50");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("51", "005-51");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("52", "005-52");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("90", "005-90");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("00", "005-00");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("11", "005-11");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("22", "005-22");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("33", "005-33");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("99", "005-99");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("01", "005-01");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("02", "005-02");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("12", "005-12");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("03", "005-03");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("13", "005-13");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("23", "005-23");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("04", "005-04");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("14", "005-14");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("24", "005-24");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("05", "005-05");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("15", "005-15");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("25", "005-25");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("09", "005-09");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("100", "003-0");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("101", "003-1");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("102", "003-2");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("103", "003-3");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("104", "003-4");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("105", "003-5");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("106", "003-6");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("107", "003-7");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("1033", "004-33");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("1031", "004-31");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("1030", "004-30");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("1013", "004-13");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("1011", "004-11");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("1010", "004-10");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("1003", "004-03");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("1001", "004-01");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("1000", "004-00");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("10003", "002-3");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("10001", "002-1");
        JCZQ_LOTTERY_NUMBER_TO_CAIQIU.put("10000", "002-0");

        JCLQ_LOTTERY_NUMBER_TO_CAIQIU.put("3", "001-2");
        JCLQ_LOTTERY_NUMBER_TO_CAIQIU.put("0", "001-1");
        /*JCLQ_LOTTERYNUM_2_CAIQIU.put("31", "");
        JCLQ_LOTTERYNUM_2_CAIQIU.put("32", "");
        JCLQ_LOTTERYNUM_2_CAIQIU.put("33", "");
        JCLQ_LOTTERYNUM_2_CAIQIU.put("34", "");
        JCLQ_LOTTERYNUM_2_CAIQIU.put("35", "");
        JCLQ_LOTTERYNUM_2_CAIQIU.put("36", "");
        JCLQ_LOTTERYNUM_2_CAIQIU.put("01", "");
        JCLQ_LOTTERYNUM_2_CAIQIU.put("02", "");
        JCLQ_LOTTERYNUM_2_CAIQIU.put("03", "");
        JCLQ_LOTTERYNUM_2_CAIQIU.put("04", "");
        JCLQ_LOTTERYNUM_2_CAIQIU.put("05", "");
        JCLQ_LOTTERYNUM_2_CAIQIU.put("06", "");*/
        JCLQ_LOTTERY_NUMBER_TO_CAIQIU.put("101", "003-2");
        JCLQ_LOTTERY_NUMBER_TO_CAIQIU.put("102", "003-1");
        JCLQ_LOTTERY_NUMBER_TO_CAIQIU.put("1003", "002-2");
        JCLQ_LOTTERY_NUMBER_TO_CAIQIU.put("1000", "002-1");
    }

    /**
     * 通过betCode获取竞彩玩法中文名
     *
     * @param betCode
     * @return
     */
    public static String getJczqPlayTypeCnByBetCode(String betCode) {
        String playTypeEn = BET_OPTION_MIX_JCGAMEEN_MAP.get(betCode);
        String playTypeCn = JC_PLAY_TYPE_NAME_CN_MAP.get(playTypeEn);
        return playTypeCn == null ? "" : playTypeCn;
    }

    /**
     * 通过betCode获取竞彩玩法中文名，竞彩篮球
     *
     * @param betCode
     * @return
     */
    public static String getJclqPlayTypeCnByBetCode(String betCode) {
        String playTypeEn = BET_OPTION_MIX_JCLQGAMEEN_MAP.get(betCode);
        String playTypeCn = JC_PLAY_TYPE_NAME_CN_MAP.get(playTypeEn);
        return playTypeCn == null ? "" : playTypeCn;
    }

    /**
     * 通过betCode获取竞彩玩法英文名
     *
     * @param betCode
     * @return
     */
    public static String getJczqPlayTypeEnByBetCode(String betCode) {
        String playTypeEn = BET_OPTION_MIX_JCGAMEEN_MAP.get(betCode);
        return playTypeEn == null ? "" : playTypeEn;
    }

    /**
     * 通过betCode获取竞彩玩法英文名，竞彩篮球
     *
     * @param betCode
     * @return
     */
    public static String getJclqPlayTypeEnByBetCode(String betCode) {
        String playTypeEn = BET_OPTION_MIX_JCLQGAMEEN_MAP.get(betCode);
        return playTypeEn == null ? "" : playTypeEn;
    }

    /**
     * 获取在混合过关玩法中一个投注项，在其对应的单一玩法中的投注项的值
     *
     * @param betCode
     * @return
     */
    public static String getOriginalBetCodeInJczqMixp(String betCode) {
        String originalBetCode = BET_OPTION_MAPING_JCZQ_MIX_TO_ORIGINAL.get(betCode);
        return StringUtils.isBlank(originalBetCode) ? betCode : originalBetCode;
    }

    /**
     * 获取在混合过关玩法中一个投注项，在其自己玩法中的投注项的值，竞彩篮球
     *
     * @param betCode
     * @return
     */
    public static String getOriginalBetCodeInJclqMixp(String betCode) {
        String originalBetCode = BET_OPTION_MAPING_JCLQ_MIX_TO_ORIGINAL.get(betCode);
        return StringUtils.isBlank(originalBetCode) ? betCode : originalBetCode;
    }

    /**
     * 获取在单一玩法中一个投注项，对应混合过关中的值
     *
     * @param betCode
     * @return
     */
    public static String getJczqMixpBetCode(String gameEn, String betCode) {
        if (gameEn.equals(GameConstant.JCZQ_ZJQ_P)) {
            return "10" + betCode;
        } else if (gameEn.equals(GameConstant.JCZQ_BQC_P)) {
            return "10" + betCode;
        } else if (gameEn.equals(GameConstant.JCZQ_RQSPF_P)) {
            return "1000" + betCode;
        } else {
            return betCode;
        }
    }

    /**
     * 获取在单一玩法中一个投注项，对应混合过关中的值
     *
     * @param betCode
     * @return
     */
    public static String getJclqMixpBetCode(String gameEn, String betCode) {
        if (gameEn.equals(GameConstant.JCLQ_DXF_P)) {
            return "10" + betCode;
        } else if (gameEn.equals(GameConstant.JCLQ_RFSF_P)) {
            return "100" + betCode;
        } else {
            return betCode;
        }
    }

    static {
        initMatchResultOptionMap();
        initLottResultOptionMap();
        initBetOptionMap();
        initBetPriceMap();
    }

    public final static String[] SFMIXP_RULE_LIST_BET_OPTION =
            {"3", "10000", "10003", "0"};
}
