package com.linglong.lottery_backend.message.quartz.job;

import com.linglong.lottery_backend.activity.job.ScheduledTasks;
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
 * @since: 2019/7/3
 */
@DisallowConcurrentExecution
public class CouponQuartzJob extends QuartzJobBean {

    @Autowired
    private ScheduledTasks scheduledTasks;
    private static final Logger LOGGER = LoggerFactory.getLogger(CouponQuartzJob.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
//        LOGGER.info("-----CouponQuartzJob start");
        try {
            scheduledTasks.updateInvalidCoupon();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
