package com.linglong.lottery_backend.ticket.bean.welfare;

import com.linglong.lottery_backend.ticket.bean.AbstractBigHappyGame;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.enums.GameEnum;
import org.springframework.stereotype.Component;

@Component(value = "DLTGame")
public class DltGame extends AbstractBigHappyGame {

    @Override
    public Game getGame() {
        return GameEnum.DLT.getGame();
    }
}
