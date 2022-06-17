package com.linglong.lottery_backend.message.quartz;

import com.linglong.lottery_backend.message.quartz.job.*;
import com.linglong.lottery_backend.message.quartz.utils.QuartzJobUtils;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/7/1
 */

@Component
public class QuartzJobManager {
    @Autowired
    private Scheduler scheduler;

    /**
     * 初始化任务的创建
     * 判断任务是否存在，不存在则创建
     */
    @PostConstruct
    public void initJob() {
        //每六秒钟执行一次
        QuartzJobUtils.createJob(scheduler, TicketQuartzJob.class.getSimpleName(), TicketQuartzJob.class,
                "0/6 * * * * ? ");
        //每六秒钟执行一次
        QuartzJobUtils.createJob(scheduler, Shx115SetLotteryQuartzJob.class.getSimpleName(), Shx115SetLotteryQuartzJob.class,
                "0/6 * * * * ? ");
        //每六秒钟执行一次
        QuartzJobUtils.createJob(scheduler, TicketStatusByAOLIQuartzJob.class.getSimpleName(), TicketStatusByAOLIQuartzJob.class,
                "0/6 * * * * ? ");
        //每六秒钟执行一次
        QuartzJobUtils.createJob(scheduler, WaitPushTicketJob.class.getSimpleName(), WaitPushTicketJob.class,
                "0/6 * * * * ? ");
        //每小时执行一次
        QuartzJobUtils.createJob(scheduler, MatchQuartzJob.class.getSimpleName(), MatchQuartzJob.class,
                "0 0 0/1 * * ? ");
        //每天早上十点执行一次
        QuartzJobUtils.createJob(scheduler, ActivityQuartzJob.class.getSimpleName(), ActivityQuartzJob.class,
                "0 0 10 * * ? ");
        //每天凌晨执行一次
        QuartzJobUtils.createJob(scheduler, CouponQuartzJob.class.getSimpleName(), CouponQuartzJob.class,
                "0 0 0 * * ? ");
        //周日、周二、周四21:10 - 21:20 执行两次
        QuartzJobUtils.createJob(scheduler, TicketWaitPushSSQQuartzJob.class.getSimpleName(), TicketWaitPushSSQQuartzJob.class,
                "0 30-40/5 21 ? * 2,4,7 ");
        //每天早上8点，将所有 待出票-3状态的票 改为 0
        QuartzJobUtils.createJob(scheduler, TicketWaitPushDPPQuartzJob.class.getSimpleName(), TicketWaitPushDPPQuartzJob.class,
                "0 30-50/4 10 * * ? ");
    }
}
