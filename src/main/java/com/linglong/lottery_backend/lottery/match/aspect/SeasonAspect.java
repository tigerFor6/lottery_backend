//package com.linglong.lottery_backend.lottery.match.aspect;
//
//import com.linglong.lottery_backend.lottery.match.entity.TblMatch;
//import com.linglong.lottery_backend.lottery.match.entity.TblSeasonTeam;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//
///**
// * 化增光
// * Season ctrl 接口 日志缓存
// */
//@Aspect
//@Component
//public class SeasonAspect {
//
//    @Autowired
//    private RedisTemplate<String,String> redisTemplate;
//
//    private static final Logger logger = LoggerFactory.getLogger(SeasonAspect.class);
//
//
//    private final String LOTTERY_SEASON_KEY = "LOTTERY_SEASON_KEY:";
//    //private final String TBL_SEASON_TEAM_KEY = "LOTTERY_SEASON_TEAM_KEY:";
//    private final String LOTTERY_MATCH_KEY = "LOTTERY_MATCH_KEY:";
//    private final String LOTTERY_MATCH_ODDS_KEY = "LOTTERY_MATCH_ODDS_KEY:";
//
//    @Autowired
//    private TblMatchService tblMatchService;
//
//    /**
//     * 联赛列表
//     * @param matchType
//     * @param matchStatus
//     */
//    @Pointcut("execution(* com.linglong.lottery_backend.lottery.match.service.TblMatchService.findMatchListByMatchTypeAndMatchStatus(Integer,Integer)) && args(matchType,matchStatus)")
//    public void performance(Integer matchType,Integer matchStatus) {}
//
//    @Around(value = "performance(matchType,matchStatus)")
//    public List<TblMatch> watchPerformance(ProceedingJoinPoint jp, Integer matchType, Integer matchStatus) {
//
//
//
//
//        this.TBL_SEASON__KEY+match_id
//
//        TblSeasonTeam  this.TBL_SEASON__KEY+host_id;
//
//        TblSeasonTeam this.TBL_SEASON__KEY+away_id;
//
//
//
//        logger.info("调用接口:TblMatchService.findMatchListByMatchTypeAndMatchStatus");
//        Gson gson = new Gson();
//        List<TblMatchPack> result = null;
//
//        try {
//            //获取列表
//            //List<String> ids = redisTemplate.opsForList().range(this.TBL_SEASON_TEAM_LIST_KEY+matchType,0,-1);
////            logger.info("查询缓存key:"+this.TBL_CHANNEL_KEY+channelCode);
////            String str = redisTemplate.opsForValue().get(this.TBL_CHANNEL_KEY+channelCode);
////            if (StringUtils.isNotBlank(str)){
////                logger.info("查询缓存res:"+str);
////                return gson.fromJson(str,TblChannel.class);
////            }
//            Object res = jp.proceed();
//            if (null!=res){
//                result = (List<TblMatchPack>) res;
//                return result;
//            }
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//
//}
