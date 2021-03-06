

package com.linglong.lottery_backend;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.linglong.lottery_backend.activity.entity.*;
import com.linglong.lottery_backend.activity.rules.ActivityRules;
import com.linglong.lottery_backend.activity.rules.CouponRules;
import com.linglong.lottery_backend.activity.service.TblActivityService;
import com.linglong.lottery_backend.activity.service.TblUserActivityService;
import com.linglong.lottery_backend.activity.service.TblUserCouponRecordService;
import com.linglong.lottery_backend.activity.service.TblUserCouponService;
import com.linglong.lottery_backend.message.sms.SmsSend;
import com.linglong.lottery_backend.message.sms.repository.SmsRepository;
import com.linglong.lottery_backend.order.entity.Order;
import com.linglong.lottery_backend.order.entity.OrderDetails;
import com.linglong.lottery_backend.order.entity.OrderDetailsInfo;
import com.linglong.lottery_backend.order.listener.bean.HitPrizeResult;
import com.linglong.lottery_backend.order.repository.OrderDetailsInfoRepository;
import com.linglong.lottery_backend.order.repository.OrderDetailsRepository;
import com.linglong.lottery_backend.order.repository.OrderRepository;
import com.linglong.lottery_backend.ticket.service.TblBettingTicketService;
import com.linglong.lottery_backend.user.account.entity.User;
import com.linglong.lottery_backend.user.account.repository.UserRepository;
import com.linglong.lottery_backend.utils.ArithUtil;
import com.linglong.lottery_backend.utils.IdWorker;
import com.linglong.lottery_backend.utils.OrderStatusUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

//

//
@RunWith(SpringRunner.class)
@SpringBootTest
public class LotteryBackendApplicationTests {
    //
    @Autowired
    private UserRepository userRepository;

    @Autowired
    TblBettingTicketService bettingTicketService;

    @Autowired
    TblUserActivityService tblUserActivityService;

    @Autowired
    TblActivityService tblActivityService;

    @Autowired
    private TblUserCouponService userCouponService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SmsSend smsSend;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderDetailsInfoRepository orderDetailsInfoRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private SmsRepository smsRepository;

    //???????????????????????????????????????????????????
    private static final BigDecimal multiple = new BigDecimal(100);

