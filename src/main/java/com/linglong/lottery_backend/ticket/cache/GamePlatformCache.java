package com.linglong.lottery_backend.ticket.cache;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linglong.lottery_backend.ticket.entity.GamePlatform;
import com.linglong.lottery_backend.ticket.repository.TblGamePlatformRepository;
import com.linglong.lottery_backend.utils.CommonUtil;
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
 * Created by ynght on 2019-04-20
 */
@Component
@Slf4j
@Order(-1)
public class GamePlatformCache {

    @Autowired
    private TblGamePlatformRepository tblGamePlatformRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private GamePlatformCache() {
    }

    @PostConstruct
    public void init() {
        log.info("init GamePlatform");
        refresh();
    }

    public void refresh() {
        List<GamePlatform> gamePlatformList = tblGamePlatformRepository.findAll();
        JSONObject platformGameCache = null;

        if (!gamePlatformList.isEmpty()) {
            platformGameCache = new JSONObject();
            for (GamePlatform gamePlatform : gamePlatformList) {

                JSONObject gamePlatformCache = platformGameCache.getJSONObject(gamePlatform.getPlatformId().toString());
                if(gamePlatformCache == null)
                    gamePlatformCache = new JSONObject();

                gamePlatformCache.put(gamePlatform.getGameType().toString(), gamePlatform);
                platformGameCache.put(gamePlatform.getPlatformId().toString(), gamePlatformCache);
            }

        }

        if(platformGameCache != null){
            redisTemplate.opsForHash().put("LotteryCache", "platformGameCache", platformGameCache.toJSONString());
        }
        log.info("refresh " + (gamePlatformList == null ? 0 : gamePlatformList.size()) + " gamePlatform");
    }

    public GamePlatform getGamePlatform(Integer gameId, Integer platformId) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        Object platFormCache = hashOperations.get("LotteryCache", "platformGameCache");
        if(platFormCache == null) {
            log.error("找不到GamePlatform");
        }
        JSONObject platformCache = JSONObject.parseObject(platFormCache.toString());
        return JSONObject.parseObject(platformCache.getJSONObject(platformId.toString()).getString(gameId.toString()), GamePlatform.class);
    }

}
