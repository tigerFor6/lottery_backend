package com.linglong.lottery_backend.activity.ctrl;

import com.alibaba.fastjson.JSON;
import com.linglong.lottery_backend.activity.constant.ActivityConstant;
import com.linglong.lottery_backend.activity.entity.TblActivity;
import com.linglong.lottery_backend.activity.entity.TblCoupon;
import com.linglong.lottery_backend.activity.entity.TblUserActivity;
import com.linglong.lottery_backend.activity.entity.TblUserCoupon;
import com.linglong.lottery_backend.activity.entity.bean.DrawActivity;
import com.linglong.lottery_backend.activity.rules.CouponRules;
import com.linglong.lottery_backend.activity.service.TblActivityService;
import com.linglong.lottery_backend.activity.service.TblUserActivityService;
import com.linglong.lottery_backend.activity.service.TblUserCouponService;
import com.linglong.lottery_backend.common.bean.LockKey;
import com.linglong.lottery_backend.listener.LotteryTags;
import com.linglong.lottery_backend.listener.MqTopicConstant;
import com.linglong.lottery_backend.lottery.match.common.Result;
import com.linglong.lottery_backend.lottery.match.common.ResultGenerator;
import com.linglong.redisson.configure.annotations.LockAction;
import com.linglong.redisson.configure.util.LockType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/activity")
@Slf4j
public class ActivityController {

    @Autowired
    TblActivityService tblActivityService;

    @Autowired
    TblUserActivityService tblUserActivityService;

    @Resource
    RocketMQTemplate rocketMQTemplate;

    @Autowired
    TblUserCouponService userCouponService;

