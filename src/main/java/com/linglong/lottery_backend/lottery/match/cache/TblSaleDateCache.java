package com.linglong.lottery_backend.lottery.match.cache;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.linglong.lottery_backend.lottery.match.entity.TblSaleDate;
import com.linglong.lottery_backend.lottery.match.repository.TblSaleDateRepository;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/6/21
 */
@Component
@Slf4j
@Order(-1)
public class TblSaleDateCache {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private TblSaleDateRepository tblSaleDateRepository;

    private TblSaleDateCache() {
    }

    @PostConstruct
    public void init() {
        log.info("init TblSaleDateCache");
        refresh();
    }

    public void refresh() {
        List<TblSaleDate> tblSaleDateList = tblSaleDateRepository.findAll();
        JSONObject saleDateCache = null;
        if (!tblSaleDateList.isEmpty()) {
            saleDateCache = new JSONObject();

            for (TblSaleDate tblSaleDate : tblSaleDateList) {

                JSONObject saleDateJSON = saleDateCache.getJSONObject(tblSaleDate.getGameType().toString());
                if(saleDateJSON == null)
                    saleDateJSON = new JSONObject();

                saleDateJSON.put(tblSaleDate.getSaleDate().toString(), tblSaleDate);
                saleDateCache.put(tblSaleDate.getGameType().toString(), saleDateJSON);
            }
        }

        if(saleDateCache != null){
            redisTemplate.opsForHash().put("LotteryCache", "SaleDateCache", saleDateCache.toJSONString());
        }
    }

    public List<TblSaleDate> getTblSaleDateList(Integer gameType) {
        JSONObject cacheJSON = getSaleJSON();
        return JSONArray.parseArray(cacheJSON.getJSONArray(gameType.toString()).toJSONString(), TblSaleDate.class);
    }
    public TblSaleDate getTblSaleDate(Integer gameType, Integer saleDate) {
        try{
            return JSONObject.parseObject(getSaleJSON().getJSONObject(gameType.toString()).getString(saleDate.toString()), TblSaleDate.class);
        }catch (Exception e) {
            return null;
        }
    }

    public JSONObject getSaleJSON() {
        Object cacheObj = redisTemplate.opsForHash().get("LotteryCache", "SaleDateCache");
        return JSONObject.parseObject(cacheObj.toString());
    }

    public String getStartTime(Integer gameType, Integer saleDate) {
        TblSaleDate tblSaleDate = getTblSaleDate(gameType, saleDate);
        String startTime = "";
        if (null != tblSaleDate){
            String saleTime = tblSaleDate.getSaleTime();
            String[] saleArr = saleTime.split(CommonConstant.COMMON_AT_STR);
            String[] time = saleArr[1].split(CommonConstant.SPACE_SPLIT_STR);
            startTime = time[0];
            return " "+startTime;
        }
        return startTime;
    }

    public String getEndTime(Integer gameType, Integer saleDate) {
        TblSaleDate tblSaleDate = getTblSaleDate(gameType, saleDate);
        String endTime = "";
        if (null != tblSaleDate){
            String saleTime = tblSaleDate.getSaleTime();
            String[] saleArr = saleTime.split(CommonConstant.COMMON_AT_STR);
            String[] time = saleArr[1].split(CommonConstant.SPACE_SPLIT_STR);
            endTime = time[1];
            return " "+endTime;
        }
        return endTime;
    }
}
