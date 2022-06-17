package com.linglong.lottery_backend.lottery.match.ctrl;

import com.alibaba.fastjson.JSON;
import com.linglong.lottery_backend.activity.entity.TblActivity;
import com.linglong.lottery_backend.activity.rules.ActivityRules;
import com.linglong.lottery_backend.activity.service.TblActivityService;
import com.linglong.lottery_backend.lottery.match.common.Code;
import com.linglong.lottery_backend.lottery.match.common.Result;
import com.linglong.lottery_backend.lottery.match.common.ResultGenerator;
import com.linglong.lottery_backend.lottery.match.ctrl.entity.MatchType;
import com.linglong.lottery_backend.lottery.match.ctrl.entity.Odds;
import com.linglong.lottery_backend.lottery.match.entity.*;
import com.linglong.lottery_backend.lottery.match.service.*;
import com.linglong.lottery_backend.lottery.match.cache.TblSaleDateCache;
import com.linglong.lottery_backend.message.sms.cache.TblSmsTempleCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * 化增光
 */
@RestController
@RequestMapping(value = "/api/match")
@Slf4j
public class MatchController {


    @Autowired
    TblMatchService tblMatchService;

    @Autowired
    TblSeasonTeamService tblSeasonTeamService;

    @Autowired
    TblMatchOddsService tblMatchOddsService;

    @Autowired
    MatchRelevantRedisService matchRelevantRedisService;

    @Autowired
    TblSaleDateCache tblSaleDateCache;

    @Autowired
    TblSmsTempleCache tblSmsTempleCache;

    @Autowired
    TblActivityService tblActivityService;

    @Autowired
    TblMatchHotService tblMatchHotService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;


    private final static ConcurrentHashMap<Long, Date> concurrentHashMap = new ConcurrentHashMap<>();

    public List<TblMatch> commonMatch(Integer type) {
        List<TblMatch> matchList = tblMatchService.findMatchListByMatchTypeAndMatchStatus(type, Integer.valueOf(type.equals(MatchType.jclq) ? 1 : 0));
        if (matchList.isEmpty()) {
            return null;
        }
        List<TblMatch> tblMatches = matchList.stream()
                .filter(data -> (null != data.getMatchOdds() && !data.getMatchOdds().isEmpty()))
                .filter(data -> StringUtils.isNotBlank(data.getMatchSn())).map(e -> {
                    Date endTime = concurrentHashMap.get(e.getId());
                    if (null == endTime) {
                        endTime = tblMatchService.getMatchEndTime(e.getMatchType(), e.getMatchTime());
                        concurrentHashMap.put(e.getId(), endTime);
                    }
                    e.setEndTime(endTime);
                    return e;
                }).filter(data -> DateUtils.truncatedCompareTo(data.getEndTime(), new Date(), Calendar.SECOND) > 0).collect(Collectors.toList());
        return tblMatches;
    }

