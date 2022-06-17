package com.linglong.lottery_backend.order.service.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: qihua.li
 * @since: 2019-04-17
 */
@Data
public class PlayDetails implements Serializable {
    @JSONField(name = "is_dan_guan")
    private String isDanGuan;
    private String score;
    private String handicap;
    private List<Play> items;
}
