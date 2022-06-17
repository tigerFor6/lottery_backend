package com.linglong.lottery_backend.lottery.match.aspect;

import com.alibaba.fastjson.JSON;
import com.linglong.lottery_backend.lottery.match.common.Code;
import com.linglong.lottery_backend.lottery.match.entity.*;
import com.linglong.lottery_backend.lottery.match.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 化增光
 * Season ctrl 接口 日志缓存
 */
@Aspect
@Component
@Slf4j
public class MatchAspect {

//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private TblMatchService tblMatchService;

    @Autowired
    private TblSeasonTeamService tblSeasonTeamService;

    @Autowired
    private TblTeamService tblTeamService;

    @Autowired
    private TblSeasonService tblSeasonService;

    @Autowired
    private TblMatchOddsService tblMatchOddsService;

    @Autowired
    private MatchRelevantRedisService matchRelevantRedisService;

//    private static final Logger logger = LoggerFactory.getLogger(MatchAspect.class);

    /**
     * 比赛列表
     */
    @Pointcut("execution(* com.linglong.lottery_backend.lottery.match.service.TblMatchService.findMatchListByMatchTypeAndMatchStatus(Integer , Integer)) && args(matchType,matchStatus)")
    public void performanceTblMatchList(Integer matchType, Integer matchStatus) {
    }

    @Around(value = "performanceTblMatchList(matchType,matchStatus)")
    public List<TblMatch> watchPerformanceTblMatchList(ProceedingJoinPoint jp, Integer matchType, Integer matchStatus) {
        List<TblMatch> result = null;
        try {
            //当第二次运行大服务时走缓存。
            Set<String> set = matchRelevantRedisService.getIndexTblMatchListByMatchType(matchType);
            //log.info("watchPerformanceTblMatchList: {}",set);
            if (set.size() > 0) {
                List<TblMatch> resMatch = new ArrayList<>();
                for (String id: set) {
                    //比赛
                    TblMatch tblMatch = tblMatchService.findMatchById(Long.valueOf(id));
                    if (StringUtils.isBlank(tblMatch.getMatchSn())
                            || null == tblMatch.getSaleStatus()
                            || !Integer.valueOf(1).equals(tblMatch.getSaleStatus())){
                        continue;
                    }
                    //联赛球队排名
                    TblSeasonTeam host = tblSeasonTeamService.findSeasonTeamById(tblMatch.getHostId());
                    tblMatch.setHostSeasonTeam(host);
                    TblSeasonTeam away = tblSeasonTeamService.findSeasonTeamById(tblMatch.getAwayId());
                    tblMatch.setAwaySeasonTeam(away);
                    //联赛
                    if (null != host){
                        TblSeason tblSeason = tblSeasonService.findSeasonById(host.getSeasonId());
                        host.setSeason(tblSeason);
                        //球队
                        TblTeam hostTime = tblTeamService.findTeamById(host.getTeam().getId());
                        tblMatch.getHostSeasonTeam().setTeam(hostTime);
                    }
                    if (null != away){
                        TblTeam awayTime = tblTeamService.findTeamById(away.getTeam().getId());
                        tblMatch.getAwaySeasonTeam().setTeam(awayTime);
                    }
                    //赔率
                    List<TblMatchOdds> tblMatchOddsList = tblMatchOddsService.findMatchOddsByMatchId(Long.valueOf(id));
                    tblMatch.setMatchOdds(tblMatchOddsList);
                    resMatch.add(tblMatch);
                }
                return resMatch;
            }

            Object res = jp.proceed();
            if (null != res) {
                result = (List<TblMatch>) res;
                //插入缓存
                if (!result.isEmpty()){
                    matchRelevantRedisService.putIndexTblMatchListByMatchType(result);
                }
            }
        } catch (Throwable throwable) {
            log.error("MatchAspect watchPerformanceTblMatchList", throwable);
        }
        return result;
    }

//    /**
//     * 热门比赛列表
//     */
//    @Pointcut("execution(* com.linglong.lottery_backend.lottery.match.service.TblMatchService.findMatchListByMatchTypeAndMatchStatusAndHotStatus(Integer , Integer)) && args(matchType,matchStatus)")
//    public void performanceTblMatchHotList(Integer matchType, Integer matchStatus) {
//    }

//    @Around(value = "performanceTblMatchHotList(matchType,matchStatus)")
//    public List<TblMatch> watchPerformanceTblMatchHotList(ProceedingJoinPoint jp, Integer matchType, Integer matchStatus) {
//        List<TblMatch> result = null;
//        try {
//            //当第二次运行大服务时走缓存。
//            Set<String> set = matchRelevantRedisService.getIndexTblMatchHotListByMatchType(matchType);
//            //log.info("watchPerformanceTblMatchHotList: {}",set);
//            if (set.size() > 0) {
//                List<TblMatch> resMatch = new ArrayList<>();
//                for (String id: set) {
//                    //比赛
//                    TblMatch tblMatch = tblMatchService.findMatchById(Long.valueOf(id));
//                    if (StringUtils.isBlank(tblMatch.getMatchSn())
//                            || null == tblMatch.getSaleStatus()
//                            || !Integer.valueOf(1).equals(tblMatch.getSaleStatus())){
//                        continue;
//                    }
//                    //联赛球队排名
//                    TblSeasonTeam host = tblSeasonTeamService.findSeasonTeamById(tblMatch.getHostId());
//                    tblMatch.setHostSeasonTeam(host);
//                    TblSeasonTeam away = tblSeasonTeamService.findSeasonTeamById(tblMatch.getAwayId());
//                    tblMatch.setAwaySeasonTeam(away);
//                    //联赛
//                    if (null != host){
//                        TblSeason tblSeason = tblSeasonService.findSeasonById(host.getSeasonId());
//                        host.setSeason(tblSeason);
//                        //球队
//                        TblTeam hostTime = tblTeamService.findTeamById(host.getTeam().getId());
//                        tblMatch.getHostSeasonTeam().setTeam(hostTime);
//                    }
//                    if (null != away){
//                        TblTeam awayTime = tblTeamService.findTeamById(away.getTeam().getId());
//                        tblMatch.getAwaySeasonTeam().setTeam(awayTime);
//                    }
//                    //赔率
//                    List<TblMatchOdds> tblMatchOddsList = tblMatchOddsService.findMatchOddsByMatchId(Long.valueOf(id));
//                    tblMatch.setMatchOdds(tblMatchOddsList);
//                    resMatch.add(tblMatch);
//                }
//                return resMatch;
//            }
//
//            Object res = jp.proceed();
//            if (null != res) {
//                result = (List<TblMatch>) res;
//                //插入缓存
//                if (!result.isEmpty()){
//                    matchRelevantRedisService.putIndexTblMatchHotListByMatchType(result);
//                }
//            }
//        } catch (Throwable throwable) {
//            log.error("MatchAspect watchPerformanceTblMatchList", throwable);
//        }
//        return result;
//    }

