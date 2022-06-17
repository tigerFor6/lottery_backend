package com.linglong.lottery_backend.order.service.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @Author: qihua.li
 * @since: 2019-04-17
 */
@Data
public class Match implements Serializable {
    @JSONField(name = "match_id")
    private Long matchId;
    @JSONField(name = "match_sn")
    private String matchBoth;
    @JSONField(name = "match_name")
    private String matchName;
    @JSONField(name = "match_issue")
    private String matchIssue;
    private Map<String, PlayDetails> plays;
}
