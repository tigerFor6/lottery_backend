package com.linglong.lottery_backend.ticket.entity;


import java.io.Serializable;
import java.util.Objects;

public class BettingTicketKey implements Serializable {

    private Long id;

    private Long ticketId;

    public BettingTicketKey() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BettingTicketKey that = (BettingTicketKey) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(ticketId, that.ticketId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ticketId);
    }
}
