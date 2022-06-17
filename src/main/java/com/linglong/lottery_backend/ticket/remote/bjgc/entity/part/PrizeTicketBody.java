package com.linglong.lottery_backend.ticket.remote.bjgc.entity.part;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

/**
 * 通知结果集
 */
@XStreamAlias("body")
public class PrizeTicketBody {

    private List<PrizeTicket> tickets;

    public List<PrizeTicket> getTickets() {
        return tickets;
    }

    public PrizeTicketBody setTickets(List<PrizeTicket> tickets) {
        this.tickets = tickets;
        return this;
    }
}
