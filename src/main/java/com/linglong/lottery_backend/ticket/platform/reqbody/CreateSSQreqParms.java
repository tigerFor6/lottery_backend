package com.linglong.lottery_backend.ticket.platform.reqbody;


import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.entity.Game;

import java.util.List;

public interface CreateSSQreqParms extends CreateReqParms{

    //彩种-双色球
    Integer GAME_TYPE = 3;

    StringBuffer createSSQreqBody(List<TicketDto> tickets, Game game);
}
