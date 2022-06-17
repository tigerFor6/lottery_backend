package com.linglong.lottery_backend.order.service.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: qihua.li
 * @since: 2019-04-24
 */
@Data
public class MatchResultItem implements Serializable {
    private String code;
    private String result;
    private String odds;
    @JsonProperty("is_danguan")
    private String isDanGuan;
}
