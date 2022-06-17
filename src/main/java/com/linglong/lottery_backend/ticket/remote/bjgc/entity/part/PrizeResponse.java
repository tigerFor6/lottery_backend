package com.linglong.lottery_backend.ticket.remote.bjgc.entity.part;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询结果集
 */
@XStreamAlias("response")
public class PrizeResponse {

    @XStreamAsAttribute
    private String code;
    @XStreamAsAttribute
    private String message;

    private List<PrizeTicket> tickets;

    public String getCode() {
        return code;
    }

    public PrizeResponse setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public PrizeResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public List<PrizeTicket> getTickets() {
        if (null == tickets){
            return new ArrayList<>();
        }
        return tickets;
    }

    public PrizeResponse setTickets(List<PrizeTicket> tickets) {
        this.tickets = tickets;
        return this;
    }
}
