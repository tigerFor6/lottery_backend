package com.linglong.lottery_backend.activity.job;


import com.linglong.lottery_backend.activity.entity.TblUserCoupon;
import com.linglong.lottery_backend.activity.service.TblUserCouponService;
import com.linglong.lottery_backend.message.jpush.JpushSendMsgService;
import com.linglong.lottery_backend.message.sms.SmsSend;
import com.linglong.lottery_backend.message.sms.cache.TblSmsTempleCache;
import com.linglong.lottery_backend.message.sms.entity.TblSms;
import com.linglong.lottery_backend.message.sms.repository.SmsRepository;
import com.linglong.lottery_backend.user.account.entity.User;
import com.linglong.lottery_backend.user.account.repository.UserRepository;
import com.linglong.lottery_backend.utils.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("couponScheduledTasks")
@Slf4j
public class ScheduledTasks {

    @Autowired
    TblUserCouponService userCouponService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SmsRepository smsRepository;

    @Autowired
    private SmsSend smsSend;

    @Autowired
    private JpushSendMsgService jpushSendMsgService;

    @Autowired
    private IdWorker idWorker;

    public void updateInvalidCoupon() {
        log.info("updateInvalidCoupon 将过期的红包置为无效状态");
        userCouponService.updateInvalidCoupon();
        //logger.info("无效订单更新完毕");
    }

    /**
     * 24小时内快过期红包短信提醒
     */
    public void sendExpireCouponMessage(){
        List<TblUserCoupon> expireCoupon = userCouponService.findExpireCoupon();
        Map<Long, List<TblUserCoupon>> group = expireCoupon.stream().collect(Collectors.groupingBy(TblUserCoupon::getUserId));
        group.forEach((k,v)->{
            Map<Long, List<TblUserCoupon>> map = v.stream().collect(Collectors.groupingBy(TblUserCoupon::getActivityId));
            map.forEach((k1,v1)->{
                User user = userRepository.findByUserId(String.valueOf(k));
                TblSms sms = smsRepository.findFirstByBusIdAndUserId(k1,k);
                if (null == sms){
                    sms = new TblSms();
                    sms.setSmsId(idWorker.nextId());
                    sms.setBusId(k1);
                    sms.setUserId(k);
                    sms.setTelphone(user.getPhone());
                    sms.setContent(TblSmsTempleCache.getSmsTemple(2));
                    sms.setStatus(0);
                    sms.setCreatedTime(new Date());
                    sms.setUpdatedTime(new Date());
                    smsRepository.saveAndFlush(sms);
                    jpushSendMsgService.sendByUserId(user.getUserId(), sms.getContent());
                }
            });
        });

    }

    /**
     * 短信发送
     */
    public void sendSms(){
        List<TblSms> smsList = smsRepository.findAll();
        List<TblSms> tblSmsList = smsList.stream().filter(data -> data.getStatus() == 0).collect(Collectors.toList());
        for(TblSms sms : tblSmsList){
            String resultCode = smsSend.send(sms.getTelphone(),sms.getContent());
            if ("0".equals(resultCode)){
                sms.setStatus(1);
            }else{
                sms.setStatus(2);
            }
            sms.setUpdatedTime(new Date());
            smsRepository.saveAndFlush(sms);
        }
    }
}
