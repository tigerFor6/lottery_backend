package com.linglong.lottery_backend.message.quartz.job;

import com.linglong.lottery_backend.ticket.task.ScheduledTask;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/7/1
 */
@DisallowConcurrentExecution
public class Shx115SetLotteryQuartzJob extends QuartzJobBean {

    @Autowired
    private ScheduledTask scheduledTask;
    private static final Logger LOGGER = LoggerFactory.getLogger(Shx115SetLotteryQuartzJob.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        try {
            scheduledTask.shx115SetLotteryTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
