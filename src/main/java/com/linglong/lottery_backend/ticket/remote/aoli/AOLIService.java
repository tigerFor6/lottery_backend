package com.linglong.lottery_backend.ticket.remote.aoli;

import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author  Yinxiong
 * @DATE 2019年5月20日
 */
public interface AOLIService {

    /**
     * aoli票商 - 出票主动查询
     * @param ticketsId
     * @return
     */
    List<Message> callTicketStatus(Map<Integer, StringBuffer> ticketsId);

    List<Message> resSHX115resultData(Integer gameType, JSONObject resResult);

    List<Message> resJCZQLQSSQresultData(Integer gameType, JSONObject resResult);
}
