package com.linglong.lottery_backend.ticket.cache;

import com.alibaba.fastjson.JSONObject;
import com.linglong.lottery_backend.ticket.entity.Weight;
import com.linglong.lottery_backend.ticket.repository.TblWeightRepository;
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
public class WeightCache {
    @Autowired
    private TblWeightRepository tblWeightRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private WeightCache() {
    }

    @PostConstruct
    public void init() {
        log.info("init GamePlatform");
        refresh();
    }

    public void refresh() {
        List<Weight> weightList = tblWeightRepository.findAll();

        JSONObject weightCache = null;
        if (!weightList.isEmpty()) {
            weightCache = new JSONObject();
            for (Weight weight : weightList) {

                JSONObject weightJSON = weightCache.getJSONObject(weight.getGameType().toString());
                if(weightJSON == null)
                    weightJSON = new JSONObject();

                weightJSON.put(weight.getPlatformId().toString(), weight);
                weightCache.put(weight.getGameType().toString(), weightJSON);

            }
        }
        log.info("refresh " + (weightList == null ? 0 : weightList.size()) + " lottery");

        if(weightCache != null)
            redisTemplate.opsForHash().put("LotteryCache", "weightCache", weightCache.toJSONString());

    }

    public List<Weight> getWeightList(Integer gameId) {
        JSONObject weightCache = getWeigthJSON();
        List<Weight> weights = new ArrayList<>();
        for(String gameType : weightCache.keySet()) {
            if(Integer.valueOf(gameType).equals(gameId)) {
                JSONObject weightJSON = weightCache.getJSONObject(gameType);
                for(String platformId : weightJSON.keySet()) {
                    weights.add(JSONObject.parseObject(weightJSON.getString(platformId), Weight.class));
                }
            }
        }
        return weights;
    }

    public Weight getWeight(Integer gameId, Integer platformId) {
        return JSONObject.parseObject(getWeigthJSON().getJSONObject(gameId.toString()).getString(platformId.toString()), Weight.class);
    }

    public JSONObject getWeigthJSON() {
        HashOperations hashOperations = redisTemplate.opsForHash();
        Object weightCache = hashOperations.get("LotteryCache", "weightCache");
        return JSONObject.parseObject(weightCache.toString());
    }
}
