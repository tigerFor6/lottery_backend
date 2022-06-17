package com.linglong.lottery_backend.ticket.bean.sport;

import com.linglong.lottery_backend.ticket.bean.AbstractSportGame;
import com.linglong.lottery_backend.ticket.bean.sport.common.RuleConstant;

/**
 * Created by ynght on 2019-04-20
 */
public abstract class AbstractFixedSportGame extends AbstractSportGame {
    @Override
    public String getRuleIdRegexp() {
        return RuleConstant.FORMAT_RULE_ID_REGEXP;
    }

    @Override
    public int getRuleIdLength() {
        return RuleConstant.FORMAT_RULE_ID_LENGTH;
    }

    @Override
    public String[][] getDanOptionCode() {
        return RuleConstant.CHAR_BET_CODE_DANTUO;
    }
}