    @Test
    public void afterOpenAllPrizeStatus() {
        Long orderId = Long.valueOf("1153191214900711424");
        Integer state = 1;
        Integer hitStatus = 1;
        BigDecimal amount = new BigDecimal(9526000);
        try {
            //????????????
            Order order = orderRepository.findByOrderId(orderId);
            //??????????????????1,2
            if (order.getGameType() > 2){
                return;
            }
            String userId = order.getUserId();
            //User user = userRepository.findByUserId(userId);
            //????????????????????????
            List<TblUserActivity> userActivities = tblUserActivityService.findUserActivityByUserIdAndActivityTypeAndStatus(Long.parseLong(userId),2,1);
            if (userActivities.isEmpty()){
                return;
            }
            userActivities.forEach(e->{
                //???????????????????????????????????????
                Date orderCreatedTime = order.getCreatedTime();
                Date userActivityCreatedTime = e.getCreatedTime();
                if (orderCreatedTime.before(userActivityCreatedTime)){
                    return;
                }
                TblActivity activity = tblActivityService.findTblActivityByActivityId(e.getActivityId());
                if (null == activity){
                    return;
                }
                if (!activity.getEnable()){
                    return;
                }
                String chuanGuan = "";
                String matchId = "";
                String [] activityRuleArr = activity.getRules().split("@");
                for (String rules : activityRuleArr){
                    String[] rule = rules.split(":");
                    if (rule[0].equals(ActivityRules.draw001)){
                        matchId = rule[1];
                    }
                    if (rule[0].equals(ActivityRules.draw002)){
                        chuanGuan = rule[1];
                    }
                }
                List<Long> lotteryTypes = new ArrayList<Long>();
                lotteryTypes.add(Long.valueOf(1));
                lotteryTypes.add(Long.valueOf(2));
                List<Order> orders = orderRepository.findByFiveDaysOrder(userId,lotteryTypes);
                Order maxOrder = filterRules(orders,chuanGuan,matchId);
                if (null == maxOrder){
                    return;
                }
                OrderDetailsInfo info = maxOrder.getOrderDetailsInfo();
                String statusNum = OrderStatusUtil.getStatusNum(info);
                List<String> deliveredCodeList = Lists.newArrayList("12211", "23211");
                //???????????????
                if (deliveredCodeList.contains(statusNum)){
                    //??????
                    BigDecimal bonus = info.getBonus();
                    // ??????????????????????????????????????????
                    List<TblCoupon> tblCoupons = tblActivityService.findTblCouponsByActivityId(activity.getActivityId());
                    List<TblCoupon> coupons = tblCoupons.stream()
                            .filter(t -> t.getEnabled())
                            .filter(t -> verificationRule(t, Long.valueOf(userId)))
                            .filter(t -> compare(bonus, t))
                            .collect(Collectors.toList());
                    if (coupons.isEmpty()){
                        return;
                    }
                    sendCoupon(activity, coupons, Long.valueOf(userId));
                }
            });

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private Order filterRules(List<Order> orders, String chuanGuan, String matchId){
        orders = orders.stream().filter(data -> data.getOrderDetailsInfo().getChuanGuan().equals(chuanGuan)).collect(Collectors.toList());
        List<Order> targert = new ArrayList<Order>();
        for (Order od : orders){
            List<OrderDetails> details = od.getOrderDetailsList();
            List<Long> matchIds = details.stream().map(e -> e.getMatchId()).collect(Collectors.toList());
            if (matchIds.contains(Long.valueOf(matchId))){
                BigDecimal realAmount = ArithUtil.sub(od.getOrderFee(), od.getRefundFee());
                od.setRealAmount(realAmount);
                od.setBonus(od.getOrderDetailsInfo().getBonus());
                targert.add(od);
            }
        }
        if (targert.isEmpty()){
            return null;
        }
        Optional<Order> max = targert.stream().max(Comparator.comparing(Order::getRealAmount));
        if (max.isPresent()) {
            Order maxOrder = max.get();
            List<Order> orderList = targert.stream()
                    .filter(e -> e.getRealAmount().equals(maxOrder.getRealAmount()))
                    .collect(Collectors.toList());
            if (orderList.size() > 1){
                //?????????????????????????????????????????????????????????????????????????????????
                for (Order order : orderList){
                    OrderDetailsInfo info = order.getOrderDetailsInfo();
                    String statusNum = OrderStatusUtil.getStatusNum(info);
                    List<String> deliveredCodeList = Lists.newArrayList("12211", "23211");
                    //?????????????????????
                    if (!deliveredCodeList.contains(statusNum)){
                        return null;
                    }
                }
                //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                Optional<Order> endMax = orderList.stream().max(Comparator.comparing(Order::getBonus));
                if (endMax.isPresent()) {
                    Order endMaxOrder = endMax.get();
                    return endMaxOrder;
                }
            }else{
                return maxOrder;
            }
        }
        return null;
    }

    private void sendCoupon(TblActivity activity, List<TblCoupon> coupons, long userId){
        //????????????
        List<TblUserCoupon> userCouponList = userCouponService.findByActivityIdAndUserId(activity.getActivityId(), userId);
        //??????????????????????????????????????????
        if (!userCouponList.isEmpty()){
            return;
        }
        List<TblUserCoupon> userCoupons = coupons.stream()
                .map(e->{
                    TblUserCoupon userCoupon = new TblUserCoupon();
                    userCoupon.setUserId(userId);
                    userCoupon.setCouponId(e.getCouponId());
                    userCoupon.setCouponAmount(e.getAmount());
                    userCoupon.setCouponName(e.getName());
                    userCoupon.setCouponType(e.getType());
                    String [] ruleArr = e.getRules().split("@");
                    Set<String> ruleSet = new HashSet<>();
                    for (String rules:ruleArr){
                        String[] rule = rules.split(":");
                        if (rule[0].startsWith("10")){
                            continue;
                        }
                        String[] timeRule  = null;
                        if (rule[0].equals(CouponRules.use004)){
                            timeRule = rule[1].split(",");
                            Date startTime = DateUtils.addDays(new Date(),Integer.parseInt(timeRule[0]));
                            Date endTime = DateUtils.addDays(startTime,Integer.parseInt(timeRule[1]));
                            userCoupon.setStartTime(com.linglong.lottery_backend.activity.util.DateUtils.transToDate(startTime,0));
                            userCoupon.setEndTime(com.linglong.lottery_backend.activity.util.DateUtils.transToDate(endTime,-1));
                            //continue;
                        }else if (rule[0].equals(CouponRules.use003)){
                            timeRule = rule[1].split(",");
                            userCoupon.setStartTime(new Date(Long.parseLong(timeRule[0])));
                            userCoupon.setEndTime(new Date(Long.parseLong(timeRule[1])));
                            //continue;
                        }
                        ruleSet.add(rules);
                    }
                    userCoupon.setUserCouponId(idWorker.nextId());
                    userCoupon.setActivityId(activity.getActivityId());
                    userCoupon.setCouponRules(Joiner.on("@").join(ruleSet));
                    return userCoupon;
                }).collect(Collectors.toList());
        if (userCoupons.isEmpty()){
            throw new RuntimeException("??????????????????");
        }
        userCouponService.saveAll(userCoupons);
    }

    private boolean verificationRule(TblCoupon e,Long userId) {
        if (StringUtils.isBlank(e.getRules())){
            return false;
        }
        Iterable<String> ruleArr = Splitter.on("@").omitEmptyStrings().split(e.getRules());
        for (String rules:ruleArr){
            String[] rule = rules.split(":");
            if (rule[0].equals(CouponRules.draw001)){
                //????????????
                int total = e.getNumber().intValue();
                String [] countArr = rule[1].split(",");
                //????????????????????????
                int drawFrequency = Integer.parseInt(countArr[0]);
                //????????????????????????
                int drawNumber = Integer.parseInt(countArr[1]);
                //???????????????????????????
                Integer drawCount = userCouponService.findDrawCouponCountByCouponId(e.getCouponId());
                //???????????????????????????
                int userTotal = drawFrequency*drawNumber;
                //???????????????????????????????????????????????????
                if ((total-drawCount.intValue())<userTotal){
                    return false;
                }
                //??????????????????
                Integer count = userCouponService.findUserCouponCountByCouponIdAndUserId(e.getCouponId(),userId);
                int userDrawFrequency = count.intValue()/drawNumber;
                if (userDrawFrequency>=drawFrequency){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean compare(BigDecimal bonus, TblCoupon coupon){
        String [] ruleArr = coupon.getRules().split("@");
        for (String rules:ruleArr){
            String[] rule = rules.split(":");
            if (rule[0].startsWith("30")){
                String[] bonusRule  = null;
                if (rule[0].equals(CouponRules.use005)){
                    bonusRule = rule[1].split(",");
                    int start = Integer.parseInt(bonusRule[0]);
                    int end = Integer.parseInt(bonusRule[1]);
                    // 100-200
                    if (bonus.intValue() >=  start && bonus.intValue() < end){
                        return true;
                    }
                }
            }
        }
        return false;
    }


    //
//    public void contextLoads() {
//        String orderId = "1116292113630367744";
//        Map<String, Object> map = orderService.queryOrderDetails("1112382656055742464", Long.parseLong(orderId));
//        System.out.println(map);
//    }

    //
    @Test
    public void findOrderList() {
        String userId = "1115900146727653376";
       // Map<String, Object> map = orderService.queryOrderList(userId, 1,9);
       // System.out.println(map);
    }

    //
//    @Test
//    public void testDeadline() throws ParseException {
//        String deadline = "2019???04???14??? 02???30???";
//        Boolean aBoolean = dateFormatUtil.compareTime(deadline, new Date());
//        System.out.println(aBoolean);
//    }

    //
    @Test
    public void testIdworker() {
        System.out.println(idWorker.nextId());
    }


    //
    @Test
    public void testproducer2() {
        String userId = "1116997875117727744";
    }

    @Test
    public void testProducer() throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        DefaultMQProducer producer = new DefaultMQProducer("lottery_im");
        producer.setSendMsgTimeout(10000);
        producer.setNamesrvAddr("39.98.69.232:9876");
        producer.start();
        HitPrizeResult prizeResult = new HitPrizeResult();
        prizeResult.setBonus(new BigDecimal(20));//??????
        prizeResult.setHitPrizeStatus(2);//????????????
        prizeResult.setOrderId(1120659791342080000L);

        HitPrizeResult prizeResult3 = new HitPrizeResult();
        prizeResult3.setBonus(new BigDecimal(14));//??????
        prizeResult3.setHitPrizeStatus(1);//????????????
        prizeResult3.setOrderId(1120634556425310208L);

        HitPrizeResult prizeResult5 = new HitPrizeResult();
        prizeResult5.setBonus(new BigDecimal(10));//??????
        prizeResult5.setHitPrizeStatus(1);//????????????
        prizeResult5.setOrderId(1120620623647870976L);

        HitPrizeResult prizeResult7 = new HitPrizeResult();
        prizeResult7.setBonus(new BigDecimal(0));//?????????
        prizeResult7.setHitPrizeStatus(1);
        prizeResult7.setOrderId(1120661584872607744L);

        List<HitPrizeResult> list = Arrays.asList(prizeResult, prizeResult3, prizeResult5, prizeResult7);

        String jsonString = JSON.toJSONString(list);
        Message message = new Message("lottery", "bettingResult", jsonString.getBytes(StandardCharsets.UTF_8));
        SendResult sendResult = producer.send(message);
        System.out.println(sendResult.getSendStatus());
    }

//    @Test
//    public void updateOpenAndHitPrizeStatus() {
//
//        HitPrizeResult prizeResult = new HitPrizeResult();
//        prizeResult.setBonus(new BigDecimal(20));
//        prizeResult.setHitPrizeStatus(2);
//        prizeResult.setOrderId(1120526480430993408L);
//
//        HitPrizeResult prizeResult3 = new HitPrizeResult();
//        prizeResult3.setBonus(new BigDecimal(20));
//        prizeResult3.setHitPrizeStatus(2);
//        prizeResult3.setOrderId(1120526575901741056L);
//
//        HitPrizeResult prizeResult5 = new HitPrizeResult();
//        prizeResult5.setBonus(new BigDecimal(20));
//        prizeResult5.setHitPrizeStatus(2);
//        prizeResult5.setOrderId(1120526738196140032L);
//
//        HitPrizeResult prizeResult7 = new HitPrizeResult();
//        prizeResult7.setBonus(new BigDecimal(20));
//        prizeResult7.setHitPrizeStatus(2);
//        prizeResult7.setOrderId(1120524389289431040L);
//
//        List<HitPrizeResult> list = Arrays.asList(prizeResult, prizeResult3, prizeResult5, prizeResult7);
//        orderService.updateOpenAndHitPrizeStatus(list);
//    }
}


