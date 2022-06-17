package com.linglong.lottery_backend.ticket.remote.bjgc.entity;

import com.linglong.lottery_backend.ticket.remote.bjgc.entity.part.TicketBody;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;

/**
 * hua
 */
@XStreamAlias("message")
public class Message implements Serializable {

    @XStreamAsAttribute
    private String version;

    private Header header;

    private TicketBody body;

    public String getVersion() {
        return version;
    }

    public Message setVersion(String version) {
        this.version = version;
        return this;
    }

    public Header getHeader() {
        return header;
    }

    public Message setHeader(Header header) {
        this.header = header;
        return this;
    }

    public TicketBody getBody() {
        return body;
    }

    public Message setBody(TicketBody body) {
        this.body = body;
        return this;
    }
}
