package com.linglong.lottery_backend.ticket.platform.reqbody;


import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.entity.Game;

import java.util.List;

public interface CreateSHX115reqParms extends CreateReqParms{
    //彩种-陕11选5
    Integer GAME_TYPE = 4;

    StringBuffer createSHX115reqBody(List<TicketDto> tickets, Game game);
}
