package com.linglong.lottery_backend.lottery.digital.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.linglong.lottery_backend.lottery.digital.cache.TblDigitalResultsCache;
import com.linglong.lottery_backend.lottery.digital.repository.TblDigitalResultsRepository;
import com.linglong.lottery_backend.lottery.digital.service.TblDigitalResultsService;
import com.linglong.lottery_backend.lottery.digital.entity.TblDigitalResults;
import com.linglong.lottery_backend.lottery.match.cache.TblSaleDateCache;
import com.linglong.lottery_backend.lottery.match.common.Code;
import com.linglong.lottery_backend.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/5/21
 */
@Service
public class TblDigitalResultsServiceImpl implements TblDigitalResultsService {

    @Autowired
    private TblDigitalResultsRepository repository;

    @Autowired
    private TblSaleDateCache tblSaleDateCache;

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private TblDigitalResultsCache tblDigitalResultsCache;

    @Override
    public List<TblDigitalResults> findByPeriod(Integer period) {
        return repository.findByPeriod(period);
    }

    @Override
    public List<TblDigitalResults> findByPeriodAndLotteryType(Integer period, Integer lotteryType) {
        return repository.findByPeriodAndLotteryType(period,lotteryType);
    }

    @Override
    public Map<String, Object> findRecentTblDigitalResults() {
        Map<String, Object> digitalResultsMap = new HashMap<>();
        Map<Integer,Map<String, Object>> results = new HashMap<>();
        Map<Integer, TblDigitalResults> tblDigitalResultsCacheMap = new ConcurrentHashMap<>();
        for (int i = 3; i < 8; i++) {
            String s = redisTemplate.opsForValue().get(Code.Redis.TBL_DIGITAL_KEY + i);
            if (StringUtils.isEmpty(s)){
                TblDigitalResults result = repository.findRecentTblDigitalResults(i);
                if (null != result){
                    tblDigitalResultsCacheMap.put(i,result);
                    tblDigitalResultsCache.refresh(i);
                }
            }else {
                TblDigitalResults r = JSONObject.parseObject(s, TblDigitalResults.class);
                tblDigitalResultsCacheMap.put(i,r);
            }
        }
        for(Integer game_type : tblDigitalResultsCacheMap.keySet()){
            Map<String, Object> resultMap = new HashMap<>();
            TblDigitalResults tblDigitalResults = tblDigitalResultsCacheMap.get(game_type);
            if (null == tblDigitalResults)
                break;
            Calendar car = Calendar.getInstance();
            Date nowDate = new Date();
            car.setTime(nowDate);
            int w = car.get(Calendar.DAY_OF_WEEK) - 1;
            String startTime = game_type.equals(Integer.valueOf(4)) ? null : tblSaleDateCache.getStartTime(game_type, 0);
            if (game_type == 3){
                //双色球
                String dateStr = tblDigitalResults.getLotteryTime() + startTime;
                Date line = DateUtil.formatToDate(dateStr,"yyyy-MM-dd HH:mm:ss");
                //截止下注时间，最新的一期在数据库中可能没有数据，用当前时间计算截止开奖时间
                Date deadLine = dateUtil.getDeadLine();
                long oddTime = deadLine.getTime() - new Date().getTime(); // 获得两个时间的毫秒时间差异
                //剩余时间
                resultMap.put("oddTime", oddTime);
                if(line.before(new Date())){
                    //期数
                    resultMap.put("code", tblDigitalResults.getPeriod()+1);
                    //2019-06-18 19:25  --70  2019-06-20 19:20 ---71    2019-06-20 19:50 ---72
                    String sTime = DateUtil.formatDate(nowDate,1) + startTime;
                    if (w == 2 || w == 4 || w == 0){
                        if (nowDate.after(DateUtil.formatToDate(sTime,DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS)) && (!DateUtil.formatDate(nowDate,1).equals(tblDigitalResults.getLotteryTime()))){
                            resultMap.put("code", tblDigitalResults.getPeriod()+2);
                        }
                    }
                }else{
                    //期数,这种情况就是找寻的最新的一期双色球开奖结果中的，截止下单时间在当前时间前，但是赛果却出现在数字赛果结果表中，属于异常情况
                    resultMap.put("code", null);
                }
                //奖池
                resultMap.put("poolMoney", tblDigitalResults.getPrizePoolAmount());
            }else if (game_type == 4){
                //陕11选5
                Integer code = tblDigitalResults.getPeriod();
                Date deadLine = DateUtil.formatToDate(tblDigitalResults.getLotteryTime(),DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS);
                long oddTime = deadLine.getTime() - new Date().getTime(); // 获得两个时间的毫秒时间差异
                if (deadLine.before(new Date())){
                    //计算生成下一期的数据传给前端
                    code++;
                    oddTime += (long)20 * 60000L;
                }
                //期数
                resultMap.put("code", code);
                //剩余时间
                resultMap.put("oddTime", oddTime);
            }else if (game_type == 5){
                //大乐透
                String dateStr = tblDigitalResults.getLotteryTime() + startTime;
                Date line = DateUtil.formatToDate(dateStr,"yyyy-MM-dd HH:mm:ss");
                Date deadLine = dateUtil.getSuperLottoDeadLine();
                long oddTime = deadLine.getTime() - new Date().getTime(); // 获得两个时间的毫秒时间差异
                //剩余时间
                resultMap.put("oddTime", oddTime);
                if(line.before(new Date())){
                    //期数
                    resultMap.put("code", tblDigitalResults.getPeriod()+1);
                    String sTime = DateUtil.formatDate(nowDate,1) + startTime;
                    if (w == 1 || w == 3 || w == 6){
                        if (nowDate.after(DateUtil.formatToDate(sTime,DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS)) && (!DateUtil.formatDate(nowDate,1).equals(tblDigitalResults.getLotteryTime()))){
                            resultMap.put("code", tblDigitalResults.getPeriod()+2);
                        }
                    }
                }else{
                    resultMap.put("code", null);
                }
                //奖池
                resultMap.put("poolMoney", tblDigitalResults.getPrizePoolAmount());
            }else if (game_type == 6 || game_type == 7){
                //排列三
                String dateStr = tblDigitalResults.getLotteryTime() + startTime;
                Date line = DateUtil.formatToDate(dateStr,"yyyy-MM-dd HH:mm:ss");
                Date deadLine = dateUtil.getRankThreeDeadLine();
                long oddTime = deadLine.getTime() - new Date().getTime(); // 获得两个时间的毫秒时间差异
                //剩余时间
                resultMap.put("oddTime", oddTime);
                if(line.before(new Date())){
                    //期数
                    resultMap.put("code", tblDigitalResults.getPeriod()+1);
                    String sTime = DateUtil.formatDate(nowDate,1) + startTime;
                    if (nowDate.after(DateUtil.formatToDate(sTime,DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS)) && (!DateUtil.formatDate(nowDate,1).equals(tblDigitalResults.getLotteryTime()))){
                        resultMap.put("code", tblDigitalResults.getPeriod()+2);
                    }
                }else{
                    resultMap.put("code", null);
                }
                //奖池
                resultMap.put("poolMoney", tblDigitalResults.getPrizePoolAmount());
            }
            results.put(game_type,resultMap);
        }
        digitalResultsMap.put("results",results);
        return digitalResultsMap;
    }

