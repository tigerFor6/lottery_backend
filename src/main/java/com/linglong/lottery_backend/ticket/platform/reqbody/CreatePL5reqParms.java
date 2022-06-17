package com.linglong.lottery_backend.ticket.platform.reqbody;


import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.entity.Game;

import java.util.List;

public interface CreatePL5reqParms extends CreateReqParms{
    //彩种-排列5
    Integer GAME_TYPE = 7;

    StringBuffer createPL5reqBody(List<TicketDto> tickets, Game game);
}
