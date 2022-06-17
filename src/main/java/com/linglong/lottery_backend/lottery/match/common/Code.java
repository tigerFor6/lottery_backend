package com.linglong.lottery_backend.lottery.match.common;

public interface Code {

    /**
     * @description: redis键前缀规则
     * @author: ZhiYao.Zhang
     * @param:
     * @return:
     * @create: 2019-04-16
     **/

    public static class Redis {

        //比赛列表
        public static final String TBL_MATCH_LIST_KEY = "TBL_MATCH_LIST_KEY:";
        //热门比赛列表
        public static final String TBL_MATCH_HOT_LIST_KEY = "TBL_MATCH_HOT_LIST_KEY:";
        //比赛赔率列表
        public static final String TBL_MATCH_ODDS_LIST_KEY = "TBL_MATCH_ODDS_LIST_KEY:";
        //比赛赔率列表(防缓存击穿)
        public static final String TBL_TX_MATCH_ODDS_LIST_KEY = "TBL_TX_MATCH_ODDS_LIST_KEY:";
        //比赛
        public static final String TBL_MATCH_KEY = "TBL_MATCH_KEY:";
        //热门比赛
        public static final String TBL_MATCH_HOT_KEY = "TBL_MATCH_HOT_KEY:";
        //联赛球队排名
        public static final String TBL_SEASON_TEAM_KEY = "TBL_SEASON_TEAM_KEY:";
        //联赛球队排名（防缓存击穿）
        public static final String TBL_TX_SEASON_TEAM_KEY = "TBL_TX_SEASON_TEAM_KEY:";
        //联赛列表
        public static final String TBL_SEASON_KEY = "TBL_SEASON_KEY:";
        //联赛列表
        public static final String TBL_SEASON_ROUND_KEY = "TBL_SEASON_ROUND_KEY:";
        //球队列表
        public static final String TBL_TEAM_KEY = "TBL_TEAM_KEY:";
        //赔率列表
        public static final String TBL_MATCH_ODDS_KEY = "TBL_MATCH_ODDS_KEY:";
        //赔率列表(防缓存击穿)
        public static final String TBL_TX_MATCH_ODDS_KEY = "TBL_TX_MATCH_ODDS_KEY:";
        //数字彩最新期次
        public static final String TBL_DIGITAL_KEY = "TBL_DIGITAL_KEY:";
        //收银台金额配置金额
        public static final String TBL_CASHIER_AMOUNT_KEY = "TBL_CASHIER_AMOUNT_KEY:";
        //收银台充值方式配置
        public static final String TBL_PAYMENT_CONFIGURATION_KEY = "TBL_PAYMENT_CONFIGURATION_KEY:";
        //收银台充值接口配置
        public static final String TBL_PAYAPI_CONFIGURATION_KEY = "TBL_PAYAPI_CONFIGURATION_KEY:";
        //跑马灯中奖信息
        public static final String TBL_WIN_KEY = "TBL_WIN_KEY:";
        //平台规定的每日提现次数
        public static final String CASH_NUM_KEY = "CASH_NUM_KEY";
        //用户每日提现次数
        public static final String USER_CASH_NUM_KEY = "USER_CASH_NUM_KEY:";
        //热门比赛列表 文案
        public static final String HOT_MATCH_LIST_LABLE = "HOT_MATCH_LIST_LABLE";
        //用户每日中奖次数
        public static final String REWARD_NUM_KEY = "REWARD_NUM_KEY:";
    }

    /**
     * @description: 订单记录中交易类常量
     * @author: ZhiYao.Zhang
     * @param:
     * @return:
     * @create: 2019-04-16
     **/

    public static class Trade {
        //交易类型
        //充值
        public static final String RECHARGE = "1";
        //提现
        public static final String CASH_WITHDRAWAL = "2";
        //返奖
        public static final String RETURN_AWARD = "3";
        //投注
        public static final String BET = "4";
        //退款
        public static final String REFUND = "5";

        //交易状态
        //充值中
        public static final String RECHARGEING = "1";
        //充值失败
        public static final String RECHARGE_FAIL = "7";
        //充值成功
        public static final String RECHARGE_SUCCESS = "2";

        //先异常，后补单为充值成功
        public static final String RECHARGE_AFTER_SUCCESS = "22";

        //提现中
        public static final String CASH_WITHDRAWALING = "3";
        //提现审核完成
        public static final String CASH_WITHDRAWAL_CHECK = "17";
        //提现失败
        public static final String CASH_WITHDRAWAL_FAIL = "4";
        //提现成功
        public static final String CASH_WITHDRAWAL_SUCCESS = "8";

        //投注中
        public static final String BETTING = "5";
        //投注失败
        public static final String THIRD_BET_FAIL = "12";
        //投注成功
        public static final String THIRD_BET_SUCCESS = "6";

        //返奖中
        public static final String RETURNAWARD = "9";
        //返奖失败
        public static final String RETURNAWARD_FAIL = "11";
        //返奖成功
        public static final String RETURNAWARD_SUCCESS = "10";

        //退款中
        public static final String REFUNDING = "13";
        //退款失败
        public static final String REFUNDING_FAIL = "14";
        //退款成功
        public static final String REFUNDING_SUCCESS = "15";
        //退款成功
        public static final String WITHDRAWAL_REFUNDING_SUCCESS = "16";

    }

    /**
     * @description: 规定分页时每页显示的数据条数
     * @author: ZhiYao.Zhang
     * @param:
     * @return:
     * @create: 2019-04-18
     **/
    public static class SetPageSize {
        public static final Integer PAGESIZE = 9;
    }

}
