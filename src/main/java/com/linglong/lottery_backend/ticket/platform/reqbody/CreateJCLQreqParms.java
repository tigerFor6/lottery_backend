package com.linglong.lottery_backend.ticket.platform.reqbody;


import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.entity.Game;

import java.util.List;

public interface CreateJCLQreqParms extends CreateReqParms{

    //彩种-篮球
    Integer GAME_TYPE = 2;

    StringBuffer createJCLQreqBody(List<TicketDto> tickets, Game game);
}
