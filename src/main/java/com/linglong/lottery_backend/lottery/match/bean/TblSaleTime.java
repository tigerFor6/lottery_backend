package com.linglong.lottery_backend.lottery.match.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/6/10
 */
@Data
public class TblSaleTime implements Serializable {

    @JSONField
    private String startTime;
    @JSONField
    private String endTime;
}
