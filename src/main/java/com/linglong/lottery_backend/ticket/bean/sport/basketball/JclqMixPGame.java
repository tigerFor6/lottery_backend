package com.linglong.lottery_backend.ticket.bean.sport.basketball;

import com.linglong.lottery_backend.ticket.enums.GameEnum;
import com.linglong.lottery_backend.ticket.bean.sport.AbstractFixedSportGame;
import com.linglong.lottery_backend.ticket.bean.sport.common.RuleConstant;
import com.linglong.lottery_backend.ticket.entity.Game;
import org.springframework.stereotype.Component;

@Component
public class JclqMixPGame extends AbstractFixedSportGame {
    @Override
    public Game getGame() {
        return GameEnum.JCLQ_MIX_P.getGame();
    }

    @Override
    public int[][][] getRuleTable() {
        return RuleConstant.RULE_TABLE_BASKETBALL_VS_P;
    }

    @Override
    public String[][] getBetOptionCode() {
        return RuleConstant.CHAR_BET_CODE_BASKETBALL_MIX;
    }

    @Override
    public String getGameEnByBetOption(String betOption) {
        return RuleConstant.BET_OPTION_MIX_JCLQGAMEEN_MAP.get(betOption);
    }
}
