package com.linglong.lottery_backend.ticket.cache;

import com.alibaba.fastjson.JSONObject;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.repository.TblGameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by ynght on 2019-04-20
 */
@Component
@Slf4j
@Order(-1)
public class GameCache {

    @Autowired
    private TblGameRepository tblGameRepository;

    private static RedisTemplate<String, String> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        GameCache.redisTemplate = redisTemplate;
    }

    private GameCache() {
    }

    @PostConstruct
    public void init() {
        log.info("init Game");
        refresh();
    }

    public void refresh() {
        List<Game> gameList = tblGameRepository.findAll();

        JSONObject gameCache = null;
        JSONObject gameEnCache = null;
        if (!gameList.isEmpty()) {
            gameCache = new JSONObject();
            gameEnCache = new JSONObject();

            for (Game game : gameList) {
                gameCache.put(game.getGameType().toString(), game);
                gameEnCache.put(game.getGameEn(), game);
            }
        }
        log.info("gameMap: {}", gameCache.toJSONString());
        log.info("gameEnMap: {}", gameEnCache.toJSONString());

        if(gameCache != null)
            redisTemplate.opsForHash().put("LotteryCache", "gameCache", gameCache.toJSONString());
        if(gameEnCache != null)
            redisTemplate.opsForHash().put("LotteryCache", "gameEnCache", gameEnCache.toJSONString());

    }

    public static Game getGame(Integer gameType) {
        return JSONObject.parseObject(getGameJSON("gameCache").getString(gameType.toString()), Game.class);
    }

    public static Game getGame(String gameEn) {
        return JSONObject.parseObject(getGameJSON("gameEnCache").getString(gameEn), Game.class);
    }

    public static JSONObject getGameJSON(String game) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        Object platFormCache = hashOperations.get("LotteryCache", game);
        return JSONObject.parseObject(platFormCache.toString());
    }

}
