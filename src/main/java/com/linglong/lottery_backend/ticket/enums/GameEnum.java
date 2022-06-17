package com.linglong.lottery_backend.ticket.enums;

import com.linglong.lottery_backend.ticket.cache.GameCache;
import com.linglong.lottery_backend.ticket.constant.GameConstant;
import com.linglong.lottery_backend.ticket.entity.Game;

/**
 * Created by ynght on 2019-04-20
 */
public enum GameEnum {

    //竞彩
    JCZQ_MIX_P(GameConstant.JCZQ_MIX_P),
    JCZQ_RQSPF_P(GameConstant.JCZQ_RQSPF_P), JCZQ_RQSPF_S(GameConstant.JCZQ_RQSPF_S),
    JCZQ_SPF_P(GameConstant.JCZQ_SPF_P), JCZQ_SPF_S(GameConstant.JCZQ_SPF_S),
    JCZQ_BF_P(GameConstant.JCZQ_BF_P), JCZQ_BF_S(GameConstant.JCZQ_BF_S),
    JCZQ_BQC_P(GameConstant.JCZQ_BQC_P), JCZQ_BQC_S(GameConstant.JCZQ_BQC_S),
    JCZQ_ZJQ_P(GameConstant.JCZQ_ZJQ_P), JCZQ_ZJQ_S(GameConstant.JCZQ_ZJQ_S),
    JCLQ_MIX_P(GameConstant.JCLQ_MIX_P),
    JCLQ_RFSF_P(GameConstant.JCLQ_RFSF_P), JCLQ_RFSF_S(GameConstant.JCLQ_RFSF_S),
    JCLQ_SF_P(GameConstant.JCLQ_SF_P), JCLQ_SF_S(GameConstant.JCLQ_SF_S),
    JCLQ_DXF_P(GameConstant.JCLQ_DXF_P), JCLQ_DXF_S(GameConstant.JCLQ_DXF_S),
    JCLQ_SFC_P(GameConstant.JCLQ_SFC_P), JCLQ_SFC_S(GameConstant.JCLQ_SFC_S),


    //数字彩
    FC_SSQ(GameConstant.FC_SSQ),
    SHX115(GameConstant.SHX115),
    DLT(GameConstant.DLT),
    PL3(GameConstant.PL3),
    PL5(GameConstant.PL5);

    private String gameEn;

    GameEnum(String gameEn) {
        this.gameEn = gameEn;
    }

    public static GameEnum getGameEnumByEn(String gameEn) {
        for (GameEnum gameEnum : values()) {
            if (gameEnum.getGameEn().equals(gameEn)) {
                return gameEnum;
            }
        }
        return null;
    }

    public static GameEnum getGameEnumById(Integer gameType) {
        String gameEn = GameCache.getGame(gameType).getGameEn();
        return getGameEnumByEn(gameEn);
    }

    public String getGameEn() {
        return gameEn;
    }

    public Game getGame() {
        return GameCache.getGame(gameEn);
    }
}