    /**
     * 赔率单表列表
     */
    @Pointcut("execution(* com.linglong.lottery_backend.lottery.match.service.TblMatchOddsService.findMatchOddsByMatchId(Long)) && args(matchId)")
    public void performanceFindMatchOddsByMatchId(Long matchId) {
    }

    @Around(value = "performanceFindMatchOddsByMatchId(matchId)")
    public List<TblMatchOdds> watchPerformanceFindMatchOddsByMatchId(ProceedingJoinPoint jp, Long matchId) {
        List<TblMatchOdds> result = new LinkedList<>();
        try {
            //根据查到的比赛id去缓存中查对应的赔率信息
            if (matchRelevantRedisService.getTxKey(Code.Redis.TBL_TX_MATCH_ODDS_LIST_KEY+matchId)){
                return result;
            }
            Set<String> ids = matchRelevantRedisService.getTblMatchOddsByMatchId(matchId);
            if (!ids.isEmpty()){
                for (String id : ids) {
                    TblMatchOdds matchOdds = tblMatchOddsService.findMatchOddsById(Long.valueOf(id));
                    if (null != matchOdds){
                        result.add(matchOdds);
                    }
                }
                return result;
            }
            Object res = jp.proceed();
            log.info("查了数据库比赛赔率表-->: {}", matchId);
            if (null != res) {
                //插入缓存
                result = (List<TblMatchOdds>) res;

                if (!result.isEmpty()){
                    log.info("查了数据库比赛赔率表-->: {}", JSON.toJSONString(result));
                    matchRelevantRedisService.putTblMatchOddsByMatchId(result);
                    return result;
                }

                log.info("查了数据库比赛赔率表 empty -->: {}", matchId);
            }
            matchRelevantRedisService.putTxKey(Code.Redis.TBL_TX_MATCH_ODDS_LIST_KEY+matchId);
        } catch (Throwable throwable) {
            log.error("MatchAspect watchPerformanceFindMatchOddsByMatchId", throwable);
        }
        return result;
    }

    /**
     * 赔率单表
     */
    @Pointcut("execution(* com.linglong.lottery_backend.lottery.match.service.TblMatchOddsService.findMatchOddsById(Long)) && args(oddsId)")
    public void performanceFindMatchOdds(Long oddsId) {
    }

    @Around(value = "performanceFindMatchOdds(oddsId)")
    public TblMatchOdds watchPerformanceFindMatchOdds(ProceedingJoinPoint jp, Long oddsId) {
        TblMatchOdds result = null;
        try {
            //根据查到的赔率id去缓存中查对应的赔率信息
            if (matchRelevantRedisService.getTxKey(Code.Redis.TBL_TX_MATCH_ODDS_KEY + oddsId)) {
                return result;
            }
            //根据查出的赔率id，查缓存中的赔率信息
            result = matchRelevantRedisService.getTblMatchOddsById(oddsId);
            if (null != result) {
                return result;
            }
            Object res = jp.proceed();
            log.info("查了数据库赔率表 oddId:",oddsId);
            if (null != res) {
                //插入缓存
                result = (TblMatchOdds) res;
                matchRelevantRedisService.putTblMatchOddsById(result);
                return result;
            }
            matchRelevantRedisService.putTxKey(Code.Redis.TBL_TX_MATCH_ODDS_KEY + oddsId);
        } catch (Throwable throwable) {
            log.error("MatchAspect watchPerformanceFindMatchOdds", throwable);
        }

        return result;
    }

