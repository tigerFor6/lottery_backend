package com.linglong.lottery_backend.ticket.bean;

import com.linglong.lottery_backend.common.error.BusinessException;
import com.linglong.lottery_backend.ticket.cache.GameCache;
import com.linglong.lottery_backend.utils.CommonUtil;
import com.linglong.lottery_backend.utils.SpringContextHolder;

/**
 * Created by ynght on 2019-04-20
 */
public class GameFactory {
    private static GameFactory instance = new GameFactory();

    private GameFactory() {
    }

    public static GameFactory getInstance() {
        return instance;
    }

    public AbstractGame getGameBean(String gameEn) {
        AbstractGame gi = SpringContextHolder.getBean(CommonUtil.initBeanName(gameEn) + "Game");
        if (gi == null) {
            throw new BusinessException("游戏工厂中的对象不存在:" + gameEn);
        }
        return gi;
    }

    public AbstractGame getGameBean(Integer gameType) {
       // System.out.println(GameCache.getGame(gameType).getGameEn());
        return getGameBean(GameCache.getGame(gameType).getGameEn());
    }
}
