package com.linglong.lottery_backend.ticket.bean;

import com.linglong.lottery_backend.order.entity.Order;
import com.linglong.lottery_backend.ticket.entity.bo.GameBean;

import java.math.BigDecimal;
import java.util.List;

/**
 * 排列三
 */
public abstract class AbstractPl5Game extends AbstractGame{

    public abstract List<GameBean> assemblyGameBean(AbstractPl5Game awg, Order order);

    @Override
    public List<GameBean> splitGameBeanList(List<GameBean> userGameBeanList, BigDecimal betTimes) {
        return userGameBeanList;
    }
}
