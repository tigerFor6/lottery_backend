package com.linglong.lottery_backend.ticket.remote.bjgc.entity.part;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.Serializable;

/**
 *
 */
@XStreamAlias("bonusQuery")
public class BonusQuery implements Serializable {

    Issue issue;

    public Issue getIssue() {
        return issue;
    }

    public BonusQuery setIssue(Issue issue) {
        this.issue = issue;
        return this;
    }
}
