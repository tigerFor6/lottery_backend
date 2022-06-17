package com.linglong.lottery_backend.lottery.match.service.redis;

import com.alibaba.fastjson.JSON;
import com.linglong.lottery_backend.user.account.controller.AccountController;
import com.linglong.lottery_backend.lottery.match.common.Code;
import com.linglong.lottery_backend.lottery.match.entity.*;
import com.linglong.lottery_backend.lottery.match.service.MatchRelevantRedisService;
import com.linglong.lottery_backend.lottery.match.service.TblMatchService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class MatchRelevantRedisServiceImpl implements MatchRelevantRedisService {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    TblMatchService tblMatchService;

    @Override
    public void putTblMatchById(TblMatch tblMatch) {
        redisTemplate.opsForValue().set(Code.Redis.TBL_MATCH_KEY + tblMatch.getId(), JSON.toJSONString(tblMatch));
        redisTemplate.opsForZSet().add(Code.Redis.TBL_MATCH_LIST_KEY + tblMatch.getMatchType(), String.valueOf(tblMatch.getId()), tblMatch.getMatchTime().getTime());
    }

    @Override
    public void putTblMatchHotById(TblMatch tblMatch) {
        redisTemplate.opsForValue().set(Code.Redis.TBL_MATCH_HOT_KEY + tblMatch.getId(), JSON.toJSONString(tblMatch));
        redisTemplate.opsForZSet().add(Code.Redis.TBL_MATCH_HOT_LIST_KEY + tblMatch.getMatchType(), String.valueOf(tblMatch.getId()), tblMatch.getMatchTime().getTime());
    }

    @Override
    public void putTblSeasonById(TblSeason tblSeason) {
        redisTemplate.opsForValue().set(Code.Redis.TBL_SEASON_KEY + tblSeason.getId(), JSON.toJSONString(tblSeason));
    }

    @Override
    public void putTblSeasonRoundById(TblSeasonRound tblSeasonRound) {
        redisTemplate.opsForValue().set(Code.Redis.TBL_SEASON_ROUND_KEY + tblSeasonRound.getId(), JSON.toJSONString(tblSeasonRound));
    }

    @Override
    public void putTblTeamById(TblTeam tblTeam) {
        redisTemplate.opsForValue().set(Code.Redis.TBL_TEAM_KEY + tblTeam.getId(), JSON.toJSONString(tblTeam));
    }

    @Override
    public Set<String> getTblMatchOddsByMatchId(Long matchId) {
        return redisTemplate.opsForSet().members(Code.Redis.TBL_MATCH_ODDS_LIST_KEY + matchId);
    }

    @Override
    public void putTblMatchOddsByMatchId(List<TblMatchOdds> result) {
        Set<Long> ids = new HashSet<>();
        result.forEach(odds -> {
            redisTemplate.opsForSet().add(Code.Redis.TBL_MATCH_ODDS_LIST_KEY + odds.getMatchId(), String.valueOf(odds.getId()));
            putTblMatchOddsById(odds);
            ids.add(odds.getMatchId());
        });
        ids.forEach(id -> clearTxKey(Code.Redis.TBL_TX_MATCH_ODDS_LIST_KEY + id));
    }

    private void clearTxKey(String key) {
        redisTemplate.delete(key);
    }

//    private void putTblMatchOddsById(Long matchId, Long id) {
//        redisTemplate.opsForSet().add(Code.Redis.TBL_MATCH_ODDS_LIST_KEY+matchId,String.valueOf(id));
//    }

    @Override
    public Set<String> getIndexTblMatchListByMatchType(Integer matchType) {
        long begin = DateUtils.addSeconds(new Date(), 15).getTime();
        long end = DateUtils.addDays(getEndOfDay(new Date()), 5).getTime();
        return redisTemplate.opsForZSet().rangeByScore(Code.Redis.TBL_MATCH_LIST_KEY + matchType, begin, end);
    }

    @Override
    public Set<String> getIndexTblMatchHotListByMatchType(Integer matchType) {
        long begin = DateUtils.addSeconds(new Date(), 15).getTime();
        long end = DateUtils.addDays(getEndOfDay(new Date()), 5).getTime();
        return redisTemplate.opsForZSet().rangeByScore(Code.Redis.TBL_MATCH_HOT_LIST_KEY + matchType, begin, end);
    }

    @Override
    public void putIndexTblMatchListByMatchType(List<TblMatch> result) {
        result.forEach(e -> {
            //比赛
            this.putTblMatchById(e);
            //联赛球队排名
            if (null != e.getHostSeasonTeam()){
                this.putTblSeasonTeamById(e.getHostSeasonTeam());
                if (null != e.getHostSeasonTeam().getSeason()){
                    this.putTblSeasonById(e.getHostSeasonTeam().getSeason());
                }
                if (null != e.getHostSeasonTeam().getTeam()){
                    //球队
                    this.putTblTeamById(e.getHostSeasonTeam().getTeam());
                }
            }
            if (null != e.getAwaySeasonTeam()){
                this.putTblSeasonTeamById(e.getAwaySeasonTeam());
                if(null != e.getAwaySeasonTeam().getTeam()){
                    this.putTblTeamById(e.getAwaySeasonTeam().getTeam());
                }
            }
            if (null != e.getMatchOdds()){
                //赔率
                this.putTblMatchOddsByMatchId(e.getMatchOdds());
            }
        });
    }

    @Override
    public void putIndexTblMatchHotListByMatchType(List<TblMatch> result) {
        result.forEach(e -> {
            //比赛
            this.putTblMatchHotById(e);
            //联赛球队排名
            this.putTblSeasonTeamById(e.getHostSeasonTeam());
            this.putTblSeasonTeamById(e.getAwaySeasonTeam());
            //联赛
            this.putTblSeasonById(e.getHostSeasonTeam().getSeason());
            //球队
            this.putTblTeamById(e.getHostSeasonTeam().getTeam());
            this.putTblTeamById(e.getAwaySeasonTeam().getTeam());
            //赔率
            this.putTblMatchOddsByMatchId(e.getMatchOdds());
        });
    }

    @Override
    public boolean getTxKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public void putTxKey(String key) {
        redisTemplate.opsForValue().set(key, "N/A", 30, TimeUnit.DAYS);
    }

    /**
     * @description: 更新一次比赛信息的所有缓存
     * @author: ZhiYao.Zhang
     * @param:
     * @return:
     * @create: 2019-04-19
     **/

    @Override
    public void updateMatchRedis(Integer matchType, Integer matchStatus) {
        logger.info("缓存更新开始");
        long t = System.currentTimeMillis();

        List<TblMatch> matchList = tblMatchService.findMatchListByMatchTypeAndMatchStatusUpdateRedis(matchType, matchStatus);
        //缓存更新
        if (!matchList.isEmpty()){
            putIndexTblMatchListByMatchType(matchList);
        }
        logger.info("缓存更新结束");
        System.out.println("用时" + (System.currentTimeMillis() - t) + "毫秒");
    }

    @Override
    public void updateHotMatchRedis(Integer matchType, Integer matchStatus) {
        logger.info("缓存更新开始");
        long t = System.currentTimeMillis();

        List<TblMatch> matchList = tblMatchService.findMatchHotListByMatchTypeAndMatchStatusUpdateRedis(matchType, matchStatus);
        //缓存更新
        if (!matchList.isEmpty()){
            putIndexTblMatchHotListByMatchType(matchList);
        }
        logger.info("缓存更新结束");
        System.out.println("用时" + (System.currentTimeMillis() - t) + "毫秒");
    }


//    @Override
//    public void putTxTblMatchOddsByMatchId(Long matchId) {
//        redisTemplate.opsForValue().set(Code.Redis.TBL_TX_MATCH_ODDS_LIST_KEY+matchId,"N/A",30,TimeUnit.DAYS);
//    }
//
//    @Override
//    public boolean getTxTblMatchOddsByMatchId(Long matchId) {
//        return redisTemplate.hasKey(Code.Redis.TBL_TX_MATCH_ODDS_LIST_KEY+matchId);
//    }

    @Override
    public void putTblSeasonTeamById(TblSeasonTeam tblSeasonTeam) {
        redisTemplate.opsForValue().set(Code.Redis.TBL_SEASON_TEAM_KEY + tblSeasonTeam.getId(), JSON.toJSONString(tblSeasonTeam));
    }

    @Override
    public void putTblMatchOddsById(TblMatchOdds tblMatchOdds) {
        redisTemplate.opsForValue().set(Code.Redis.TBL_MATCH_ODDS_KEY + tblMatchOdds.getId(), JSON.toJSONString(tblMatchOdds));
    }

    @Override
    public TblMatch getTblMatchById(Long id) {
        String json = redisTemplate.opsForValue().get(Code.Redis.TBL_MATCH_KEY + id);
        if (StringUtils.isBlank(json)) {
            return null;
        }
        return JSON.parseObject(json, TblMatch.class);
    }

    @Override
    public TblSeason getTblSeasonById(Long id) {
        String json = redisTemplate.opsForValue().get(Code.Redis.TBL_SEASON_KEY + id);
        if (StringUtils.isBlank(json)) {
            return null;
        }
        return JSON.parseObject(json, TblSeason.class);
    }

    @Override
    public TblSeasonRound getTblSeasonRoundById(Long id) {
        String json = redisTemplate.opsForValue().get(Code.Redis.TBL_SEASON_ROUND_KEY + id);
        if (StringUtils.isBlank(json)) {
            return null;
        }
        return JSON.parseObject(json, TblSeasonRound.class);
    }

    @Override
    public TblSeasonTeam getTblSeasonTeamById(Long id) {
        String json = redisTemplate.opsForValue().get(Code.Redis.TBL_SEASON_TEAM_KEY + id);
        if (StringUtils.isBlank(json)) {
            return null;
        }
        return JSON.parseObject(json, TblSeasonTeam.class);
    }

    @Override
    public TblMatchOdds getTblMatchOddsById(Long id) {
        String json = redisTemplate.opsForValue().get(Code.Redis.TBL_MATCH_ODDS_KEY + id);
        if (StringUtils.isBlank(json)) {
            return null;
        }
        return JSON.parseObject(json, TblMatchOdds.class);
    }

    @Override
    public TblTeam getTblTeamById(Long id) {
        String json = redisTemplate.opsForValue().get(Code.Redis.TBL_TEAM_KEY + id);
        if (StringUtils.isBlank(json)) {
            return null;
        }
        return JSON.parseObject(json, TblTeam.class);
    }

    private Date getEndOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

}
