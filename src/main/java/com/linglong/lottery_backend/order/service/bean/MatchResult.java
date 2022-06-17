package com.linglong.lottery_backend.order.service.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: qihua.li
 * @since: 2019-04-24
 */
@Data
public class MatchResult implements Serializable {
    @JSONField(name = "match_id")
    private Long matchId;
    private List<MatchResultItem> result;
}
