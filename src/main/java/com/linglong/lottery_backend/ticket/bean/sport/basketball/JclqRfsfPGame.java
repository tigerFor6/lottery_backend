package com.linglong.lottery_backend.ticket.bean.sport.basketball;

import com.linglong.lottery_backend.ticket.enums.GameEnum;
import com.linglong.lottery_backend.ticket.bean.sport.AbstractFixedSportGame;
import com.linglong.lottery_backend.ticket.bean.sport.common.RuleConstant;
import com.linglong.lottery_backend.ticket.entity.Game;
import org.springframework.stereotype.Component;

@Component
public class JclqRfsfPGame extends AbstractFixedSportGame {
    @Override
    public Game getGame() {
        return GameEnum.JCLQ_RFSF_P.getGame();
    }

    @Override
    public int[][][] getRuleTable() {
        return RuleConstant.RULE_TABLE_BASKETBALL_LETVS_P;
    }

    @Override
    public String[][] getBetOptionCode() {
        return RuleConstant.CHAR_BET_CODE_BASKETBALL_LETVS;
    }
}
