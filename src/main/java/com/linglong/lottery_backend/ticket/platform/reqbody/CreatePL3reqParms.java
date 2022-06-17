package com.linglong.lottery_backend.ticket.platform.reqbody;


import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.entity.Game;

import java.util.List;

public interface CreatePL3reqParms extends CreateReqParms{
    //彩种-排列3
    Integer GAME_TYPE = 6;

    StringBuffer createPL3reqBody(List<TicketDto> tickets, Game game);
}
