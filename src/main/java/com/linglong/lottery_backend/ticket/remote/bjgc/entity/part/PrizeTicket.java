package com.linglong.lottery_backend.ticket.remote.bjgc.entity.part;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * prize ticket
 */
@XStreamAlias("ticket")
public class PrizeTicket {

    @XStreamAsAttribute
    @XStreamAlias("tid")
    private String ticketId;

    @XStreamAsAttribute
    private String bonusAmount;

    @XStreamAsAttribute
    private String fixBonusAmount;

    @XStreamAsAttribute
    private String bonusLevel;

    public String getTicketId() {
        return ticketId;
    }

    public PrizeTicket setTicketId(String ticketId) {
        this.ticketId = ticketId;
        return this;
    }

    public String getBonusAmount() {
        return bonusAmount;
    }

    public PrizeTicket setBonusAmount(String bonusAmount) {
        this.bonusAmount = bonusAmount;
        return this;
    }

    public String getFixBonusAmount() {
        return fixBonusAmount;
    }

    public PrizeTicket setFixBonusAmount(String fixBonusAmount) {
        this.fixBonusAmount = fixBonusAmount;
        return this;
    }

    public String getBonusLevel() {
        return bonusLevel;
    }

    public PrizeTicket setBonusLevel(String bonusLevel) {
        this.bonusLevel = bonusLevel;
        return this;
    }
}
