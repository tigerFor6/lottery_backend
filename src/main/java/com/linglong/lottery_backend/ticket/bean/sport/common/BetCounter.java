package com.linglong.lottery_backend.ticket.bean.sport.common;

import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.bean.AbstractSportGame;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by ynght on 2019-04-20
 */
public class BetCounter {

    // 竞彩格式：/*3:56|1:87|0:55*/
    // 单场胜平负格式： 56|87|55
    //key有序
    protected Map<String, Integer> countMap;

    //倒序
    Comparator<String> comparator = new Comparator<String>() {
        public int compare(String o1, String o2) {
            return o2.compareTo(o1);
        }
    };

    public BetCounter(RuleBet rule) {
        countMap = new TreeMap<>(comparator);
        for (String option : rule.getValues()) {
            Integer temp = countMap.get(option);
            //不存在此option
            if (temp == null) {
                countMap.put(option, 0);
            }
        }
    }

    public void add(String option, int times) {
        Integer counter = countMap.get(option);
        if (counter == null) {
            throw new IllegalArgumentException("此选项不存在！");
        }
        countMap.put(option, counter.intValue() + times);
    }

    public void addAll(Collection<String> options, int times) {
        for (String option : options) {
            add(option, times);
        }

    }

    public void addAll(String[] options, int times) {
        for (String option : options) {
            this.add(option, times);
        }
    }

    public void addAll(char[] options, int times) {
        for (char option : options) {
            this.add(String.valueOf(option), times);
        }
    }

    public void add(BetCounter betCounter) {
        for (Map.Entry<String, Integer> entry : betCounter.countMap.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }
    }

    protected void put(String option, Integer counter) {
        countMap.put(option, counter);
    }

    //转成数据库格式
    public String formatBetCount() {
        StringBuffer sb = new StringBuffer();
        int size = countMap.size();
        int i = 0;
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            sb.append(entry.getKey()).append(CommonConstant.COMMON_COLON_STR).append(entry.getValue());
            if (i != (size - 1))
                sb.append(CommonConstant.COMMON_VERTICAL_STR);
            i++;
        }
        return sb.toString();
    }

    //转成数据库格式
    public String formatBetCountForJczqsfmixp() {
        StringBuffer sb = new StringBuffer();
        int size = countMap.size();
        int i = 0;
        List<String> JczqsfmixpOpts = Arrays.asList(RuleConstant.SFMIXP_RULE_LIST_BET_OPTION);
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            if (JczqsfmixpOpts.contains(entry.getKey())) {
                sb.append(entry.getKey()).append(CommonConstant.COMMON_COLON_STR).append(entry.getValue());
                if (i != (size - 1))
                    sb.append(CommonConstant.COMMON_VERTICAL_STR);
                i++;
            }
        }
        return sb.toString();
    }

    //将混合过关的转成相应玩法格式
    public String formatBetCountForJczqmixp(AbstractSportGame gi) {
        StringBuffer sb = new StringBuffer();
        List<String> betOptions = gi.getRuleListBetOption();
        int size = betOptions.size();
        int i = 0;
        for (String betOption : betOptions) {
            for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
                if (RuleConstant.getJczqMixpBetCode(gi.getGame().getGameEn(), betOption).equals(entry.getKey())) {
                    sb.append(betOption).append(CommonConstant.COMMON_COLON_STR).append(entry.getValue());
                    if (i != (size - 1))
                        sb.append(CommonConstant.COMMON_VERTICAL_STR);
                    continue;
                }
            }
            i++;
        }
        return sb.toString();
    }

    //为了兼容以前单场胜平负格式，转成单场数据库格式
    public String formatSingleBetCount() {
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            if (i != 0) {
                sb.append(CommonConstant.COMMON_COLON_STR);
            }
            sb.append(entry.getValue());
            i++;
        }
        return sb.toString();
    }

    //从数据库读数据，初始化成对象
    public static BetCounter parser(String counter, RuleBet rule) {
        try {
            BetCounter betCount = new BetCounter(rule);
            if (StringUtils.isBlank(counter))
                return betCount;
            String[] elements = counter.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_VERTICAL_STR);
            calcBetCount(betCount, elements);
            return betCount;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //从数据库读数据，初始化成对象
    //单场格式：      100:50:100 表示 胜:平:负 比例
    public static BetCounter parserSingle(String counter, RuleBet rule) {
        try {
            BetCounter betCount = new BetCounter(rule);
            if (StringUtils.isBlank(counter))
                return betCount;
            String[] elements = counter.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_COLON_STR);
            //如果还是老格式，则转换成新的格式
            //// 竞彩格式：/*3:56|1:87|0:55*/
            // 单场胜平负格式： 56|87|55
            if (elements.length == 3) {
                elements[0] = "3:" + elements[0];
                elements[1] = "1:" + elements[1];
                elements[2] = "0:" + elements[2];
            } else if (elements.length == 2 && !counter.contains("|")) {
                elements[0] = "3:" + elements[0];
                elements[1] = "0:" + elements[1];
            } else {
                elements = counter.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_VERTICAL_STR);
            }
            calcBetCount(betCount, elements);
            return betCount;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //从数据库读数据，初始化成对象
    //单场上下单双格式：      1:2:3:4
    public static BetCounter parserSingleSxds(String counter, RuleBet rule) {
        try {
            BetCounter betCount = new BetCounter(rule);
            if (StringUtils.isBlank(counter))
                return betCount;
            String[] elements = counter.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_COLON_STR);
            if (elements.length == 4) {
                elements[0] = "11:" + elements[0];
                elements[1] = "10:" + elements[1];
                elements[2] = "01:" + elements[2];
                elements[3] = "00:" + elements[3];
            } else {//如果还是老格式，则转换成新的格式
                elements = counter.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_VERTICAL_STR);
            }
            calcBetCount(betCount, elements);
            return betCount;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void calcBetCount(BetCounter betCount, String[] elements) {
        for (String element : elements) {
            String[] entry = element.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_COLON_STR);
            if (betCount.countMap.get(entry[0]) == null)
                throw new IllegalArgumentException("此选项不存在！");
            betCount.put(entry[0], Integer.parseInt(entry[1]));
        }
    }
}
