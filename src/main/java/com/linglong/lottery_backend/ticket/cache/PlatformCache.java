package com.linglong.lottery_backend.ticket.cache;

import com.alibaba.fastjson.JSONObject;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.entity.Platform;
import com.linglong.lottery_backend.ticket.repository.TblPlatformRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ynght on 2019-04-26
 */
@Component
@Order(-1)
@Slf4j
public class PlatformCache {

    @Autowired
    private TblPlatformRepository tblPlatformRepository;

    private static RedisTemplate<String, String> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        PlatformCache.redisTemplate = redisTemplate;
    }

    private PlatformCache() {
    }

    @PostConstruct
    public void init() {
        log.info("init Platform");
        refresh();
    }

    public void refresh() {
        List<Platform> platformList = tblPlatformRepository.findAll();

        JSONObject platformCache = null;
        JSONObject platformEnCache = null;
        if (!platformList.isEmpty()) {
            platformCache = new JSONObject();
            platformEnCache = new JSONObject();

            for (Platform platform : platformList) {
                platformCache.put(platform.getPlatformId().toString(), platform);
                platformEnCache.put(platform.getPlatformEn(), platform);
            }
        }
        log.info("refresh " + (platformList == null ? 0 : platformList.size()) + " Platform");

        if(platformCache != null)
            redisTemplate.opsForHash().put("LotteryCache", "platformCache", platformCache.toJSONString());
        if(platformEnCache != null)
            redisTemplate.opsForHash().put("LotteryCache", "platformEnCache", platformEnCache.toJSONString());
    }

    public static Platform getPlatform(Integer platformId) {
        return JSONObject.parseObject(getPlatformJSON("platformCache").getString(platformId.toString()), Platform.class);
    }

    public static Platform getPlatform(String platformEn) {
        return JSONObject.parseObject(getPlatformJSON("platformEnCache").getString(platformEn), Platform.class);
    }

    public static JSONObject getPlatformJSON(String platform) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        Object platFormCache = hashOperations.get("LotteryCache", platform);
        return JSONObject.parseObject(platFormCache.toString());
    }
}