    @Override
    public Map<String, Object> findHistoryTblDigitalResults(Integer game_type) {
        Map<String, Object> historyResultMap = new HashMap<>();
        ArrayList<Map<String, Object>> codes = new ArrayList<>();
        List<TblDigitalResults> tblDigitalResultsList = repository.findHistoryTblDigitalResults(game_type);
        for (TblDigitalResults tblDigitalResults : tblDigitalResultsList){
            Map<String, Object> resultMap = new HashMap<>();
            if (game_type == 3 || game_type == 5){
                //双色球，大乐透
                //期数
                resultMap.put("code", tblDigitalResults.getPeriod());
                String result = tblDigitalResults.getLotteryResult();
                String[] str = result.split(":");
                if (str.length>1){
                    //红球
                    resultMap.put("redBall", str[0]);
                    //蓝球
                    resultMap.put("blueBall", str[1]);
                }
                codes.add(resultMap);
            }else if (game_type == 4 || game_type == 6 || game_type == 7){
                //陕11选5,,排列三
                //期数
                resultMap.put("code", tblDigitalResults.getPeriod());
                String numbers = tblDigitalResults.getLotteryResult();
                resultMap.put("numbers",numbers);
                codes.add(resultMap);
            }

        }
        historyResultMap.put("codes",codes);
        return historyResultMap;
    }

    @Override
    public void updateLotteryResultById(String lotteryResult, Long id) {
        repository.updateLotteryResultById(lotteryResult, id);
    }

}