    /**
     * 联赛单表
     */
    @Pointcut("execution(* com.linglong.lottery_backend.lottery.match.service.TblSeasonService.findSeasonById(Long)) && args(seasonId)")
    public void performanceFindSeason(Long seasonId) {
    }

    @Around(value = "performanceFindSeason(seasonId)")
    public TblSeason watchPerformanceFindSeason(ProceedingJoinPoint jp, Long seasonId) {
        //未添加缓存穿透
        TblSeason result = null;
        try {
            //根据查出的联赛id，查缓存中的联赛信息
            result = matchRelevantRedisService.getTblSeasonById(seasonId);
            if (null != result) {
                return result;
            }
            Object res = jp.proceed();
            log.info("查了数据库联赛表");
            if (null != res) {
                //插入缓存
                result = (TblSeason) res;
                matchRelevantRedisService.putTblSeasonById(result);
            }
        } catch (Throwable throwable) {
            log.error("MatchAspect watchPerformanceFindSeason", throwable);
        }

        return result;
    }

    /**
     * 球队单表
     */
    @Pointcut("execution(* com.linglong.lottery_backend.lottery.match.service.TblTeamService.findTeamById(Long)) && args(teamId)")
    public void performanceFindTeamById(Long teamId) {
    }

    @Around(value = "performanceFindTeamById(teamId)")
    public TblTeam watchPerformanceFindTeamById(ProceedingJoinPoint jp, Long teamId) {
        TblTeam result = null;
        try {
            //根据查出的球队id，查缓存中的球队信息
            result = matchRelevantRedisService.getTblTeamById(teamId);
            //String team = redisTemplate.opsForValue().get(Code.Redis.TBL_TEAM_KEY + teamId);
            if (null != result) {
                return result;
            }

            Object res = jp.proceed();
            log.info("查了数据库球队表");
            if (null != res) {
                //插入缓存
                result = (TblTeam) res;
                matchRelevantRedisService.putTblTeamById(result);
            }
        } catch (Throwable throwable) {
            log.error("MatchAspect watchPerformanceFindTeamById", throwable);
        }

        return result;
    }

    /**
     * 联赛球队排名单表
     */
    @Pointcut("execution(* com.linglong.lottery_backend.lottery.match.service.TblSeasonTeamService.findSeasonTeamById(Long)) && args(seasonTeamId)")
    public void performanceFindSeasonTeamById(Long seasonTeamId) {
    }

    @Around(value = "performanceFindSeasonTeamById(seasonTeamId)")
    public TblSeasonTeam watchPerformanceFindSeasonTeamById(ProceedingJoinPoint jp, Long seasonTeamId) {
        TblSeasonTeam result = null;
        try {
            //根据查到的联赛球队id去缓存中查对应的球队排名信息
            if (matchRelevantRedisService.getTxKey(Code.Redis.TBL_TX_SEASON_TEAM_KEY + seasonTeamId)) {
                return result;
            }
            result = matchRelevantRedisService.getTblSeasonTeamById(seasonTeamId);
            if (null != result) {
                return result;
            }
            Object res = jp.proceed();
            log.info("查了数据库联赛球队表");
            if (null != res) {
                //插入缓存
                result = (TblSeasonTeam) res;
                matchRelevantRedisService.putTblSeasonTeamById(result);
                return result;
            }
        } catch (Throwable throwable) {
            log.error("MatchAspect watchPerformanceFindSeasonTeamById", throwable);
        }

        return result;
    }

    /**
     * 比赛列表单表
     */
    @Pointcut("execution(* com.linglong.lottery_backend.lottery.match.service.TblMatchService.findMatchById(Long)) && args(match_id)")
    public void performanceMatch(Long match_id) {
    }

    @Around(value = "performanceMatch(match_id)")
    public TblMatch watchPerformanceMatch(ProceedingJoinPoint jp, Long match_id) {
        TblMatch result = null;
        try {
            result = matchRelevantRedisService.getTblMatchById(match_id);
            if (null != result) {
                return result;
            }

            Object res = jp.proceed();
            log.info("查了数据库比赛表");
            if (null != res) {
                //插入缓存
                result = (TblMatch) res;
                matchRelevantRedisService.putTblMatchById(result);
            }
        } catch (Throwable throwable) {
            log.error("MatchAspect watchPerformanceMatch", throwable);
        }

        return result;
    }

}
