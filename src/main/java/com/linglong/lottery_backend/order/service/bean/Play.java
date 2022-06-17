package com.linglong.lottery_backend.order.service.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: qihua.li
 * @since: 2019-04-17
 */
@Data
public class Play implements Serializable {
    private String result;
    private String item;
    private String odds;
    @JSONField(name = "is_dan_guan")
    private String isDanGuan;
    private String betResult;
}
