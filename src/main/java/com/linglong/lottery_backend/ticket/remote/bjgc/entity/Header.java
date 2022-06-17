package com.linglong.lottery_backend.ticket.remote.bjgc.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.Serializable;

/**
 *
 */
@XStreamAlias("header")
public class Header implements Serializable {

    private String sid;

    @XStreamAlias("timetag")
    private String timeTag;

    private String digest;

    private int cmd;

    public String getSid() {
        return sid;
    }

    public Header setSid(String sid) {
        this.sid = sid;
        return this;
    }

    public String getTimeTag() {
        return timeTag;
    }

    public Header setTimeTag(String timeTag) {
        this.timeTag = timeTag;
        return this;
    }

    public String getDigest() {
        return digest;
    }

    public Header setDigest(String digest) {
        this.digest = digest;
        return this;
    }

    public int getCmd() {
        return cmd;
    }

    public Header setCmd(int cmd) {
        this.cmd = cmd;
        return this;
    }
}
