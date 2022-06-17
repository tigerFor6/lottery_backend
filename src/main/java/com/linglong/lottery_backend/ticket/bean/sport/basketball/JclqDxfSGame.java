package com.linglong.lottery_backend.ticket.bean.sport.basketball;

import com.linglong.lottery_backend.ticket.enums.GameEnum;
import com.linglong.lottery_backend.ticket.bean.sport.AbstractFixedSportGame;
import com.linglong.lottery_backend.ticket.bean.sport.common.RuleConstant;
import com.linglong.lottery_backend.ticket.entity.Game;
import org.springframework.stereotype.Component;

@Component
public class JclqDxfSGame extends AbstractFixedSportGame {
    @Override
    public Game getGame() {
        return GameEnum.JCLQ_DXF_S.getGame();
    }

    @Override
    public int[][][] getRuleTable() {
        return RuleConstant.RULE_TABLE_BASKETBALL_BS_S;
    }

    @Override
    public String[][] getBetOptionCode() {
        return RuleConstant.CHAR_BET_CODE_BASKETBALL_BS;
    }
}
