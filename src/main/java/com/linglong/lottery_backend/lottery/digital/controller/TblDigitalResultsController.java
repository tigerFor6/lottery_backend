package com.linglong.lottery_backend.lottery.digital.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linglong.lottery_backend.lottery.digital.entity.TblWinData;
import com.linglong.lottery_backend.lottery.digital.repository.TblWinDataRepository;
import com.linglong.lottery_backend.lottery.digital.cache.TblDigitalResultsCache;
import com.linglong.lottery_backend.lottery.digital.service.TblDigitalResultsService;
import com.linglong.lottery_backend.lottery.match.common.Code;
import com.linglong.lottery_backend.order.model.Result;
import com.linglong.lottery_backend.order.model.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/5/22
 */
@RestController
@RequestMapping(value = "/api/match")
public class TblDigitalResultsController {

    @Autowired
    TblDigitalResultsService tblDigitalResultsService;

    @Autowired
    TblDigitalResultsCache tblDigitalResultsCache;

    @Autowired
    TblWinDataRepository tblWinDataRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 获取最近一期数字彩信息
     */
    @GetMapping("/recentOne")
    public Result queryOrderList() {
        Map<String, Object> resultMap = tblDigitalResultsService.findRecentTblDigitalResults();
        return new Result(StatusCode.OK.getCode(), "查询最近一期信息", resultMap);
    }

    /**
     * 获取数字彩历史开奖信息
     */
    @GetMapping("/queryHistoryOrderList")
    public Result queryHistoryOrderList(@RequestParam("game_type") Integer game_type) {
        Map<String, Object> resultMap = tblDigitalResultsService.findHistoryTblDigitalResults(game_type);
        if (game_type == 3){
            return new Result(StatusCode.OK.getCode(), "查询双色球历史开奖信息", resultMap);
        }else if (game_type == 4){
            return new Result(StatusCode.OK.getCode(), "查询陕11选5历史开奖信息", resultMap);
        }else if (game_type == 5){
            return new Result(StatusCode.OK.getCode(), "查询大乐透历史开奖信息", resultMap);
        }else if (game_type == 6){
            return new Result(StatusCode.OK.getCode(), "查询排列三历史开奖信息", resultMap);
        } else{
            return new Result(StatusCode.OK.getCode(), "查询竞彩历史开奖信息", resultMap);
        }
    }

    @GetMapping("/insertWinData")
    public Result insertWinData(@RequestParam("game_type") Integer game_type,@RequestParam("number") String  number,@RequestParam("amount") BigDecimal amount){
        for (int i = 0; i < 5; i++) {
            Random random = new Random();
            String numStr = number;
            for (int j = 0; j < 4; j++) {
                //生成0~9 随机数
                numStr += random.nextInt(9);
            }
            TblWinData winData = new TblWinData();
            winData.setPhoneNumber(numStr);
            winData.setGameType(game_type);
            winData.setAmount(amount);
            winData.setCreatedTime(new Date());
            winData.setUpdatedTime(new Date());
            tblWinDataRepository.saveAndFlush(winData);
        }
        return new Result(StatusCode.OK.getCode(), "插入数据成功", null);
    }

    @GetMapping("/queryWinData")
    public  Result queryWinData() {
        List<TblWinData> tblWinDataList = new ArrayList<TblWinData>();
        String s = redisTemplate.opsForValue().get(Code.Redis.TBL_WIN_KEY);
        if (StringUtils.isEmpty(s)){
            tblWinDataList = tblWinDataRepository.findAll();
            redisTemplate.opsForValue().set(Code.Redis.TBL_WIN_KEY, JSON.toJSONString(tblWinDataList));
        }else{
            tblWinDataList = JSON.parseArray(s, TblWinData.class);
        }
        int size = tblWinDataList.size();
        Random random = new Random();

        for(int i = 0; i < size; i++) {
            // 获取随机位置
            int randomPos = random.nextInt(size);
            // 当前元素与随机元素交换
            TblWinData temp = tblWinDataList.get(i);
            tblWinDataList.set(i, tblWinDataList.get(randomPos));
            tblWinDataList.set(randomPos, temp);
        }
        return new Result(StatusCode.OK.getCode(), "查询中奖数据信息", tblWinDataList);
    }

    @GetMapping("refreshDigitalResultsCache")
    public Result refreshDigitalResultsCache() {
        for (int i = 3; i < 8; i++) {
            tblDigitalResultsCache.refresh(i);
        }
        return new Result(StatusCode.OK.getCode(), "刷新数字彩近期结果redis完毕", null);
    }

    @GetMapping("refreshTblWinDataCache")
    public Result refreshTblWinDataCache() {
        List<TblWinData> tblWinDataList = tblWinDataRepository.findAll();
        redisTemplate.opsForValue().set(Code.Redis.TBL_WIN_KEY, JSON.toJSONString(tblWinDataList));
        return new Result(StatusCode.OK.getCode(), "刷新跑马灯中奖数据redis完毕", null);
    }
}
