package com.linglong.lottery_backend.ticket.platform.reqbody;


import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.entity.Game;

import java.util.List;

public interface CreateDLTreqParms extends CreateReqParms{

    //彩种-大乐透
    Integer GAME_TYPE = 5;

    StringBuffer createDLTreqBody(List<TicketDto> tickets, Game game);
}
