package com.linglong.lottery_backend.ticket.bean.welfare;

import com.linglong.lottery_backend.ticket.bean.AbstractHighWelfareGame;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.entity.bo.GameBean;
import com.linglong.lottery_backend.ticket.enums.GameEnum;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 陕11选5拆票
 */
@Component(value = "Shx115Game")
public class Shx115Game extends AbstractHighWelfareGame {


    @Override
    public Game getGame() {
        return GameEnum.SHX115.getGame();
    }

    @Override
    public List<GameBean> splitGameBeanList(List<GameBean> userGameBeanList, BigDecimal betTimes) {
        List<GameBean> splitGameBean = new ArrayList<>();
        userGameBeanList.forEach(bean -> {
            while ( bean.getBetTimes().divide(new BigDecimal(20)).doubleValue() > 1){
                GameBean gameBean = bean.cloneGameBean();
                gameBean.setBetTimes(new BigDecimal(20));
                bean.setBetTimes(bean.getBetTimes().subtract(new BigDecimal(20)));
                splitGameBean.add(gameBean);
            }
            splitGameBean.add(bean);
        });
        return splitGameBean;
    }

    @Override
    protected List<GameBean> splitGameBeanListForAmount(List<GameBean> userGameBeanList) {
        return super.splitGameBeanListForAmount(userGameBeanList);
    }
}