    /**
     * 参与活动
     * @param activityId
     * @param userId
     * @return
     */
    @PostMapping("draw/{activityId}")
    @LockAction(key = LockKey.lockActivity, value = "#activityId", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public Result draw(@PathVariable("activityId") Long activityId,@SessionAttribute String userId) {
        TblActivity activity = tblActivityService.findTblActivityByActivityId(activityId);
        if(null == activity){
            return ResultGenerator.genFailResult("活动不存在!");
        }
        if (!activity.getEnable()){
            return ResultGenerator.genFailResult("活动已关闭!");
        }
        Long now  = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();

        if (now < activity.getStartTime().getTime()){
            return ResultGenerator.genFailResult("活动未开始!");
        }

        if (now > activity.getEndTime().getTime()){
            return ResultGenerator.genFailResult("活动已截止!");
        }

        if (StringUtils.isBlank(userId)){
            return ResultGenerator.genFailResult("请输入正确的手机号!");
        }
        TblUserActivity userActivity = null;
        if (ActivityConstant.activity_type_register_unique.equals(activity.getType())){
            List<TblUserActivity> userActivitys = tblUserActivityService.findUserActivityByUserIdAndActivityTypeAndStatus(Long.parseLong(userId), activity.getType(), ActivityConstant.activity_type_register_unique);
            if (!userActivitys.isEmpty()){
                return ResultGenerator.genFailResult("您已参与过此活动!");
            }
        }else{
            userActivity = tblUserActivityService.findUserActivityByUserIdAndActivityId(Long.parseLong(userId),activityId);
            if (null != userActivity && userActivity.getStatus().equals(1)){
                return ResultGenerator.genFailResult("您已参与过此活动!");
            }
            if (StringUtils.isBlank(activity.getRules())){
                return ResultGenerator.genFailResult("此类型活动规则未定义!");
            }
        }
        //参与活动
        //int status = 0;
        try {
            if (null == userActivity){
                userActivity = new TblUserActivity();
                userActivity.setActivityId(activityId);
                userActivity.setActivityType(activity.getType());
                userActivity.setUserId(Long.valueOf(userId));
                if (ActivityConstant.activity_type_prize_unique.equals(activity.getType())){
                    userActivity.setStatus(1);
                }
                tblUserActivityService.saveUserActivity(userActivity);
                if(ActivityConstant.activity_type_prize_unique.equals(activity.getType())){
                    return ResultGenerator.genSuccessResult("参与活动成功!");
                }
            }

            DrawActivity param = new DrawActivity();
            param.setActivity(activity);
            param.setUserId(Long.parseLong(userId));
            rocketMQTemplate.asyncSend(MqTopicConstant.lotteryTopic+":" + LotteryTags.coupon,
                    MessageBuilder.withPayload(JSON.toJSONString(param)).build(),
                    new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            log.info("send to " + LotteryTags.coupon + " success body:{}" + sendResult.toString());
                        }

                        @Override
                        public void onException(Throwable throwable) {
                            log.info("send to " + LotteryTags.coupon + " fail:", throwable);
                            throw new RuntimeException(throwable);
                        }
            });
            //rocketMQTemplate.a("lottery:" + LotteryTags.coupon, MessageBuilder.withPayload(JSON.toJSONString(param)).build());
            // tblActivityService.participateInActivity(activity,Long.parseLong(userId));
        }catch (Exception e){
            log.error("error:",e);
            //status = 2;
            return ResultGenerator.genFailResult("参与活动失败!");
        }
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 参与过活动
     * @param activityId
     * @param userId
     * @return
     */
    @GetMapping("drawResult/{activityId}")
    public Result drawResult(@PathVariable("activityId") Long activityId,@SessionAttribute String userId) {
        TblUserActivity userActivity = tblUserActivityService.findUserActivityByUserIdAndActivityId(Long.parseLong(userId),activityId);
        if (null == userActivity){
            return ResultGenerator.genFailResult("您未参与过次活动!");
        }
        if (userActivity.getStatus().equals(2)){
            return ResultGenerator.genFailResult("手慢了，红包已被领光了!");
        }
        if (userActivity.getStatus().equals(0)){
            return ResultGenerator.genProcessingResult("处理中...");
        }
        List<TblUserCoupon> userCoupons = userCouponService.findByActivityIdAndUserId(activityId,Long.parseLong(userId));
        if (userCoupons.isEmpty()){
            return ResultGenerator.genFailResult("手慢了，红包已被领光了!");
        }
        return ResultGenerator.genSuccessResult(userCoupons.stream().map(e->{
            Map<String,String> rMap = new HashMap<>();
            rMap.put("couponName",e.getCouponName());
            rMap.put("amount",e.getCouponAmount().toString());
            String[] ruleArr = e.getCouponRules().split("@");
            for (int i = 0; i < ruleArr.length; i++) {
                String[] rules = ruleArr[i].split(":");
                if (rules[0].equals(CouponRules.use001)) {
                    rMap.put("use001", rules[1]);
                }
                if (rules[0].equals(CouponRules.use002)) {
                    rMap.put("use002", rules[1]);
                }
            }
            rMap.put("startTime", DateFormatUtils.format(e.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
            rMap.put("endTime", DateFormatUtils.format(e.getEndTime(), "yyyy/MM/dd HH:mm:ss"));
            return rMap;
        }));
    }
    /**
     * 主页弹窗可参与活动列表
     * @param userId
     * @return
     */
    @GetMapping("participate/list")
    public Result participateList(@SessionAttribute(required = false) String userId) {
        List<TblActivity> tblActivities = tblActivityService.findParticipateList();
        List<TblActivity> activities = tblActivities.stream()
                .filter(e -> e.getEnable())
                .filter(e -> participateActivities(e, userId)).collect(Collectors.toList());
        List<Map<String,Object>> resList = new ArrayList<Map<String,Object>>();
        for (TblActivity  activity: activities){
            List<TblCoupon> tblCoupons = tblActivityService.findTblCouponsByActivityId(activity.getActivityId());
            if (tblCoupons.isEmpty()){
                return ResultGenerator.genSuccessResult();
            }
            Map<String,Object> resp = new HashMap<>();
            resp.put("activityId",String.valueOf(activity.getActivityId()));
            resp.put("activityName",activity.getName());
            resp.put("level",activity.getLevel());
            resp.put("type",activity.getType());
            resp.put("coupons", tblCoupons.stream().map(e->{
                Map<String,String> rMap = new HashMap<>();
                rMap.put("couponName",e.getName());
                rMap.put("amount",e.getAmount().toString());
                String[] ruleArr = e.getRules().split("@");
                for (int i = 0; i < ruleArr.length; i++) {
                    String[] rules = ruleArr[i].split(":");
                    if (rules[0].equals(CouponRules.use001)){
                        rMap.put("use001",rules[1]);
                    }
                    if (rules[0].equals(CouponRules.use002)){
                        rMap.put("use002",rules[1]);
                    }
                }
                rMap.put("startTime", DateFormatUtils.format(e.getStartTime(),"yyyy-MM-dd"));
                rMap.put("endTime",DateFormatUtils.format(e.getEndTime(),"yyyy-MM-dd"));
                return rMap;
            }).collect(Collectors.toList()));
            resList.add(resp);
        }
        return ResultGenerator.genSuccessResult(resList);
    }

    private boolean participateActivities(TblActivity e, String userId) {
        if (StringUtils.isBlank(userId)){
            return true;
        }
        if (ActivityConstant.activity_type_register_unique.equals(e.getType())){
            return tblUserActivityService.countByUserIdAndActivityType(Long.parseLong(userId),e.getType()) == 0;
        }else{
            return tblUserActivityService.countByActivityIdAndUserId(e.getActivityId(),Long.parseLong(userId)) == 0;
        }
    }



    @GetMapping("details/{activityId}")
    public Result details(@PathVariable("activityId") Long activityId) {
        TblActivity activity = tblActivityService.findTblActivityByActivityId(activityId);
        if(null == activity){
            return ResultGenerator.genFailResult("活动不存在!");
        }
        return ResultGenerator.genSuccessResult(activity);
    }


    @GetMapping("/qualify/{activityId}")
    public Result qualify(@PathVariable("activityId")  Long activityId,@SessionAttribute(required = false) String userId) {
        TblActivity activity = tblActivityService.findTblActivityByActivityId(activityId);
        if (null == activity) {
            return ResultGenerator.genFailResult("活动不存在");
        }
        if (!activity.getEnable()) {
            return ResultGenerator.genFailResult("活动已关闭");
        }
        Long now = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();

        if (now < activity.getStartTime().getTime()) {
            return ResultGenerator.genFailResult("活动未开始");
        }

        if (now > activity.getEndTime().getTime()) {
            return ResultGenerator.genFailResult("活动已截止");
        }

        if (StringUtils.isBlank(userId)){
            return ResultGenerator.genSuccessResult();
        }
        TblUserActivity userActivity = tblUserActivityService.findUserActivityByUserIdAndActivityId(Long.parseLong(userId), activity.getActivityId());
        if (null != userActivity && userActivity.getStatus().equals(1)) {
            return ResultGenerator.genFailResult("您已领取资格");
        }
        return ResultGenerator.genSuccessResult();
    }
}
