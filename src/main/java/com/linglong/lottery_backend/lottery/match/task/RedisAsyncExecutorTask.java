package com.linglong.lottery_backend.lottery.match.task;

import com.linglong.lottery_backend.common.bean.LockKey;
import com.linglong.lottery_backend.listener.bean.MatchInfo;
import com.linglong.lottery_backend.lottery.match.entity.*;
import com.linglong.lottery_backend.lottery.match.repository.*;
import com.linglong.lottery_backend.lottery.match.service.MatchRelevantRedisService;
import com.linglong.lottery_backend.prize.task.PrizeAsyncExecutorTask;
import com.linglong.lottery_backend.utils.DateUtil;
import com.linglong.redisson.configure.annotations.LockAction;
import com.linglong.redisson.configure.util.LockType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class RedisAsyncExecutorTask {

    private static final int lockTime = 5000;
    //private static final Logger logger = LoggerFactory.getLogger(RedisAsyncExecutorTask.class);

    @Autowired
    TblSeasonRepository seasonRepository;

    @Autowired
    TblSeasonTeamRepository seasonTeamRepository;

    @Autowired
    TblSeasonRoundRepository seasonRoundRepository;

    @Autowired
    TblTeamRepository teamRepository;

    @Autowired
    TblMatchRepository matchRepository;

    @Autowired
    TblMatchOddsRepository matchOddsRepository;

    @Autowired
    MatchRelevantRedisService matchRelevantRedisService;

    @Autowired
    PrizeAsyncExecutorTask prizeAsyncExecutorTask;

    @Async("taskExecutor")
    @LockAction(key = LockKey.lockMatch, value = "#matchId", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public void doUpdateTblMatch(Long matchId, boolean matchResult, boolean cancelResult) {
       Optional<TblMatch> tblMatch = matchRepository.findById(matchId);
       if (tblMatch.isPresent()){
           matchRelevantRedisService.putTblMatchById(tblMatch.get());
           if (matchResult){
               prizeAsyncExecutorTask.doCalculateResult(matchId);
           }
           if (cancelResult){
               prizeAsyncExecutorTask.doCalculateSpecialResult(matchId);
           }

       }
    }

    @Async("taskExecutor")
    public void doUpdateTblSeason(Long seasonId) {
        Optional<TblSeason> tblSeason = seasonRepository.findById(seasonId);
        if (tblSeason.isPresent()){
            matchRelevantRedisService.putTblSeasonById(tblSeason.get());
        }
    }

    @Async("taskExecutor")
    public void doUpdateTblSeasonRound(Long seasonRoundId) {
        Optional<TblSeasonRound> tblSeasonRound = seasonRoundRepository.findById(seasonRoundId);
        if (tblSeasonRound.isPresent()){
            matchRelevantRedisService.putTblSeasonRoundById(tblSeasonRound.get());
        }
    }

    @Async("taskExecutor")
    public void doUpdateTblSeasonTeam(Long seasonTeamId) {
        Optional<TblSeasonTeam> tblSeasonTeam = seasonTeamRepository.findById(seasonTeamId);
        if (tblSeasonTeam.isPresent()){
            matchRelevantRedisService.putTblSeasonTeamById(tblSeasonTeam.get());
        }
    }

    @Async("taskExecutor")
    @LockAction(key = LockKey.lockMatchOdds, value = "#matchInfo.matchOddsId[0]", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public void doUpdateTblMatchOdds(MatchInfo matchInfo) {
        log.info("doUpdateTblMatchOdds: {}",matchInfo.getMatchOddsId());
        List<TblMatchOdds> tblMatchOdds = matchOddsRepository.findAllById(Arrays.asList(matchInfo.getMatchOddsId()));
        if (!tblMatchOdds.isEmpty()){
            matchRelevantRedisService.putTblMatchOddsByMatchId(tblMatchOdds);
        }
    }

    @Async("taskExecutor")
    public void doUpdateTblTeam(Long teamId) {
        Optional<TblTeam> tblTeam = teamRepository.findById(teamId);
        if (tblTeam.isPresent()){
            matchRelevantRedisService.putTblTeamById(tblTeam.get());
        }
    }


    public void updateMatchRedisTask() {
        Timestamp current = DateUtil.getCurrentTimestamp();
        String time = DateUtil.formatTime(current, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS);
        System.out.println(time+ "刷新竞彩缓存开始>>>>>>>>>>>");
        matchRelevantRedisService.updateMatchRedis(1, 0);
        matchRelevantRedisService.updateMatchRedis(2, 1);
    }
//    @Async("taskExecutor")
//    @Deprecated
//    public void doUpdateTblMatchResult(Long matchId) {
//        Optional<TblMatch> tblMatch = matchRepository.findById(matchId);
//        if (tblMatch.isPresent()){
//            PrizeResult prizeResult = JSON.parseObject(tblMatch.get().getMatchResults(), PrizeResult.class);
//            prizeAsyncExecutorTask.doUpdateBatchBettingGroupDetails(prizeResult);
//        }
//    }
}