    /**
     * 获取足球比赛列表
     *
     * @return
     */
    @GetMapping("getMatchList/{type}")
    public Result getMatchList(@PathVariable("type") Integer type) {
        List<TblMatch> tblMatches = commonMatch(type);
        if (null == tblMatches || tblMatches.isEmpty()) {
            return ResultGenerator.genSuccessResult();
        }

        try {
            //获取热门比赛id
            List<TblMatchHot> matchHotList = tblMatchHotService.tblMatchHot();
            //存放热门比赛列表
            List<TblMatch> matchHotInfo = new ArrayList<>();
            //从所有赛中找到热门比赛 并加入热门比赛列表
            tblMatches.forEach(e -> {
                matchHotList.forEach(f -> {
                    if (e.getId().equals(f.getMatchId())) {
                        matchHotInfo.add(e);
                    }
                });
            });

            //移除比赛列表中的热门比赛
            tblMatches.removeAll(matchHotInfo);

        } catch (Exception e) {
            log.error(String.valueOf(e));
        }

        //查询结果过滤分组
        Map<String, List<TblMatch>> result = new LinkedHashMap<>();
        Map<String, List<TblMatch>> group = tblMatches.stream().collect(Collectors.groupingBy(TblMatch::getMatchTimeFormat));
        group.entrySet().stream().sorted(Entry.comparingByKey()).forEachOrdered(x -> result.put(x.getKey(), x.getValue()));

        List<HashMap<String, Object>> res = assembleMatchData(result);

        //添加热门赛事列表
        try {
            Result hotMatch = getHotMatchList(type);
            if (null == hotMatch.getResp()) {

            } else {
                List<HashMap<String, Object>> res1 = (List<HashMap<String, Object>>) hotMatch.getResp();
                res1.get(0).put("hot",true);
                res.add(res1.get(0));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        return ResultGenerator.genSuccessResult(res);
    }


    /**
     * @description: 获取热门比赛列表
     **/
    @GetMapping("getHotMatchList/{type}")
    public Result getHotMatchList(@PathVariable("type") Integer type) {
        //获取热门比赛id
        List<TblMatchHot> matchHotList = tblMatchHotService.tblMatchHot();
        //获取所有比赛列表
        List<TblMatch> tblMatches = commonMatch(type);
        //存放热门比赛列表
        List<TblMatch> matchHotInfo = new ArrayList<>();
        if (null == tblMatches || tblMatches.isEmpty() || null == matchHotList || matchHotList.isEmpty()) {
            return ResultGenerator.genSuccessResult("未查询到数据");
        }

        //从所有赛中找到热门比赛 并加入热门比赛列表
        tblMatches.forEach(e -> {
            matchHotList.forEach(f -> {
                if (e.getId().equals(f.getMatchId())) {
                    e.setLevel(f.getLevel());
                    e.setLable_status(f.getLableStatus());
                    e.setLable(f.getLable());
                    matchHotInfo.add(e);
                }
            });
        });

        if (null == matchHotInfo || matchHotInfo.isEmpty()) {
            return ResultGenerator.genSuccessResult();
        }

        //matchHotInfo 进行排序
        matchHotInfo.sort((a, b) -> Integer.compare(b.getLevel(), a.getLevel()));
        List<HashMap<String, Object>> result = assembleHotMatchData(matchHotInfo);
        String listLable = redisTemplate.opsForValue().get(Code.Redis.HOT_MATCH_LIST_LABLE);
        if (null == listLable || "".equals(listLable)) {
            result.get(0).put("title", "推荐热门比赛" + result.get(0).get("title"));
        } else {
            result.get(0).put("title", listLable + result.get(0).get("title"));
        }

        return ResultGenerator.genSuccessResult(result);
    }



    /**
     * 获取热门赛事
     * @param type
     * @return
     */
    @GetMapping("getHotMatch/{type}")
    public Result getHotMatch(@PathVariable("type") Integer type) {
        List<TblMatch> matchList = commonMatch(type);
        if (null == matchList || matchList.isEmpty()) {
            return ResultGenerator.genSuccessResult();
        }
        List<TblMatch> tblMatches = matchList.stream().filter(data -> null != data.getHotStatus() && data.getHotStatus() == 1).collect(Collectors.toList());
        if (null == tblMatches || tblMatches.isEmpty()) {
            //如果没有配置热门比赛，随便挑一场比赛展示到热门中
            Optional<TblMatch> first = matchList.stream().findFirst();
            if (first.isPresent()) {
                tblMatches.add(first.get());
            }
        } else {
            //取热门比赛中最近的一个时间展示
            Optional<TblMatch> first = tblMatches.stream().findFirst();
            if (first.isPresent()) {
                tblMatches.clear();
                tblMatches.add(first.get());
            }
        }
        //查询结果过滤分组
        Map<String, List<TblMatch>> result = new LinkedHashMap<>();
        Map<String, List<TblMatch>> group = tblMatches.stream().collect(Collectors.groupingBy(TblMatch::getMatchTimeFormat));
        group.entrySet().stream().sorted(Entry.comparingByKey()).forEachOrdered(x -> result.put(x.getKey(), x.getValue()));
        return ResultGenerator.genSuccessResult(hotAssembleMatchData(result));
    }

    private List<HashMap<String, Object>> assembleMatchData(Map<String, List<TblMatch>> group) {

        List<HashMap<String, Object>> list = new LinkedList<>();
        group.forEach((k, v) -> {
            HashMap<String, Object> result = new HashMap<>();
            List<HashMap<String, Object>> matches = v.stream().map(data -> {
                HashMap<String, Object> match = new HashMap<>();
                match.put("match_id", data.getId());
                match.put("match_week", data.getMatchWeek());
                match.put("match_sn", data.getMatchSn());
                match.put("period", null == data.getPeriod() ? "" : data.getPeriod());
                match.put("match_desc", data.getMatchDesc());
                TblSeasonTeam hostSeasonTeam = data.getHostSeasonTeam();
                TblSeasonTeam awaySeasonTeam = data.getAwaySeasonTeam();
                TblSeason tblSeason = null == data.getHostSeasonTeam() ? null : data.getHostSeasonTeam().getSeason();
                match.put("season_pre", null == tblSeason ? "" : tblSeason.getSeasonName());
                match.put("league_pre", null == tblSeason ? "" : tblSeason.getSeasonFullName());
                match.put("hot_status", data.getHotStatus());
                if (null == hostSeasonTeam) {
                    match.put("host_rank", "[0]");
                    match.put("host_name", "");
                } else {
                    match.put("host_rank", "[" + hostSeasonTeam.getTeamRank() + "]");
                    match.put("host_name", null == hostSeasonTeam.getTeam() ? "" : hostSeasonTeam.getTeam().getTeamName());
                }
                if (null == awaySeasonTeam) {
                    match.put("away_rank", "[0]");
                    match.put("away_name", "");
                } else {
                    match.put("away_rank", "[" + awaySeasonTeam.getTeamRank() + "]");
                    match.put("away_name", null == awaySeasonTeam.getTeam() ? "" : awaySeasonTeam.getTeam().getTeamName());
                }

                Map<String, Odds> odds = data.getMatchOdds().stream().collect(Collectors.groupingBy(TblMatchOdds::getPlayCode, Collectors.collectingAndThen(Collectors.toList(), value -> JSON.parseObject(value.get(0).getPlayDetails(), Odds.class))));
                match.putAll(odds);
                match.put("match_issue", data.getMatchIssueFormat() + data.getMatchSn().substring(data.getMatchSn().length() - 3));
                match.put("issue_time_desc", DateFormatUtils.format(data.getEndTime(), "HH:mm") + "截止");
                match.put("deadline", DateFormatUtils.format(data.getMatchTime(), "yyyy年MM月dd日 HH点mm分"));
                return match;
            }).collect(Collectors.toList());
            result.put("title", k + ",共" + matches.size() + "场比赛");
            matches.sort(Comparator.comparing(data -> String.valueOf(data.get("match_sn"))));
            result.put("matches", matches);
            list.add(result);
        });
        return list;
    }

    private List<HashMap<String, Object>> hotAssembleMatchData(Map<String, List<TblMatch>> group) {

        List<HashMap<String, Object>> list = new LinkedList<>();
        group.forEach((k, v) -> {
            HashMap<String, Object> result = new HashMap<>();
            List<HashMap<String, Object>> matches = v.stream().map(data -> {
                HashMap<String, Object> match = new HashMap<>();
                Integer matchType = data.getMatchType();
                match.put("match_id", data.getId());
                match.put("match_week", data.getMatchWeek());
                match.put("match_sn", data.getMatchSn());
                match.put("match_desc", data.getMatchDesc());
                TblSeasonTeam hostSeasonTeam = data.getHostSeasonTeam();
                TblSeasonTeam awaySeasonTeam = data.getAwaySeasonTeam();
                TblSeason tblSeason = data.getHostSeasonTeam().getSeason();
                match.put("season_pre", null == tblSeason ? "" : tblSeason.getSeasonName());
                match.put("league_pre", null == tblSeason ? "" : tblSeason.getSeasonFullName());
                match.put("hot_status", data.getHotStatus());
                match.put("host_rank", "[" + ((null == hostSeasonTeam) ? "" : hostSeasonTeam.getTeamRank()) + "]");
                match.put("away_rank", "[" + ((null == awaySeasonTeam) ? "" : awaySeasonTeam.getTeamRank()) + "]");
                match.put("host_name", null == hostSeasonTeam.getTeam() ? "" : hostSeasonTeam.getTeam().getTeamName());
                match.put("away_name", null == awaySeasonTeam.getTeam() ? "" : awaySeasonTeam.getTeam().getTeamName());
                Map<String, Odds> odds = data.getMatchOdds().stream().collect(Collectors.groupingBy(TblMatchOdds::getPlayCode, Collectors.collectingAndThen(Collectors.toList(), value -> JSON.parseObject(value.get(0).getPlayDetails(), Odds.class))));
                match.putAll(odds);
                match.put("match_issue", data.getMatchIssueFormat() + data.getMatchSn().substring(data.getMatchSn().length() - 3));
                match.put("issue_time_desc", DateFormatUtils.format(data.getEndTime(), "HH:mm") + "截止");
                match.put("deadline", DateFormatUtils.format(data.getMatchTime(), "yyyy/MM/dd HH:mm:ss"));
                match.put("endTime", DateFormatUtils.format(data.getEndTime(), "yyyy/MM/dd HH:mm:ss"));
                match.put("sale_status", data.getSaleStatus());
                match.put("match_type", matchType);
                Date endTime = data.getEndTime();
                if (new Date().before(endTime)) {
                    match.put("dead_status", 1);
                } else {
                    match.put("dead_status", 0);
                }
                return match;
            }).collect(Collectors.toList());
            matches.sort(Comparator.comparing(data -> String.valueOf(data.get("match_sn"))));
            result.put("matches", matches);
            list.add(result);
        });
        return list;
    }

    @GetMapping("/getActivityMatch")
    public Result getActivityMatch(@RequestParam("activityId") Long activityId) {
        TblActivity tblActivity = tblActivityService.findTblActivityByActivityId(activityId);
        ArrayList<Long> matchIds = new ArrayList<Long>();
        if (StringUtils.isBlank(tblActivity.getRules())) {
            return ResultGenerator.genFailResult("活动规则为空");
        }
        String[] activityRuleArr = tblActivity.getRules().split("@");
        for (String rules : activityRuleArr) {
            String[] rule = rules.split(":");
            if (rule[0].equals(ActivityRules.draw001)) {
                long[] longs = Arrays.stream(rule[1].split(",")).mapToLong(Long::valueOf).toArray();
                for (long matchId : longs) {
                    matchIds.add(matchId);
                }
            }
        }
        List<TblMatch> tblMatches = tblMatchService.findMatchByIds(matchIds);
        tblMatches.stream().map(e -> {
            Date endTime = concurrentHashMap.get(e.getId());
            if (null == endTime) {
                endTime = tblMatchService.getMatchEndTime(e.getMatchType(), e.getMatchTime());
                concurrentHashMap.put(e.getId(), endTime);
            }
            e.setEndTime(endTime);
            return e;
        }).collect(Collectors.toList());
        Map<String, List<TblMatch>> result = new LinkedHashMap<>();
        Map<String, List<TblMatch>> group = tblMatches.stream().collect(Collectors.groupingBy(TblMatch::getMatchTimeFormat));
        group.entrySet().stream().sorted(Entry.comparingByKey()).forEachOrdered(x -> result.put(x.getKey(), x.getValue()));
        return ResultGenerator.genSuccessResult(hotAssembleMatchData(result));
    }

    @GetMapping("updateMatchRedis/{type}")
    public Result updateMatchRedis(@PathVariable("type") Integer type) {
        matchRelevantRedisService.updateMatchRedis(type, Integer.valueOf(type.equals(MatchType.jclq) ? 1 : 0));
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("refresh")
    public void refresh() {
        concurrentHashMap.clear();
        tblSaleDateCache.refresh();
        tblSmsTempleCache.refresh();
    }

    private List<HashMap<String, Object>> assembleHotMatchData(List<TblMatch> hotMatchList) {

        List<HashMap<String, Object>> res = new ArrayList<>();
        HashMap<String, Object> result = new HashMap<>();

        List<HashMap<String, Object>> matches = hotMatchList.stream().map(data -> {
            HashMap<String, Object> match = new HashMap<>();
            match.put("match_id", data.getId());
            match.put("match_week", data.getMatchWeek());
            match.put("match_sn", data.getMatchSn());
            match.put("match_desc", data.getMatchDesc());
            TblSeasonTeam hostSeasonTeam = data.getHostSeasonTeam();
            TblSeasonTeam awaySeasonTeam = data.getAwaySeasonTeam();
            TblSeason tblSeason = null == data.getHostSeasonTeam() ? null : data.getHostSeasonTeam().getSeason();
            match.put("season_pre", null == tblSeason ? "" : tblSeason.getSeasonName());
            match.put("league_pre", null == tblSeason ? "" : tblSeason.getSeasonFullName());
            match.put("hot_status", data.getHotStatus());
            if (null == hostSeasonTeam) {
                match.put("host_rank", "[0]");
                match.put("host_name", "");
            } else {
                match.put("host_rank", "[" + hostSeasonTeam.getTeamRank() + "]");
                match.put("host_name", null == hostSeasonTeam.getTeam() ? "" : hostSeasonTeam.getTeam().getTeamName());
            }
            if (null == awaySeasonTeam) {
                match.put("away_rank", "[0]");
                match.put("away_name", "");
            } else {
                match.put("away_rank", "[" + awaySeasonTeam.getTeamRank() + "]");
                match.put("away_name", null == awaySeasonTeam.getTeam() ? "" : awaySeasonTeam.getTeam().getTeamName());
            }

            Map<String, Odds> odds = data.getMatchOdds().stream().collect(Collectors.groupingBy(TblMatchOdds::getPlayCode, Collectors.collectingAndThen(Collectors.toList(), value -> JSON.parseObject(value.get(0).getPlayDetails(), Odds.class))));
            match.putAll(odds);
            match.put("match_issue", data.getMatchIssueFormat() + data.getMatchSn().substring(data.getMatchSn().length() - 3));
            match.put("issue_time_desc", DateFormatUtils.format(data.getEndTime(), "HH:mm") + "截止");
            match.put("deadline", DateFormatUtils.format(data.getMatchTime(), "yyyy年MM月dd日 HH点mm分"));

            if (-1 == data.getLable_status()) {
                match.put("lable", "");
            } else {
                match.put("lable", data.getLable());
            }

            return match;
        }).collect(Collectors.toList());

        result.put("matches", matches);
        result.put("title", ",共" + matches.size() + "场");
        res.add(result);
        return res;
    }
}
