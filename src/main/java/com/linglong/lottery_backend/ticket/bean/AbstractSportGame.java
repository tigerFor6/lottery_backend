package com.linglong.lottery_backend.ticket.bean;

import com.linglong.lottery_backend.common.error.BusinessException;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.constant.WordConstant;
import com.linglong.lottery_backend.ticket.bean.sport.common.*;
import com.linglong.lottery_backend.ticket.entity.bo.GameBean;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by ynght on 2019-04-20
 */
@Slf4j
public abstract class AbstractSportGame extends AbstractGame {
    public static final int BET_SIZE_SINGLE_MODE = 15;
    public static final int BET_SIZE_PASS_MODE = 15;
    public static final int BET_BET_TIMES_PER_TICKET = 99;
    public static final BigDecimal BET_SUM_SCHEME_MAX = new BigDecimal("2000000");
    public static final BigDecimal BET_SUM_TICKET_MAX = new BigDecimal(20000);


    protected RuleBetScheme ruleBetScheme;
    protected RulePassScheme rulePassScheme;

    protected RuleID ruleId;
    protected RuleBet ruleBet;

    public RuleBet getRuleBet() {
        return ruleBet;
    }

    public void setRuleBet(RuleBet ruleBet) {
        this.ruleBet = ruleBet;
    }

    protected RuleDan ruleDan;

    public AbstractSportGame() {
        ruleId = new RuleID(getRuleIdRegexp(), getRuleIdLength());
        ruleBet = new RuleBet(getRuleListBetOption(), CommonConstant.POUND_SPLIT_STR);
        ruleDan = new RuleDan(getRuleListDantuo());

        ruleBetScheme = new RuleBetScheme(ruleId, ruleBet, ruleDan, CommonConstant.COMMON_COLON_STR,
                CommonConstant.SPACE_SPLIT_STR);
        rulePassScheme = new RulePassScheme(getRuleTable(), CommonConstant.COMMON_SPLIT_STR, CommonConstant
                .COMMON_VERTICAL_STR);
    }

    protected abstract String getRuleIdRegexp();

    protected abstract int getRuleIdLength();

    /**
     * 得到预定义的所有胆拖的可能值集合,如果是单关，则不允许选胆
     *
     * @return
     */
    public abstract String[][] getDanOptionCode();

    /**
     * 得到预定义的投注串关规则Table
     *
     * @return
     */
    public abstract int[][][] getRuleTable();

    /**
     * 拆分完成后投注号应是无胆拖的，只包含多串1或者单关(1串1)的。如果单关的投注号不需要拆单，只需要验证合法性和计算注数
     *
     * @param sourceBean
     * @param betTimes
     * @param targetBean
     * @return
     */
    public Map<String, BetCounter> split(GameBean sourceBean, int betTimes, List<GameBean> targetBean, boolean
            splitToOne) {
        return this.split(sourceBean, betTimes, targetBean, splitToOne, true, null);
    }

    public List<String> getRuleListBetOption() {
        return getRowList(getBetOptionCode(), 0);
    }

