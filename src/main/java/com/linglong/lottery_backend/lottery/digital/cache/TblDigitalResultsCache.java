package com.linglong.lottery_backend.lottery.digital.cache;


import com.alibaba.fastjson.JSON;
import com.linglong.lottery_backend.lottery.digital.entity.TblDigitalResults;
import com.linglong.lottery_backend.lottery.digital.repository.TblDigitalResultsRepository;
import com.linglong.lottery_backend.lottery.match.cache.TblSaleDateCache;
import com.linglong.lottery_backend.lottery.match.common.Code;
import com.linglong.lottery_backend.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/07/01
 */
    @Component
    @Slf4j
    @Order(-1)
    public class TblDigitalResultsCache {

        @Autowired
        private TblDigitalResultsRepository repository;
        @Autowired
        private RedisTemplate<String, String> redisTemplate;

        @Autowired
        private TblSaleDateCache tblSaleDateCache;

        private TblDigitalResultsCache() {
        }

    @PostConstruct
    public void init() {
        for (int i = 3; i < 8; i++) {
            refresh(i);
        }
    }

        public void refresh(Integer i) {
                TblDigitalResults result = repository.findRecentTblDigitalResults(i);
                if (null != result){
                    String startTime = tblSaleDateCache.getStartTime(i, 0);
                    String dateStr = result.getLotteryTime() + startTime;
                    long seconds= (DateUtil.formatToDate(dateStr,"yyyy-MM-dd HH:mm:ss").getTime() - new Date().getTime())/1000;
                    Calendar car = Calendar.getInstance();
                    car.setTime(DateUtil.formatToDate(dateStr,"yyyy-MM-dd HH:mm:ss"));
                    int w = car.get(Calendar.DAY_OF_WEEK) - 1;
                    if (i == 3){
                        if (w == 2 || w == 0){
                            seconds += (long)2* 24 * 60 * 60L;
                        }else if (w == 4){
                            seconds += (long)3* 24 * 60 * 60L;
                        }
                    }else if (i == 5){
                        if (w == 1 || w == 6){
                            seconds += (long)2* 24 * 60 * 60L;
                        }else if (w == 3){
                            seconds += (long)3* 24 * 60 * 60L;
                        }
                    }else if (i == 6 || i == 7){
                        seconds += (long)1* 24 * 60 * 60L;
                    }
                    if (seconds >0){
                        log.info("更新gameType:"+i+"最近一期结果reids中的数据");
                        redisTemplate.opsForValue().set(Code.Redis.TBL_DIGITAL_KEY + i, JSON.toJSONString(result), seconds, TimeUnit.SECONDS);
                    }
                }
        }

}
