package com.linglong.lottery_backend.message.sms.cache;

import com.alibaba.fastjson.JSONObject;
import com.linglong.lottery_backend.message.sms.entity.TblSmsTemple;
import com.linglong.lottery_backend.message.sms.repository.SmsTempleRepository;
import com.linglong.lottery_backend.ticket.cache.GameCache;
import com.linglong.lottery_backend.ticket.cache.PlatformCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/7/13
 */
@Component
@Slf4j
@Order(-1)
public class TblSmsTempleCache {

    @Autowired
    private SmsTempleRepository repository;

    private static RedisTemplate<String, String> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        TblSmsTempleCache.redisTemplate = redisTemplate;
    }

    private TblSmsTempleCache() {
    }

    @PostConstruct
    public void init() {
        log.info("init TblSmsTempleCache");
        refresh();
    }

    public void refresh() {
        List<TblSmsTemple> smsTempleList = repository.findAll();

        JSONObject smsCache = null;
        if (!smsTempleList.isEmpty()) {
            smsCache = new JSONObject();
            for (TblSmsTemple smsTemple : smsTempleList){
                Integer type = smsTemple.getType();
                String temple = smsTemple.getTemple();
                smsCache.put(type.toString(),temple);
            }
        }

        if(smsCache != null)
            redisTemplate.opsForHash().put("LotteryCache", "smsCache", smsCache.toJSONString());
    }

    public static String getSmsTemple(Integer type) {
        return GameCache.getGameJSON("smsCache").getString(type.toString());
    }


}
