package com.linglong.lottery_backend.ticket.bean.sport.common;


import com.linglong.lottery_backend.common.error.BusinessException;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
public class RuleBetScheme {

    protected final RuleID ruleID;
    protected final RuleBet ruleBet;
    protected final RuleDan ruleDan;

    protected static String SEPARATOR_INNER;
    protected static String SEPARATOR_OUTER;

    public RuleBetScheme(RuleID ruleID, RuleBet ruleBet, RuleDan ruleDan, String separatorInner, String
            separatorOuter) {
        this.ruleID = ruleID;
        this.ruleBet = ruleBet;
        this.ruleDan = ruleDan;
        SEPARATOR_INNER = separatorInner;
        SEPARATOR_OUTER = separatorOuter;
    }

    public SetBet parser(String userInput, boolean dan) {
        try {
            EntryBet ent;
            Collection<EntryBet> c;

            if (StringUtils.isBlank(userInput)) {
                throw new IllegalArgumentException("投注信息不能为空");
            }
            String[] bets = userInput.split(CommonConstant.COMMON_ESCAPE_STR + SEPARATOR_OUTER);
            c = new ArrayList<>();
            //20170001:3-3.34:0 20170001:3-3.34:0
            for (String bet : bets) {
                String[] parts = bet.split(CommonConstant.COMMON_ESCAPE_STR + SEPARATOR_INNER);
                if (dan && parts.length != RuleConstant.LENGTH_INNER_BET) {
                    throw new IllegalArgumentException("投注信息由赛事编号，投注信息和胆拖信息组成");
                }
                if (!dan && parts.length != RuleConstant.LENGTH_INNER_BET_NO_DAN) {
                    throw new IllegalArgumentException("拆单后的投注信息由赛事编号和投注信息组成");
                }
                // TODO: 17-7-17 原来的代码是没有考虑票上的赔率的
                StringBuffer sb = new StringBuffer();
                String[] split = parts[1].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.POUND_SPLIT_STR);
                for (String str : split) {
                    String[] temp = str.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_DASH_STR);
                    sb.append(temp[0]).append(CommonConstant.POUND_SPLIT_STR);
                }
                sb = sb.deleteCharAt(sb.length() - 1);
                if (dan) {
//                    ent = new EntryBet(ruleID.parser(parts[0]), ruleBet.parser(parts[1]), ruleDan.parser(parts[2]));
                    ent = new EntryBet(ruleID.parser(parts[0]), ruleBet.parser(sb.toString()), ruleDan.parser
                            (parts[2]));
                } else {
                    ent = new EntryBet(ruleID.parser(parts[0]), ruleBet.parser(sb.toString()), false);
                }
                c.add(ent);
            }
            return new SetBet(c);
        } catch (IllegalArgumentException e) {
            log.info("RuleBetScheme parser error", e);
            throw new BusinessException("投注信息格式错误：" + e.getMessage());
        }
    }

    public boolean addID(String id) {
        return ruleID.add(id);
    }

    public boolean removeID(String id) {
        return ruleID.remove(id);
    }

    public boolean containsID(String id) {
        return ruleID.contains(id);
    }

    public static void main(String[] args) {

        List<String> listID = new ArrayList<String>();
        listID.add("20110519001");
        listID.add("20110519002");
        listID.add("20110519003");
        listID.add("20110519004");
        listID.add("20110519005");
        listID.add("20110519006");
        listID.add("20110519007");
        listID.add("20110519008");
        listID.add("20110519009");
        listID.add("20110519010");
        listID.add("20110519011");
        listID.add("20110519012");
        List<String> listBetType = new ArrayList<String>();
        listBetType.add("01");
        listBetType.add("02");
        listBetType.add("03");
        listBetType.add("04");
        List<String> listDanType = new ArrayList<String>();
        listDanType.add("0");
        listDanType.add("1");

        String input1 =
                "20110519001:01.02.04:0" +
                        " 20110519002:03.04:1" +
                        " 20110519005:03:0" +
                        " 20110519006:01.04:1" +
                        " 20110519007:01.04:1" +
                        " 20110519008:04:1" +
                        " 20110519009:03:1" +
                        " 20110519010:01.03:0";

        String input2 =
                "20110519001:01.02.04" +
                        " 20110519002:03.04" +
                        " 20110519005:03" +
                        " 20110519006:01.04" +
                        " 20110519007:01.04" +
                        " 20110519008:04" +
                        " 20110519009:03" +
                        " 20110519010:01.03";


        RuleID ruleId = new RuleID(listID);
        RuleBet ruleBet = new RuleBet(listBetType, ".");
        RuleDan ruleDan = new RuleDan(listDanType);

        RuleBetScheme ruleBetScheme = new RuleBetScheme(ruleId, ruleBet, ruleDan, ":", " ");

        System.out.println(ruleBetScheme.parser(input2, false).toString());
    }
}
