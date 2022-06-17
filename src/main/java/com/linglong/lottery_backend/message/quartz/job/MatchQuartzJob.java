package com.linglong.lottery_backend.message.quartz.job;

import com.linglong.lottery_backend.lottery.match.task.RedisAsyncExecutorTask;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/7/1
 */
@DisallowConcurrentExecution
public class MatchQuartzJob extends QuartzJobBean {

    @Autowired
    private RedisAsyncExecutorTask redisAsyncExecutorTask;
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchQuartzJob.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
//        LOGGER.info("-----MatchQuartzJob start");
        try {
            redisAsyncExecutorTask.updateMatchRedisTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
