package com.linglong.lottery_backend.ticket.remote.bjgc.entity.part;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;

/**
 * ticket
 */
@XStreamAlias("ticket")
public class Ticket implements Serializable {

    @XStreamAsAttribute
    @XStreamAlias("ticketid")
    private String ticketId;

    @XStreamAsAttribute
    private Integer status;

    @XStreamAsAttribute
    private String sp;

    @XStreamAsAttribute
    private String srno;

    @XStreamAsAttribute
    private String ckcode;

    @XStreamAsAttribute
    private String pname;

    @XStreamAsAttribute
    private String pcode;

    @XStreamAsAttribute
    private String ptime;

    private Integer gameType;

    public String getTicketId() {
        return ticketId;
    }

    public Ticket setTicketId(String ticketId) {
        this.ticketId = ticketId;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public Ticket setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getSp() {
        return sp;
    }

    public Ticket setSp(String sp) {
        this.sp = sp;
        return this;
    }

    public String getSrno() {
        return srno;
    }

    public Ticket setSrno(String srno) {
        this.srno = srno;
        return this;
    }

    public String getCkcode() {
        return ckcode;
    }

    public Ticket setCkcode(String ckcode) {
        this.ckcode = ckcode;
        return this;
    }

    public String getPname() {
        return pname;
    }

    public Ticket setPname(String pname) {
        this.pname = pname;
        return this;
    }

    public String getPcode() {
        return pcode;
    }

    public Ticket setPcode(String pcode) {
        this.pcode = pcode;
        return this;
    }

    public String getPtime() {
        return ptime;
    }

    public Ticket setPtime(String ptime) {
        this.ptime = ptime;
        return this;
    }

    public Integer getGameType() {
        return gameType;
    }

    public void setGameType(Integer gameType) {
        this.gameType = gameType;
    }
}
