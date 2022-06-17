

package com.linglong.lottery_backend.activity;

import com.alibaba.fastjson.JSON;
import com.linglong.lottery_backend.activity.entity.TblActivity;
import com.linglong.lottery_backend.activity.entity.TblUserCoupon;
import com.linglong.lottery_backend.activity.service.TblActivityService;
import com.linglong.lottery_backend.activity.service.TblUserCouponService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

//

//
@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivityTests {

    @Autowired
    TblUserCouponService userCouponService;

    @Autowired
    TblActivityService tblActivityService;

    /**
     * 用户红包列表
     */
    @Test
    public void findParticipateList() {
        List<TblActivity> tblActivities = tblActivityService.findParticipateList();
        System.out.println(JSON.toJSONString(tblActivities));
    }

    /**
     * 用户红包列表
     */
    @Test
    public void findListByParam() {
        TblUserCoupon param = new TblUserCoupon();
        param.setUserId(Long.parseLong("1132990115589787666"));
//        param.setActivityId(Long.parseLong("1122325591568289792"));
//        param.setCouponStatus(Integer.parseInt("1"));
        String status ="0";
        Page<TblUserCoupon> page = userCouponService.findListByParam(param,status,0,10);

        System.out.println(JSON.toJSONString(page));
    }

    /**
     * 可使用红包
     */
    @Test
    public void findAllowedToUseCoupon(){
        Long userId = Long.parseLong("1132990115589787666");
        BigDecimal amount = new BigDecimal(200);
        Integer gameType = 1;
        List<TblUserCoupon> userCoupons = userCouponService.findAllowedToUseCoupon(userId,amount,gameType);
    }

    /**
     * 使用红包
     */
    @Test
    public void useCoupon(){
        Long userId = Long.parseLong("1132990115589787666");
        BigDecimal amount = new BigDecimal(200);
        Integer gameType = 1;
        Long userCouponId = Long.parseLong("1132990115589787666");
        Long orderId = Long.parseLong("1132990115589787666");
        int userCoupons = userCouponService.useCoupon(userId,userCouponId,orderId,amount,gameType);
    }

    /**
     * 补偿红包
     */
    @Test
    public void compensateCoupon() throws CloneNotSupportedException {
        Long userCouponId = Long.parseLong("1132990115589787666");
        BigDecimal amount = new BigDecimal(200);
        Long orderId = Long.parseLong("1132990115589787666");
        Long voucherNo = Long.parseLong("1132990115589787666");
        Boolean userCoupons = userCouponService.compensateCoupon(userCouponId,amount,orderId);
    }

}


