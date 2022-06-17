package com.linglong.lottery_backend.ticket.remote.bjgc.entity.part;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.Serializable;
import java.util.List;

/**
 * 通知结果集
 */
@XStreamAlias("body")
public class TicketBody implements Serializable {

    private List<Ticket> tickets;

    private BonusQuery bonusQuery;

    public BonusQuery getBonusQuery() {
        return bonusQuery;
    }

    public TicketBody setBonusQuery(BonusQuery bonusQuery) {
        this.bonusQuery = bonusQuery;
        return this;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public TicketBody setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
        return this;
    }
}
