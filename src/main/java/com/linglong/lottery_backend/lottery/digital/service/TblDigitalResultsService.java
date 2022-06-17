package com.linglong.lottery_backend.lottery.digital.service;

import com.linglong.lottery_backend.lottery.digital.entity.TblDigitalResults;

import java.util.List;
import java.util.Map;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/5/21
 */
public interface TblDigitalResultsService {

    /**
     * 根据双色球期数去找当期数据
     * @param period
     * @return
     */

    List<TblDigitalResults> findByPeriod(Integer period);


    List<TblDigitalResults> findByPeriodAndLotteryType(Integer period, Integer lotteryType);

    /**
     * 查询最近一期的双色球数据
     * @return
     */
    Map<String, Object> findRecentTblDigitalResults();

    /**
     * 查询双色球历史开奖数据
     * @return
     */
    Map<String, Object> findHistoryTblDigitalResults(Integer game_type);

    void updateLotteryResultById(String lotteryResult, Long id);
}
