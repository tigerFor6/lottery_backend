package com.linglong.lottery_backend.ticket.bean.sport.football;

import com.linglong.lottery_backend.ticket.enums.GameEnum;
import com.linglong.lottery_backend.ticket.bean.sport.AbstractFixedSportGame;
import com.linglong.lottery_backend.ticket.bean.sport.common.RuleConstant;
import com.linglong.lottery_backend.ticket.entity.Game;
import org.springframework.stereotype.Component;

@Component
public class JczqMixPGame extends AbstractFixedSportGame {
    @Override
    public Game getGame() {
        return GameEnum.JCZQ_MIX_P.getGame();
    }

    @Override
    public int[][][] getRuleTable() {
        return RuleConstant.RULE_TABLE_FOOTBALL_SPF_P;
    }

    @Override
    public String[][] getBetOptionCode() {
        return RuleConstant.CHAR_BET_CODE_FOOTBALL_MIX;
    }

    @Override
    public String getGameEnByBetOption(String betOption) {
        return RuleConstant.BET_OPTION_MIX_JCGAMEEN_MAP.get(betOption);
    }
}
