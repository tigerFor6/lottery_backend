package com.linglong.lottery_backend.ticket.bean.sport.common;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class RuleDan extends AbstractRuleBet<Boolean> {

    public RuleDan(Collection<String> values) {
        super(values, 1);
    }

    public Boolean parser(String userInput) {
        if (StringUtils.isBlank(userInput)) {
            throw new IllegalArgumentException("胆拖信息不能为空");
        }
        if (!userInput.matches(regularExp)) {
            throw new IllegalArgumentException("胆拖信息不合法");
        }
        return userInput.equalsIgnoreCase(RuleConstant.CHAR_FORMAT_DAN) ? true : false;
    }
}