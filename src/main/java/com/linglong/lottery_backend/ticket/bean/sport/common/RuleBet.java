package com.linglong.lottery_backend.ticket.bean.sport.common;

import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashSet;

public class RuleBet extends AbstractRuleBet<String[]> {

    public RuleBet(Collection<String> values, String delimiter) {
        super(values, delimiter);
    }

    public String[] parser(String userInput) {
        if (StringUtils.isBlank(userInput)) {
            throw new IllegalArgumentException("赛事选择信息不能为空");
        }
        if (!userInput.matches(regularExp)) {
            throw new IllegalArgumentException("赛事选择信信息不合法 " + userInput);
        }
        //去除重复
        HashSet<String> hs = new HashSet<String>();
        String[] entries = userInput.split(CommonConstant.COMMON_ESCAPE_STR + DELIMITER);
        for (String entry : entries) {
            hs.add(entry);
        }
        return hs.toArray(new String[0]);
    }
}
