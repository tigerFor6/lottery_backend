package com.linglong.lottery_backend.lottery.match.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/6/10
 */
@Data
public class TblSale implements Serializable {

    private List<TblSaleTime> tblSaleTimes;
}
