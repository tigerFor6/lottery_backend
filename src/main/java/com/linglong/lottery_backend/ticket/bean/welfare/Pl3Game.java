package com.linglong.lottery_backend.ticket.bean.welfare;

import com.linglong.lottery_backend.ticket.bean.AbstractPl3Game;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.entity.bo.GameBean;
import com.linglong.lottery_backend.ticket.enums.GameEnum;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 排列3拆票
 */
@Component(value = "PL3Game")
public class Pl3Game extends AbstractPl3Game {

    @Override
    public Game getGame() {
        return GameEnum.PL3.getGame();
    }

    @Override
    public List<GameBean> splitGameBeanList(List<GameBean> userGameBeanList, BigDecimal betTimes) {
        BigDecimal singePrice = new BigDecimal(2);
        List<GameBean> splitGameBean = new ArrayList<>();
        userGameBeanList.forEach(bean -> {
            while ( bean.getBetTimes().divide(new BigDecimal(50)).doubleValue() > 1){
                GameBean gameBean = bean.cloneGameBean();
                gameBean.setBetTimes(new BigDecimal(50));
                gameBean.setPrice(PRICE_PER_STAKE.multiply(BigDecimal.valueOf(50)).multiply(BigDecimal.valueOf(gameBean.getBetNumber())));
                bean.setBetTimes(bean.getBetTimes().subtract(new BigDecimal(50)));
                bean.setPrice(bean.getPrice().subtract(gameBean.getPrice()));
                splitGameBean.add(gameBean);
            }
            splitGameBean.add(bean);
        });

        List<GameBean> splitFinalGameBean = new ArrayList<>();
        splitGameBean.forEach(bean -> {

            BigDecimal limitPrise = new BigDecimal(20000);

            while(bean.getPrice().doubleValue() > limitPrise.doubleValue()) {
                GameBean gameBean = bean.cloneGameBean();
                BigDecimal betDecimal = new BigDecimal(bean.getBetNumber());
                BigDecimal newBetTimes = limitPrise.divide(singePrice, 0 , BigDecimal.ROUND_DOWN).divide(betDecimal, 0,BigDecimal.ROUND_DOWN);
                if(newBetTimes.doubleValue() <= 0) {
                    break;
                }
                gameBean.setBetTimes(newBetTimes);
                    gameBean.setPrice(newBetTimes.multiply(betDecimal).multiply(singePrice));
                    splitFinalGameBean.add(gameBean);

                    bean.setBetTimes(bean.getBetTimes().subtract(newBetTimes));
                    bean.setPrice(bean.getPrice().subtract(gameBean.getPrice()));
                }
                splitFinalGameBean.add(bean);
        });

        return splitFinalGameBean;
    }

    @Override
    protected List<GameBean> splitGameBeanListForAmount(List<GameBean> userGameBeanList) {
        return super.splitGameBeanListForAmount(userGameBeanList);
    }
}