    /**
     * 得到tab里第0行的值，放进List返回
     *
     * @param tab
     * @param n
     * @return
     */
    private static List<String> getRowList(String[][] tab, int n) {
        List<String> list = new ArrayList<String>();
        try {
            for (String[] t : tab) {
                list.add(t[n]);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }
        return list;
    }

    /**
     * 得到预定义的投注选项的Table
     *
     * @return
     */
    public abstract String[][] getBetOptionCode();

    /***
     * @return
     */
    public boolean isSplitToOne() {
        return true;
    }

    public Map<String, BetCounter> split(GameBean sourceBean, int betTimes, List<GameBean> targetBean, boolean
            splitToOne, boolean containsDan, Map<String, BetCounter> totalCounterMap) {
        betTimes = betTimes * sourceBean.getBetTimes().intValue();
        try {// 拆单
            UserBetScheme user;
            user = new UserBetScheme(ruleBetScheme, rulePassScheme);
            user.parser(sourceBean.getLotteryNumber(), sourceBean.getExtra(), containsDan);
            List<BetVo> betVoList = user.splitSecene(splitToOne);// 拆单
            //原始号码
            //201304243009:3:0 201304243010:0:0 201304243011:3.1:0 201304243012:3:0 2_1 3_1
            //betvolist
            //[201304243009:3:0 201304243011:3.1:0 过关方式 2_1 注数 2,
            //201304243010:0:0 201304243011:3.1:0 过关方式 2_1 注数 2,
            //201304243011:3.1:0 201304243012:3:0 过关方式 2_1 注数 2,
            //201304243009:3:0 201304243010:0:0 过关方式 2_1 注数 1,
            //201304243009:3:0 201304243012:3:0 过关方式 2_1 注数 1,
            //201304243010:0:0 201304243012:3:0 过关方式 2_1 注数 1,
            //201304243009:3:0 201304243010:0:0 201304243011:3.1:0 过关方式 3_1 注数 2,
            //201304243009:3:0 201304243011:3.1:0 201304243012:3:0 过关方式 3_1 注数 2,
            //201304243010:0:0 201304243011:3.1:0 201304243012:3:0 过关方式 3_1 注数 2,
            //201304243009:3:0 201304243010:0:0 201304243012:3:0 过关方式 3_1 注数 1]
            boolean isBetRealMix = isMixPlayForSplit(betVoList);
            if (isBetRealMix) {
                betVoList = rebuildBetVoList(betVoList);
            }
            List<GameBean> tempTarget = new ArrayList<>();
            while (!betVoList.isEmpty()) {
                List<BetVo> betVoListTemp = new ArrayList<>();
                betVoListTemp.addAll(betVoList);
                BetVo betVo = betVoList.remove(0);
                GameBean tempBean = new GameBean();
                String stake = betVo.formatLotteryNumber();
                tempBean.setLotteryNumber(stake);
                tempBean.setExtra(betVo.formatPassWay());
                tempBean.setBetNumber(betVo.getBetTimes());
                tempBean.setPlayType(betVo.isBetWay() ? WordConstant.SINGLE : WordConstant.MULTIPLE);
                tempBean.setPrice(getPrice(tempBean));
                tempBean.setBetTimes(sourceBean.getBetTimes());
                BigDecimal times = betTimes > BET_BET_TIMES_PER_TICKET ? new BigDecimal(BET_BET_TIMES_PER_TICKET) :
                        new BigDecimal(betTimes);
                if (isMixPlayForSplit(betVoListTemp) && isBetOptionMix(betVo.getBets())) {
                    //混合过关一个投注号码一场比赛不能有多个玩法 进行拆
                    List<BetVo> splitVo = betVo.split(true, this);
                    if (!splitVo.isEmpty()) {
                        betVoList.addAll(splitVo);
                    } else {
                        tempTarget.add(tempBean);
                    }
                } else if (tempBean.getPrice().multiply(times).compareTo(BET_SUM_TICKET_MAX) > 0) {
                    //串1单票的二次拆分，倍数限制拆分在下单后续统一进行
                    List<BetVo> splitVo = betVo.split(false, this);
                    if (!splitVo.isEmpty()) {
                        betVoList.addAll(splitVo);
                    } else {
                        tempTarget.add(tempBean);
                    }
                } else {
                    tempTarget.add(tempBean);
                }
            }
            targetBean.addAll(tempTarget);
            return user.calculateBetCounter(betTimes, totalCounterMap);// 计算本站投数，按照未拆之前的单算
        } catch (Exception e) {
            log.error(getGame().getGameEn() + "拆单失败！e:" + e, e);
            return null;
        }
    }

    public List<String> getRuleListDantuo() {
        return getRowList(getDanOptionCode(), 0);
    }

    /**
     * 主要是在混合过关中用到，通过投注号码获得游戏的英文名称
     *
     * @param betOption
     * @return
     */
    public String getGameEnByBetOption(String betOption) {
        return null;
    }

    private boolean isMixPlayForSplit(List<BetVo> betVolist) {
        Set<String> playSet = new HashSet<String>();
        for (BetVo bet : betVolist) {
            for (EntryBet entryBet : bet.getBets()) {
                for (int i = 0; i < entryBet.getBets().length; i++) {
                    playSet.add(getGameEnByBetOption(entryBet.getBets()[i]));
                    //edited by mwlv 减少计算次数
                    if (playSet.size() > 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private List<BetVo> rebuildBetVoList(List<BetVo> betVolist) {
        //注数 单复式都得改
        //201304243009:3:0 201304243010:0:0 201304243011:3.1:0 过关方式 3_1 注数 2,
        //201304243009:3:0 201304243011:3.1:0 201304243012:3:0 过关方式 3_1 注数 2,
        //201304243010:0:0 201304243011:3.1:0 201304243012:3:0 过关方式 3_1 注数 2,
        //201304243009:3:0 201304243010:0:0 201304243012:3:0 过关方式 3_1 注数 1]

        //[206009214376:40.05.1010.11:1 258205127929:1003.1001.40.05.15.50:1 262506096684:0.05.105.25:1
        // 295409275883:22.99:0 296501255438:40.90.104:0 过关方式 5_1 注数 576]
        List<BetVo> newBetVolist = new ArrayList<BetVo>();
        for (BetVo bet : betVolist) {
            //198910301312:3.21.20.52.23.1011.11:0 207402053942:20.41.107.11.03:1 208712184185:0.04.104.24:0
            // 220401065906:107.104.02:0 288803114595:0.09.41.40.04:0 过关方式 5_1 注数 2100
            BetVo newBet = bet;
            //2串1
            int pass = newBet.getPassWay().getM();
            int betCount = newBet.getBetTimes();
            //TODO 每场比赛的投注选项个数
            //[3, 4, 5, 5, 7]
            List<Integer> betOptionCount = optionCountInBet(newBet.getBets());
            int j = 0;
            for (EntryBet entryBet : newBet.getBets()) {
                //201007305957:0.51.90.106.33.13.100:0 226311217312:1033.30.23.1013.11:1 290901071899:3.10.99:1
                // 293108293252:09.04.23.105.15.1011.02.50:0
                //201007305957:0.106.100               226311217312:                     290901071899:3
                // 293108293252:105

                //201007305957:0.106.100
                int thisBetOptionCount = entryBet.getBets().length;//5

                //过关限制 投注选项 序号列表
                List<Integer> spportList = spportGameOptionIndex(pass, entryBet.getBets());//
                if (spportList.size() == thisBetOptionCount) {
                    continue;
                } else if (spportList.size() == 0) {
                    betCount = 0;
                    break;
                }
                betCount -= (getUnruleBetCount(betOptionCount, j) * (thisBetOptionCount - spportList.size()));

                int count = betOptionCount.get(j) - thisBetOptionCount + spportList.size();
                betOptionCount.set(j, count);
                List<String> l = new ArrayList<>();
                for (Integer un : spportList) {
                    l.add(entryBet.getBets()[un]);
                }
                //bets
                String[] strings = new String[l.size()];
                entryBet.setBets(l.toArray(strings));
                j++;
            }
            if (betCount > 0) {
                newBet.setBetTimes(betCount);
                newBetVolist.add(newBet);
            }
        }
        return newBetVolist;
    }

    public int getLotteryNumberPerTicket(String playType, String extra) {
        return 1;
    }

    private Integer getUnruleBetCount(List<Integer> betOptionCount, int index) {
        Integer count = 1;
        for (int i = 0; i < betOptionCount.size(); i++) {
            if (i != index) {
                count *= betOptionCount.get(i);
            }
        }
        return count;
    }

    private boolean isBetOptionMix(Collection<EntryBet> entryBetList) {
        for (EntryBet entryBet : entryBetList) {
            Set<String> playSet = new HashSet<String>();
            for (int i = 0; i < entryBet.getBets().length; i++) {
                playSet.add(getGameEnByBetOption(entryBet.getBets()[i]));
            }
            if (playSet.size() > 1) {
                return true;
            }
        }
        return false;
    }

    private List<Integer> optionCountInBet(Collection<EntryBet> bet) {
        List<Integer> list = new ArrayList<Integer>();
        for (EntryBet entryBet : bet) {
            list.add(entryBet.getBets().length);
        }
        return list;
    }

    private List<Integer> spportGameOptionIndex(int pass, String[] options) {
        List<Integer> spportOption = new ArrayList<Integer>();
        for (int i = 0; i < options.length; i++) {
            int maxPass = RuleConstant.BET_MAX_PASSWAY_JCGAME_MAP.get(getGameEnByBetOption(options[i]));
            if (maxPass >= pass) {
                spportOption.add(i);
            }
        }
        return spportOption;
    }
}
