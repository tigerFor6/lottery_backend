//package com.linglong.lottery_backend.lottery.match.common;
//
//import com.linglong.lottery_backend.lottery.match.service.TblMatchService;
//import org.apache.commons.collections.CollectionUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//import java.util.Set;
//
//@Component
//public class Scheduied {
//    private static final Logger logger = LoggerFactory.getLogger(Scheduied.class);
//
//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;
//
//    @Autowired
//    TblMatchService tblMatchService;
//
//    @Scheduled(cron = "1 0 0 * * ?")
//    public void executeFileDownLoadTask() {
//
//        Set<String> keys = redisTemplate.keys(Code.Redis.TBL_MATCH_LIST_KEY + "*");
//
//        //清除比赛列表缓存
//        if (CollectionUtils.isNotEmpty(keys)) {
//            redisTemplate.delete(keys);
//        }
//
//        //重新加入比赛列表缓存
//        //足球
//        tblMatchService.findMatchListByMatchTypeAndMatchStatus(Integer.valueOf(1), Integer.valueOf(0));
//        //篮球
//        tblMatchService.findMatchListByMatchTypeAndMatchStatus(Integer.valueOf(2), Integer.valueOf(1));
//
//        logger.info("在" + new Date() + "时重置了比赛列表缓存");
//
//    }
//}
