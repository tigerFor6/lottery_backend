package com.linglong.lottery_backend.activity.ctrl;

import com.linglong.lottery_backend.activity.entity.TblUserCoupon;
import com.linglong.lottery_backend.activity.rules.CouponRules;
import com.linglong.lottery_backend.activity.service.TblUserCouponService;
import com.linglong.lottery_backend.lottery.match.common.Result;
import com.linglong.lottery_backend.lottery.match.common.ResultGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@RestController
@RequestMapping(value = "/api/coupon")
@Slf4j
public class CouponController {

    @Autowired
    TblUserCouponService userCouponService;

    /**
     * 用户红包列表查询
     * @param userId
     * @param activityId
     * @param status
     * @param page
     * @param size
     * @return
     */
    @GetMapping("list")
    public Result list(@SessionAttribute String userId
            , @RequestParam(name = "activityId",required = false) String activityId
            , @RequestParam(name = "status",required = false) String status
            , @RequestParam(name = "page") Integer page
            , @RequestParam(name = "size") Integer size) {
        if (StringUtils.isBlank(userId)){
            return ResultGenerator.genFailResult("未获取到登录用户");
        }
        TblUserCoupon param = new TblUserCoupon();
        param.setUserId(Long.parseLong(userId));
        if (StringUtils.isNotBlank(activityId)){
            param.setActivityId(Long.parseLong(activityId));
        }
        Page<TblUserCoupon> userCoupons = userCouponService.findListByParam(param,status,page,size);
        return ResultGenerator.genSuccessResult(userCoupons.map(e->{
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
            rMap.put("status",e.getCouponStatus().toString());
            rMap.put("startTime", DateFormatUtils.format(e.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
            rMap.put("endTime", DateFormatUtils.format(e.getEndTime(), "yyyy/MM/dd HH:mm:ss"));
            return rMap;
        }));
    }

    /**
     * 即将失效红包
     * @param userId
     * @return
     */
    @GetMapping("count/expire")
    public Result countExpire(@SessionAttribute String userId) {
        if (StringUtils.isBlank(userId)){
            return ResultGenerator.genFailResult("未获取到登录用户");
        }
        Map<String,Object> res = new HashMap<>();

        int expireCount = userCouponService.countExpireByUserId(Long.parseLong(userId));
        int notUseCount = userCouponService.countByUserIdAndCouponStatus(Long.parseLong(userId),0);
        res.put("expire",expireCount);
        res.put("notUse",notUseCount);
        return ResultGenerator.genSuccessResult(res);
    }
}
