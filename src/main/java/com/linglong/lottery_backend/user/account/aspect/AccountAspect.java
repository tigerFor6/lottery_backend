package com.linglong.lottery_backend.user.account.aspect;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.linglong.lottery_backend.activity.entity.TblActivity;
import com.linglong.lottery_backend.activity.entity.TblCoupon;
import com.linglong.lottery_backend.activity.entity.TblUserActivity;
import com.linglong.lottery_backend.activity.entity.TblUserCoupon;
import com.linglong.lottery_backend.activity.rules.ActivityRules;
import com.linglong.lottery_backend.activity.rules.CouponRules;
import com.linglong.lottery_backend.activity.service.TblActivityService;
import com.linglong.lottery_backend.activity.service.TblUserActivityService;
import com.linglong.lottery_backend.activity.service.TblUserCouponService;
import com.linglong.lottery_backend.message.jpush.JpushSendMsgService;
import com.linglong.lottery_backend.message.sms.SmsSend;
import com.linglong.lottery_backend.message.sms.cache.TblSmsTempleCache;
import com.linglong.lottery_backend.message.sms.entity.TblSms;
import com.linglong.lottery_backend.message.sms.repository.SmsRepository;
import com.linglong.lottery_backend.order.entity.Order;
import com.linglong.lottery_backend.order.entity.OrderDetails;
import com.linglong.lottery_backend.order.entity.OrderDetailsInfo;
import com.linglong.lottery_backend.order.repository.OrderDetailsInfoRepository;
import com.linglong.lottery_backend.order.repository.OrderDetailsRepository;
import com.linglong.lottery_backend.order.repository.OrderRepository;
import com.linglong.lottery_backend.ticket.cache.GameCache;
import com.linglong.lottery_backend.ticket.constant.WordConstant;
import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import com.linglong.lottery_backend.ticket.service.TblBettingTicketService;
import com.linglong.lottery_backend.user.account.entity.User;
import com.linglong.lottery_backend.user.account.repository.UserRepository;
import com.linglong.lottery_backend.utils.ArithUtil;
import com.linglong.lottery_backend.utils.IdWorker;
import com.linglong.lottery_backend.utils.OrderStatusUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/7/9
 */
@Aspect
@Component
@Slf4j
public class AccountAspect {

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
    private JpushSendMsgService jpushSendMsgService;

    @Autowired
    private SmsRepository smsRepository;

    //???????????????????????????????????????????????????
    private static final BigDecimal multiple = new BigDecimal(100);

//    @Pointcut(value = "execution(* com.linglong.lottery_backend.user.account.service.TblUserBalanceService.reward(..)) && args(userId, voucherNo, amount)")
//    public void reward(String userId, Long voucherNo, BigDecimal amount) {
//    }
//
//    @After(value = "reward(userId, voucherNo, amount)")
//    public void afterReward(String userId, Long voucherNo, BigDecimal amount) {
//        try {
//            //??????????????????
//            log.info("??????????????????:userId"+userId+"voucherNo:"+voucherNo+"amount:"+amount);
//            User user = userRepository.findByUserId(userId);
//            BettingTicket ticket = bettingTicketService.findByTicketId(voucherNo);
//            String temple = TblSmsTempleCache.getSmsTemple(1);
//            Boolean flag = false;
//            if ("1".equals(ticket.getPrizeStatus())){
//                flag = true;
//            }
//            if (flag){
//                temple = TblSmsTempleCache.getSmsTemple(3);
//            }
//            temple = temple.replace("#gameType#", GameCache.getGame(ticket.getGameType()).getGameName());
//            String playType = ticket.getPlayType();
//            temple = temple.replace("{play_type}", WordConstant.PLAY_MAP.get(playType));
//            if (!flag){
//                temple = temple.replace("#amount#", String.valueOf(amount.divide(multiple)));
//            }
//            TblSms sms = new TblSms();
//            sms.setSmsId(idWorker.nextId());
//            sms.setBusId(voucherNo);
//            sms.setUserId(Long.valueOf(user.getUserId()));
//            sms.setTelphone(user.getPhone());
//            sms.setContent(temple);
//            sms.setStatus(0);
//            smsRepository.saveAndFlush(sms);
////            smsSend.send(user.getPhone(),temple);
//
//            jpushSendMsgService.sendByUserId(user.getUserId(), temple);
//        } catch (Exception e) {
//            log.error("?????????????????????voucherNo:"+voucherNo+"error:"+e);
//            e.printStackTrace();
//        }
//    }

    public void addSms(Order order){
        try {
            String temple = TblSmsTempleCache.getSmsTemple(1);
            List<String> deliveredCodeList = Lists.newArrayList("12211", "23211");
            OrderDetailsInfo info = order.getOrderDetailsInfo();
            String statusNum = OrderStatusUtil.getStatusNum(info);
            //???????????????
            if (deliveredCodeList.contains(statusNum) || statusNum.contains("99")) {
                if (statusNum.contains("99")){
                    //??????
                    temple = TblSmsTempleCache.getSmsTemple(3);
                }else {
                    BigDecimal bonus = order.getOrderDetailsInfo().getBonus();
                    if (null != bonus && bonus.intValue() > 0 ){
                        temple = temple.replace("#amount#", String.valueOf(bonus.divide(multiple)));
                    }
                }
                temple = temple.replace("#gameType#", GameCache.getGame(order.getGameType()).getGameName());
                //???????????????????????????
                temple = temple.replace("{play_type}", "");
                TblSms tblSms = smsRepository.findByBusId(order.getOrderId());
                if (null == tblSms){
                    TblSms sms = new TblSms();
                    sms.setSmsId(idWorker.nextId());
                    sms.setBusId(order.getOrderId());
                    sms.setUserId(Long.valueOf(order.getUserId()));
                    User user = userRepository.findByUserId(order.getUserId());
                    if (null == user){
                        log.warn("?????????????????????????????????orderId:"+order.getOrderId());
                        return;
                    }
                    sms.setTelphone(user.getPhone());
                    sms.setContent(temple);
                    sms.setStatus(0);
                    smsRepository.saveAndFlush(sms);
                    jpushSendMsgService.sendByUserId(user.getUserId(), temple);
                }
            }
        }catch (Exception e){
            log.error("?????????????????????orderId:"+order.getOrderId()+"error:"+e);
            e.printStackTrace();
        }
    }

    @Pointcut(value = "execution(* com.linglong.lottery_backend.order.service.IOrderService.updateOpenAllPrizeStatus(..)) && args(orderId, state, hitStatus, amount)")
    public void updateOpenAllPrizeStatus(Long orderId, Integer state, Integer hitStatus,BigDecimal amount) {
    }

    @Async("taskExecutor")
    @After(value = "updateOpenAllPrizeStatus(orderId, state, hitStatus,amount)")
    public void afterOpenAllPrizeStatus(Long orderId, Integer state, Integer hitStatus, BigDecimal amount) {
        try {
            //????????????
            Order order = orderRepository.findByOrderId(orderId);
            //???????????????????????????
            addSms(order);
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
                log.info("??????????????????????????????maxOrderId:"+maxOrder.getOrderId());
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
        log.info("??????????????????");
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

}