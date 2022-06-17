package com.linglong.lottery_backend.ticket.remote.bjgc.entity.part;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询结果集
 */
@XStreamAlias("response")
public class Response {

    @XStreamAsAttribute
    private String code;
    @XStreamAsAttribute
    private String message;

    private List<Ticket> tickets;

    public String getCode() {
        return code;
    }

    public Response setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Response setMessage(String message) {
        this.message = message;
        return this;
    }

    public List<Ticket> getTickets() {
        if (null ==  this.tickets){
            return new ArrayList<>();
        }
        return tickets;
    }

    public Response setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
        return this;
    }
}
