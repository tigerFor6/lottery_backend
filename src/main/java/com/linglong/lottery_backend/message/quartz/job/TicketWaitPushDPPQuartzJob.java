package com.linglong.lottery_backend.message.quartz.job;

import com.linglong.lottery_backend.ticket.task.ScheduledTask;
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
public class TicketWaitPushDPPQuartzJob extends QuartzJobBean {

    @Autowired
    private ScheduledTask scheduledTask;
    private static final Logger LOGGER = LoggerFactory.getLogger(TicketWaitPushDPPQuartzJob.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
//        LOGGER.info("-----TicketWaitPushDPPQuartzJob start");
        try {
            scheduledTask.waitPushDPPTicketListTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}