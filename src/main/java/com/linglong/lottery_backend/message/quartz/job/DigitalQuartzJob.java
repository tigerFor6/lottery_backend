//package com.linglong.lottery_backend.message.quartz.job;
//
//import com.linglong.lottery_backend.lottery.digital.cache.TblDigitalResultsCache;
//import org.quartz.DisallowConcurrentExecution;
//import org.quartz.JobExecutionContext;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.quartz.QuartzJobBean;
//
///**
// * Description
// *
// * @author xiaohu.liu
// * @since: 2019/7/2
// */
//@DisallowConcurrentExecution
//public class DigitalQuartzJob extends QuartzJobBean {
//
//    @Autowired
//    TblDigitalResultsCache tblDigitalResultsCache;
//    private static final Logger LOGGER = LoggerFactory.getLogger(DigitalQuartzJob.class);
//
//    @Override
//    protected void executeInternal(JobExecutionContext jobExecutionContext) {
////        LOGGER.info("-----DigitalQuartzJob start");
//        tblDigitalResultsCache.refresh();
//    }
//}
