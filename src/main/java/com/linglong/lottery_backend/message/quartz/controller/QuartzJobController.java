package com.linglong.lottery_backend.message.quartz.controller;

import com.linglong.lottery_backend.message.quartz.utils.QuartzJobUtils;
import com.linglong.lottery_backend.order.model.Result;
import com.linglong.lottery_backend.order.model.StatusCode;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/5/22
 */
@RestController
@RequestMapping(value = "/api/job")
public class QuartzJobController {

    @Autowired
    private Scheduler scheduler;


    /**
     * 获取最近一期数字彩信息
     */
    @PostMapping("/modifyJobTime")
    public Result modifyJobTime(@RequestParam("triggerName") String triggerName, @RequestParam("triggerGroupName") String triggerGroupName, @RequestParam("cron") String cron) {
        //修改定时任务执行时间
        QuartzJobUtils.modifyJobTime(scheduler, triggerName, triggerGroupName, cron);
        return new Result(StatusCode.OK.getCode(), "修改成功", null);
    }

    @PostMapping("/pauseJob")
    public Result pauseJob(@RequestParam("triggerName") String triggerName, @RequestParam("triggerGroupName") String triggerGroupName) {
        //暂停一个job
        QuartzJobUtils.pauseJob(scheduler, triggerName, triggerGroupName);
        return new Result(StatusCode.OK.getCode(), "修改成功", null);
    }

    @PostMapping("/resumeJob")
    public Result resumeJob(@RequestParam("triggerName") String triggerName, @RequestParam("triggerGroupName") String triggerGroupName) {
        //恢复一个任务
        QuartzJobUtils.resumeJob(scheduler, triggerName, triggerGroupName);
        return new Result(StatusCode.OK.getCode(), "修改成功", null);
    }

    @PostMapping("/closeJob")
    public Result closeJob(@RequestParam("jobName") String jobName, @RequestParam("jobGroupName") String jobGroupName, @RequestParam("triggerName") String triggerName, @RequestParam("triggerGroupName") String triggerGroupName) {
        //移除一个任务
        QuartzJobUtils.close(scheduler, jobName, jobGroupName, triggerName, triggerGroupName);
        return new Result(StatusCode.OK.getCode(), "修改成功", null);
    }
}
