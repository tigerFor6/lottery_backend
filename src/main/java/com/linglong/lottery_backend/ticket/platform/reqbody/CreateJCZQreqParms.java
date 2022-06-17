package com.linglong.lottery_backend.ticket.platform.reqbody;


import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.entity.Game;

import java.util.List;

public interface CreateJCZQreqParms extends CreateReqParms{
    //彩种-足球
    Integer GAME_TYPE = 1;

    StringBuffer createJCZQreqBody(List<TicketDto> tickets, Game game);
}
