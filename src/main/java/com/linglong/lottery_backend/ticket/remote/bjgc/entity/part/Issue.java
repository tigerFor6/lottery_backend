package com.linglong.lottery_backend.ticket.remote.bjgc.entity.part;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;

/**
 *
 */
@XStreamAlias("issue")
public class Issue implements Serializable {

    @XStreamAsAttribute
    private String name;

    @XStreamAsAttribute
    @XStreamAlias("lottid")
    private String lottId;

    public String getName() {
        return name;
    }

    public Issue setName(String name) {
        this.name = name;
        return this;
    }

    public String getLottId() {
        return lottId;
    }

    public Issue setLottId(String lottId) {
        this.lottId = lottId;
        return this;
    }
}
