package com.linglong.lottery_backend.ticket.platform.reqbody;

import com.linglong.lottery_backend.ticket.bean.TicketDto;

import java.util.List;

public interface CreateReqParms{

    StringBuffer createParms(List<TicketDto> tickets);
}
