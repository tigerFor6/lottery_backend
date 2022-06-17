package com.linglong.lottery_backend.ticket.bean.sport.football;

import com.linglong.lottery_backend.ticket.enums.GameEnum;
import com.linglong.lottery_backend.ticket.bean.sport.AbstractFixedSportGame;
import com.linglong.lottery_backend.ticket.bean.sport.common.RuleConstant;
import com.linglong.lottery_backend.ticket.entity.Game;
import org.springframework.stereotype.Component;

@Component
public class JczqBqcPGame extends AbstractFixedSportGame {
    @Override
    public Game getGame() {
        return GameEnum.JCZQ_BQC_P.getGame();
    }

    @Override
    public int[][][] getRuleTable() {
        return RuleConstant.RULE_TABLE_FOOTBALL_HALF_P;
    }

    @Override
    public String[][] getBetOptionCode() {
        return RuleConstant.CHAR_BET_CODE_FOOTBALL_HALF;
    }
}
