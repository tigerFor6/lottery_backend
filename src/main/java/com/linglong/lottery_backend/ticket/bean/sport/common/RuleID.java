package com.linglong.lottery_backend.ticket.bean.sport.common;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class RuleID extends AbstractRuleBet<String> {
    private int length;

    public RuleID(String regexp, int len) {//无目标值，只做简单校验
        super();
        length = len;
        regularExp = regexp;
    }

    public RuleID(Collection<String> values) {//有所有可能的值，做校验
        super(values, 1);
    }

    public String parser(String userInput) {
        if (StringUtils.isBlank(userInput)) {
            throw new IllegalArgumentException("赛事编号不能为空");
        }
        if (!userInput.matches(regularExp)) {
            throw new IllegalArgumentException("赛事编号不合法或者不在当前列表里");
        }
        return userInput;
    }

    public int valueLength() {
        int length = super.valueLength();
        if (length <= 0) {
            return this.length;
        } else {
            return length;
        }
    }
}
