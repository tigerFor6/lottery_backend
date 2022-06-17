package com.linglong.lottery_backend.ticket.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linglong.lottery_backend.activity.service.TblUserCouponService;
import com.linglong.lottery_backend.common.service.JpaBatch;
import com.linglong.lottery_backend.listener.LotteryTags;
import com.linglong.lottery_backend.listener.MqTopicConstant;
import com.linglong.lottery_backend.lottery.digital.entity.TblDigitalResults;
import com.linglong.lottery_backend.lottery.digital.repository.TblDigitalResultsRepository;
import com.linglong.lottery_backend.order.entity.Order;
import com.linglong.lottery_backend.order.entity.OrderDetails;
import com.linglong.lottery_backend.order.repository.OrderDetailsInfoRepository;
import com.linglong.lottery_backend.order.repository.OrderDetailsRepository;
import com.linglong.lottery_backend.order.service.IOrderService;
import com.linglong.lottery_backend.order.service.bean.Play;
import com.linglong.lottery_backend.order.service.bean.PlayDetails;
import com.linglong.lottery_backend.prize.entity.TblBettingTicketDetails;
import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.cache.PlatformCache;
import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import com.linglong.lottery_backend.ticket.entity.Platform;
import com.linglong.lottery_backend.ticket.entity.TicketStatus;
import com.linglong.lottery_backend.ticket.entity.bo.TicketNumber;
import com.linglong.lottery_backend.ticket.enums.AoliEnum;
import com.linglong.lottery_backend.ticket.enums.PlatformEnum;
import com.linglong.lottery_backend.ticket.platform.PlatformFactory;
import com.linglong.lottery_backend.ticket.platform.PlatformInterface;
import com.linglong.lottery_backend.ticket.remote.aoli.AOLIService;
import com.linglong.lottery_backend.ticket.remote.bjgc.call.BJGCService;
import com.linglong.lottery_backend.ticket.remote.bjgc.entity.Header;
import com.linglong.lottery_backend.ticket.remote.bjgc.entity.Message;
import com.linglong.lottery_backend.ticket.remote.bjgc.entity.part.Response;
import com.linglong.lottery_backend.ticket.remote.bjgc.entity.part.Ticket;
import com.linglong.lottery_backend.ticket.remote.bjgc.entity.part.TicketBody;
import com.linglong.lottery_backend.ticket.repository.TblBettingTicketDigitalRepository;
import com.linglong.lottery_backend.ticket.service.TblBettingTicketService;
import com.linglong.lottery_backend.ticket.util.OrderTicketStatusUtil;
import com.linglong.lottery_backend.ticket.util.SignUtil;
import com.linglong.lottery_backend.user.account.service.TblUserBalanceService;
import com.linglong.lottery_backend.utils.DateUtil;
import com.linglong.lottery_backend.utils.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Component
@Slf4j
public class TicketAsyncExecutorTask {

    @Autowired
    JpaBatch jpaBatch;

    @Autowired
    TblBettingTicketService bettingTicketService;

//    @Autowired
//    TblBettingTicketRepository tblBettingTicketRepository;
//
//    @Autowired
//    OrderRepository orderRepository;

    @Autowired
    IOrderService orderService;

    @Resource
    RocketMQTemplate rocketMQTemplate;

    @Autowired
    BJGCService bjgcService;

    @Autowired
    AOLIService aoliService;

    @Autowired
    TblUserBalanceService userBalanceService;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TblDigitalResultsRepository tblDigitalResultsRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderDetailsInfoRepository orderDetailsInfoRepository;

    @Autowired
    TblUserCouponService userCouponService;

    @Autowired
    private TblBettingTicketDigitalRepository tblBettingTicketDigitalRepository;

    private final static BigDecimal amount = new BigDecimal(100);
    /**
     * 出票通知
     *
     * @param tickets
     */
    @Async("taskExecutor")
    public void doUpdateTicketStatus(List<Ticket> tickets) {
        List<Long> ids = tickets.stream().map(e -> Long.valueOf(e.getTicketId())).collect(Collectors.toList());
        List<BettingTicket> bettingTickets = bettingTicketService.findAllByTicketIds(ids);
        //更新出票状态
        this.updateTicketStatus(bettingTickets, tickets);
        //添加返奖详情
        sendToBettingTicketDetails(PlatformEnum.BJGONGCAI.getPlatformEn(), tickets);

        this.doUpdateOrderStatus(ids);
    }

    /**
     * 投注详情
     *
     * @param platformEn
     * @param tickets
     */
    public void sendToBettingTicketDetails(String platformEn, List<Ticket> tickets) {
        try {
            PlatformEnum platformEnum = PlatformEnum.getPlatformEnum(platformEn);
            if (null == platformEnum) {
                return;
            }
            List<TblBettingTicketDetails> tblBettingTicketDetails = platformEnum.sendTicket2Prize(tickets, bettingTicketService, orderDetailsRepository, idWorker);
            if (tblBettingTicketDetails.isEmpty()) {
                return;
            }
            this.sendToBettingTicketDetails(tblBettingTicketDetails);

            //更新订单赔率
            this.updateOrderOdds(tblBettingTicketDetails);

        } catch (Exception e) {
            log.error("send2Prize: {}", e);
        }
    }

    /**
     * 更新赔率
     *
     * @param tblBettingTicketDetails
     */
    private void updateOrderOdds(List<TblBettingTicketDetails> tblBettingTicketDetails) {
        Map<Long, List<TblBettingTicketDetails>> group = tblBettingTicketDetails.stream().collect(Collectors.groupingBy(TblBettingTicketDetails::getOrderId));
        for(Long k : group.keySet()){
            List<TblBettingTicketDetails> v = group.get(k);
            List<OrderDetails> orderDetailsList = orderDetailsRepository.findByOrderId(k);
            orderDetailsList.forEach(dt -> {
                Map<Long, List<TblBettingTicketDetails>> matchGroup = v.stream().collect(Collectors.groupingBy(TblBettingTicketDetails::getMatchId));

                List<TblBettingTicketDetails> ticketDetails = matchGroup.get(dt.getMatchId());
                if (null == ticketDetails) {
                    return;
                }
                Map<String, List<TblBettingTicketDetails>> playCodeGroup = ticketDetails.stream().collect(Collectors.groupingBy(TblBettingTicketDetails::getPlayCode));
                Map<String, JSONObject> playsMap = JSONObject.parseObject(dt.getPlaysDetails(), HashMap.class);
                for(String k1 : playsMap.keySet()){
                    PlayDetails v1 = JSONObject.parseObject(playsMap.get(k1).toString(), PlayDetails.class);
                    List<TblBettingTicketDetails> playCodeList = playCodeGroup.get(k1);
                    if (null == playCodeList) {
                        continue;
                    }
                    Map<String, List<TblBettingTicketDetails>> oddsCodeGroup = playCodeList.stream().collect(Collectors.groupingBy(TblBettingTicketDetails::getOddsCode));
                    List<Play> plays = v1.getItems();
                    plays_for:
                    for(int i1 = 0 ; i1 < plays.size() ; i1++){
                        Play e1 = plays.get(i1);
                        List<TblBettingTicketDetails> oddsList = oddsCodeGroup.get(e1.getItem());
                        if (null == oddsList) {
                            continue plays_for;
                        }
                        DoubleSummaryStatistics stats = oddsList.stream().mapToDouble(x -> x.getOdds().doubleValue()).summaryStatistics();
                        e1.setOdds(String.valueOf(stats.getMin()));
                    }
                    v1.setItems(plays);
                    playsMap.put(k1, JSON.parseObject(JSON.toJSONString(v1)));
                }
                dt.setPlaysDetails(JSON.toJSONString(playsMap));
                orderDetailsRepository.saveAndFlush(dt);
            });

        }

    }

    private void sendToBettingTicketDetails(List<TblBettingTicketDetails> tblBettingTicketDetails) {
        rocketMQTemplate.asyncSend(MqTopicConstant.lotteryTopic+":" + LotteryTags.betting,
                MessageBuilder.withPayload(JSON.toJSONString(tblBettingTicketDetails)).build(),
                new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        log.info("send to " + LotteryTags.betting + " success body:{}" + sendResult.toString());
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        log.info("send to " + LotteryTags.betting + " fail:", throwable);
                    }
                });
    }

    /**
     *
     * @param all
     */
    @Async("taskExecutor")
    public void doRepeatQueryTicketStatus( List<BettingTicket> all) {
        //分割 ticket list
        List<List<Ticket>> averageTicket = splitTicket(all, 5);
        List<Ticket> list = new ArrayList<>();
        for (List<Ticket> tickets : averageTicket) {
            //log.info("doRepeatQueryTicketStatus: {}",tickets);
            if (tickets.isEmpty()){ continue; }

            Map<Integer, List<Ticket>> groupTicket = tickets.stream().collect((Collectors.groupingBy(Ticket::getGameType)));
            groupTicket.forEach((gameType, ticketList) -> {
                Message message = this.assembleMessage(ticketList, 1003);
                //查询出票状态
                Response res = bjgcService.callTicketStatus(message, gameType);
                if (res.getTickets().isEmpty()) {
                    ticketList.forEach(e ->{
                        e.setStatus(Integer.valueOf(3));
                    });
                    list.addAll(ticketList);
                }
                list.addAll(res.getTickets());
//                if (res.getCode().equals("0000")) {
//                    if (res.getTickets().isEmpty()) {return;}
//                    list.addAll(res.getTickets());
//                }

            });

        }
        this.doUpdateTicketStatus(list);
    }

    /**
     *
     * @param all
     */
    @Async("taskExecutorAOLI")
    public void doRepeatQueryTicketStatusByAOLI( List<BettingTicket> all) {
        if (all.isEmpty())
            return;

        Map<Integer, StringBuffer> AOLIreqList = new HashMap<>();
        for (int i = 0; i < all.size(); i++) {
            if(i>=50)
                break;
            BettingTicket bettingTicket = all.get(i);
            StringBuffer ticketsId = AOLIreqList.get(bettingTicket.getGameType());
            if(ticketsId == null) {
                ticketsId = new StringBuffer();
            }
            ticketsId.append(bettingTicket.getTicketId())
                    .append(",");
            AOLIreqList.put(bettingTicket.getGameType(),ticketsId);
        }
        List<com.linglong.lottery_backend.ticket.remote.aoli.Message> resultList = aoliService.callTicketStatus(AOLIreqList);

        doUpdateOrderStatusByAOLI(resultList);
    }

    @Async("taskExecutor")
    @Transactional
    public void doUpdateOrderStatusByAOLI(List<com.linglong.lottery_backend.ticket.remote.aoli.Message> tickets) {
        List<Long> ids = tickets.stream().map(e -> Long.valueOf(e.getTicketId())).collect(Collectors.toList());
        List<BettingTicket> bettingTickets = bettingTicketService.findAllByTicketIds(ids);
        //更新出票状态
        this.updateTicketStatusByAOLI(bettingTickets, tickets);
        //添加返奖详情
        sendToBettingTicketDetailsByAOLI(PlatformEnum.AOLI.getPlatformEn(), tickets);

        this.doUpdateOrderStatus(ids);
    }


    public void sendToBettingTicketDetailsByAOLI(String platformEn,
                                                 List<com.linglong.lottery_backend.ticket.remote.aoli.Message> tickets) {
        try {
            PlatformEnum platformEnum = PlatformEnum.getPlatformEnum(platformEn);
            if (null == platformEnum) {
                return;
            }
            List<TblBettingTicketDetails> tblBettingTicketDetails = platformEnum.sendTicket2PrizeByAOLI(tickets, bettingTicketService, tblBettingTicketDigitalRepository, orderDetailsRepository, idWorker);
            if (tblBettingTicketDetails.isEmpty()) {
                return;
            }
            this.sendToBettingTicketDetails(tblBettingTicketDetails);

            //更新订单赔率
            this.updateOrderOdds(tblBettingTicketDetails);

        } catch (Exception e) {
            log.error("sendToBettingTicketDetailsByAOLI: {}", e);
        }
    }



    protected void updateTicketStatusByAOLI(List<BettingTicket> bettingTickets,
                                            List<com.linglong.lottery_backend.ticket.remote.aoli.Message> tickets) {
        if (!tickets.isEmpty()) {
            Map<String,
                    com.linglong.lottery_backend.ticket.remote.aoli.Message>
                    ticketMap = tickets.stream().collect(Collectors.toMap(com.linglong.lottery_backend.ticket.remote.aoli.Message::getTicketId, Function.identity()));
            List<BettingTicket> update = new ArrayList<>();
            for (int i = 0; i < bettingTickets.size(); i++) {
                BettingTicket bettingTicket = bettingTickets.get(i);
                com.linglong.lottery_backend.ticket.remote.aoli.Message message = ticketMap.get(bettingTicket.getTicketId().toString());
                if (null == message)
                    continue;
                if(bettingTicket.getTicketStatus().equals(Integer.valueOf(-2)) ||  bettingTicket.getTicketStatus().equals(Integer.valueOf(2))) {
                    continue;
                }

                String resultStatus = message.getResult();
                Integer status;
                if (resultStatus.equals(AoliEnum.ING_ENTRUST.getStatus())) {
                    continue;

                }else if (resultStatus.equals(AoliEnum.SUC_ENTRUST.getStatus())) {
                    status = AoliEnum.SUC_ENTRUST.getCode();

                }else if (resultStatus.equals(AoliEnum.SUC_TICKET.getStatus())) {
                    if(message.getOdds() == null)
                        continue;
                    status = AoliEnum.SUC_TICKET.getCode();

                    Date issueTime = null;
                    try{
                        issueTime = DateUtils.parseDate(message.getSuccessTime(), new String[]{DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS_SSS});
                    }catch (Exception e) {
                        try {
                            issueTime = new Date(Long.valueOf(message.getSuccessTime()));
                        }catch (Exception e1) {
                            log.error("aoli set issueTime DateUtils.parseDate Error", e1);
                            issueTime = new Date();
                        }
                    }
                    bettingTicket.setIssueTime(issueTime);

                }else {
                    status = AoliEnum.TICKET_CANCLE.getCode();
                }

                if(bettingTicket.getTicketStatus().equals(status))
                    continue;
//                if(bettingTicket.getGameType().equals(3) || bettingTicket.getGameType().equals(4)) {
//                    bettingTicket.setTicketStatus(2);
//
//                }else {
//                    bettingTicket.setTicketStatus(status);
//                }
                bettingTicket.setTicketStatus(status);
                update.add(bettingTicket);
            }
            if (!update.isEmpty()) {
                jpaBatch.batchUpdate(update);
            }
        }
    }

    private List<List<Ticket>> splitTicket(List<BettingTicket> all, int i) {
        List<Ticket> tickets = all.stream().map(e -> {
            Ticket ticket = new Ticket();
            ticket.setTicketId(String.valueOf(e.getTicketId()));
            ticket.setGameType(e.getGameType());
            return ticket;
        }).collect(Collectors.toList());

        //分割list
        List<List<Ticket>> averageTicket = Stream.iterate(0, n -> n + 1).limit(tickets.size()).parallel().map(a -> {
            List<Ticket> sendList = tickets.stream().skip(a * i).limit(i).parallel().collect(Collectors.toList());
            return sendList;
        }).collect(Collectors.toList());
        return averageTicket;
    }

    @Async("taskExecutor")
    @Transactional
    public void doUpdateOrderStatus(List<Long> ids) {
        List<BettingTicket> bettingTickets = bettingTicketService.findAllByTicketIds(ids);
        Set<Long> set = new HashSet<>();
        bettingTickets.forEach(e -> set.add(e.getOrderId()));
        List<BettingTicket> all = bettingTicketService.findAllByOrderIds(new ArrayList<>(set));

        Map<Long, List<BettingTicket>> ticketMap = all.stream().collect(Collectors.groupingBy(BettingTicket::getOrderId));
        ticketMap.forEach((k, v) -> {
                Order order = orderService.findByOrderId(k);
            try {
                if (null == order.getRefundFee()){
                    order.setRefundFee(BigDecimal.ZERO);
                }
                Integer status = OrderTicketStatusUtil.judgeOrderTicketStatus(v);
                if (status.equals(TicketStatus.wait)){
                    return;
                }
                if (status.equals(TicketStatus.wait)){
                    return;
                }
                List<BettingTicket> refundTicket = v.stream().filter(e -> e.getTicketStatus().equals(TicketStatus.fail.getValue())
                        || e.getTicketStatus().equals(TicketStatus.mfail.getValue())).collect(Collectors.toList());
                BigDecimal refundAmount = new BigDecimal(0.00);
                for (int i = 0; i < refundTicket.size(); i++) {
                    refundAmount = refundAmount.add(refundTicket.get(i).getTicketAmount());
                }
                refundAmount = refundAmount.multiply(new BigDecimal(100));
                int r = refundAmount.compareTo(order.getOrderFee());
                if (status.equals(TicketStatus.processing.getValue())) {
                    //处理中
                    orderService.updateAllBillStatus(k, status);
                } else if (status.equals(TicketStatus.successPart.getValue())
                        || status.equals(TicketStatus.fail.getValue())
                        || status.equals(TicketStatus.mfail.getValue())) {
                    if (r == 1){
                        order.setOrderStatus(-1);
                        orderService.updateOrder(order);
                        return;
                    }
                    BigDecimal couponAmount = order.getCouponAmount();

                    if (null == couponAmount){
                        couponAmount = BigDecimal.ZERO;
                    }
                    BigDecimal refundCouponAmount = BigDecimal.ZERO;
                    if (couponAmount.compareTo(BigDecimal.ZERO) == 1){
                        refundCouponAmount = couponAmount.compareTo(refundAmount) == 1?couponAmount.subtract(refundAmount):couponAmount;
                    }
                    if (refundCouponAmount.compareTo(BigDecimal.ZERO) == 1){
                        //退补偿红包
                        userCouponService.compensateCoupon(order.getUserCouponId(),refundCouponAmount,order.getOrderId());
                    }
                    //修改为按订单退款
                    BigDecimal rAmount = (refundAmount.subtract(refundCouponAmount)).divide(amount);
                    if (rAmount.compareTo(BigDecimal.ZERO) == 1){
                        userBalanceService.refund(order.getUserId(), rAmount, String.valueOf(order.getOrderId()),String.valueOf(order.getOrderId()));
                    }
                    if (status.equals(TicketStatus.successPart.getValue())){
                        //部分出票
                        orderService.updatePartBillStatus(k, refundAmount);
                    }else{
                        orderService.updatePayRefundedStatus(k, refundAmount, status);
                    }
                } else if (status.equals(TicketStatus.success.getValue())) {
                    //出票成功
                    orderService.updateAllBillStatus(k, status);
                }
            }catch (Exception e){
                log.error("doUpdateOrderStatus",e);
                log.error("order error info {}",order);
            }
        });
    }

    /**
     * 更新出票状态
     *
     * @param tickets
     */
    @Transactional
    protected void updateTicketStatus(List<BettingTicket> bettingTickets, List<Ticket> tickets) {
        try {
            if (!tickets.isEmpty()) {
                Map<String, Ticket> ticketMap = tickets.stream().collect(Collectors.toMap(Ticket::getTicketId, Function.identity()));
                List<BettingTicket> update = new ArrayList<>();
                bettingTickets.forEach(e -> {
                    Ticket ticket = ticketMap.get(String.valueOf(e.getTicketId()));
                    if (null == ticket
                            || e.getTicketStatus().equals(ticket.getStatus())) {
                        return;
                    }

                    e.setTicketStatus(ticket.getStatus().equals(3) ? TicketStatus.fail.getValue() : ticket.getStatus());
                    if (e.getTicketStatus().equals(TicketStatus.success.getValue())){
                        if(e.getGameType().equals(Integer.valueOf(1)) || e.getGameType().equals(Integer.valueOf(2))) {
                            if(StringUtils.isEmpty(ticket.getSp()))
                                return;

                        }

                        if (StringUtils.isNotBlank(ticket.getPtime())){
                            try {
                                e.setIssueTime(DateUtils.parseDate(ticket.getPtime(), new String[]{DateUtil.DATE_FORMAT_YY_MM_DD_HH_MM_SS}));
                            }catch (Exception e1){
                                e.setIssueTime(new Date());
                            }
                        }
                    }
                    update.add(e);
                });
                if (!update.isEmpty()) {
                    jpaBatch.batchUpdate(update);
                }
            }
        }catch (Exception e){
            log.error("updateTicketStatus {}",e);
        }
    }

    private Message assembleMessage(List<Ticket> tickets, int cmd) {
        Platform platform = PlatformEnum.BJGONGCAI.getPlatform();
        Message message = new Message();
        Header header = new Header();
        header.setCmd(cmd);
        header.setSid(platform.getPlatformAccount());
        header.setTimeTag(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
        TicketBody body = new TicketBody();
        body.setTickets(tickets);
        String digest = SignUtil.bjgcSign(header.getTimeTag(), body);
        header.setDigest(digest);
        message.setBody(body);
        message.setHeader(header);
        message.setVersion("1.0");
        return message;
    }

    /**
     * 陕115，更新奖期和返奖
     * @param lotteryType
     */
    public void shx115SetLottery(Integer lotteryType) {
        TblDigitalResults tblDigitalResults = tblDigitalResultsRepository.findFirstBylotteryTypeOrderByIdDesc(lotteryType);

        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        int year;
        int month;
        int day;

        int period = 0;
        if(tblDigitalResults == null) {
            calendar.setTime(now);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH) + 1;
            day = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 30);

        }else {

            Date endTime = DateUtil.formatToDate(tblDigitalResults.getLotteryTime(), "yyyy-MM-dd HH:mm:ss");
            long endTimeUnix = endTime.getTime();
//            long startTimeUnix = endTime.getTime() + (2 * 60) * 1000;
            long nowUnix = now.getTime();

            //到截止时间，发送延迟消息
            if(endTimeUnix - nowUnix <= 0){
                DefaultMQProducer defaultMQProducer = rocketMQTemplate.getProducer();
                org.apache.rocketmq.common.message.Message rocketMsg = RocketMQUtil.convertToRocketMessage(rocketMQTemplate.getObjectMapper(),
                        rocketMQTemplate.getCharset(), MqTopicConstant.lotteryTopic+":" + LotteryTags.bettingDigitalshx, MessageBuilder.withPayload(JSON.toJSONString(tblDigitalResults)).build());
                rocketMsg.setDelayTimeLevel(5);
                try{
                    defaultMQProducer.send(rocketMsg, new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            log.info("lottery:" + LotteryTags.bettingDigitalshx+" onSuccess");
                        }

                        @Override
                        public void onException(Throwable throwable) {
                            log.info("lottery:" + LotteryTags.bettingDigitalshx+" onException");
                        }
                    }, 5000L);
                }catch (Exception e) {
                    log.error("shx115SetLottery defaultMQProducer.send -> "+"lottery:" + LotteryTags.bettingDigitalshx,e);
                    return ;
                }
            }

            if(endTimeUnix - nowUnix > 0)
                return;

            String strPeriod = tblDigitalResults.getPeriod().toString();
            year = Integer.valueOf(strPeriod.substring(0,2)).intValue();
            month = Integer.valueOf(strPeriod.substring(2,4)).intValue();
            day = Integer.valueOf(strPeriod.substring(4,6)).intValue();
            period = Integer.valueOf(strPeriod.substring(6)).intValue();

            calendar.setTime(endTime);
        }

        while (calendar.getTime().before(now)) {
            calendar.add(Calendar.MINUTE, 20);
            period++;
            if(period > 44) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH) + 1;
                day = calendar.get(Calendar.DAY_OF_MONTH);
                calendar.set(Calendar.HOUR_OF_DAY, 8);
                calendar.set(Calendar.MINUTE, 46);
                period = 1;
            }
        }

        if(tblDigitalResults == null)
            calendar.add(Calendar.MINUTE, -4);

        String yearCode = (year+"").length() == 4 ? (year+"").substring(2) : (year+"");
        String monthCode = month < 10 ? "0"+month : month+"";
        String dayCode = day < 10 ? "0"+day : day+ "";
        String periodCode = period < 10 ? "0"+period : period+"";

        TblDigitalResults digitalResults = new TblDigitalResults();
        digitalResults.setSource("1");
        digitalResults.setPeriod(Integer.valueOf(yearCode+monthCode+dayCode+periodCode));
        digitalResults.setLotteryType(4);
        digitalResults.setLotteryTime(DateFormatUtils.format(calendar, "yyyy-MM-dd HH:mm:00"));
        digitalResults.setLotterySn(DateUtil.initWeekMap().get(calendar.get(Calendar.DAY_OF_WEEK)));
        digitalResults.setSalesAmount(BigDecimal.ZERO);
        digitalResults.setPrizePoolAmount(BigDecimal.ZERO);
        digitalResults.setCreatedTime(now);
        digitalResults.setUpdatedTime(now);
        digitalResults.setVersion(0);
        tblDigitalResultsRepository.save(digitalResults);
    }

    /**
     * 等待推送出票商
     */
    public void waitPushTicketList() {
        List<BettingTicket> ticketList = bettingTicketService.findByTicketStatus(Integer.valueOf(-3));

        //只做SHX115等待推送出票商
        ticketList = ticketList.stream().filter(ticket -> ticket.getGameType().equals(Integer.valueOf(AoliEnum.SHX115.getCode()))).collect(Collectors.toList());

        Map<Integer, List<BettingTicket>> group = ticketList.stream().collect(Collectors.groupingBy(BettingTicket::getPeriod));

        if(group.isEmpty())
            return ;

        long now = new Date().getTime();

        group.forEach((period, tickets) -> {
            List<TblDigitalResults> tblDigitalResults =
                    tblDigitalResultsRepository.findByPeriodAndLotteryType(period, AoliEnum.SHX115.getCode());

            if(tblDigitalResults.size() > 1)
                return ;

            TblDigitalResults tblDigitalResult = tblDigitalResults.get(0);

            Date lotteryTime;
            try {
                lotteryTime = DateUtils.parseDate(tblDigitalResult.getLotteryTime(), new String[]{DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS});
            }catch (Exception e) {
                log.error("tblDigitalResult.getLotteryTime() String by Date lotteryTime Error",e);
                return ;
            }

            Calendar lotteryCal = DateUtils.toCalendar(lotteryTime);
            lotteryCal.add(Calendar.MINUTE, -14);

            if(now - lotteryCal.getTimeInMillis() > 0) {

                Map<Integer, List<TicketDto>> platformTicketsMap = new HashMap<>();
                for (BettingTicket ticket : tickets) {
                    if (!platformTicketsMap.containsKey(ticket.getPlatformId())) {
                        List<TicketDto> ticketDtoList = new ArrayList<>();
                        platformTicketsMap.put(ticket.getPlatformId(), ticketDtoList);
                    }
                    List<TicketNumber> nums = ticket.getTicketNumbers();
                    TicketDto ticketDto = new TicketDto(ticket.getTicketId(), ticket.getTicketAmount(), nums, ticket
                            .getPlayType(), ticket.getExtra(), ticket.getTimes(),ticket.getGameType(),ticket.getOrderId(), ticket.getCreateTime());
                    platformTicketsMap.get(ticket.getPlatformId()).add(ticketDto);
                }

                for (Map.Entry<Integer, List<TicketDto>> entry : platformTicketsMap.entrySet()) {
                    List<TicketDto> dto = entry.getValue();
                    PlatformInterface pi = PlatformFactory.getInstance().getPlatform(entry.getKey());
                    int betNum = PlatformCache.getPlatform(entry.getKey()).getBetNum();
                    int total = dto.size() / betNum + (dto.size() % betNum == 0 ? 0 : 1);
                    for (int i = 0; i < total; i++) {
                        int end = (i + 1) * betNum > dto.size() ? dto.size() : (i + 1) * betNum;
                        pi.send2Center(AoliEnum.SHX115.getCode(), dto.subList(i * betNum, end));
                    }

                }
            }

        });
    }

    public void waitPushSSQTikcetListTask() {
        List<BettingTicket> ticketList = bettingTicketService.findByTicketStatus(Integer.valueOf(-3));
        log.warn("waitPushSSQTikcetListTask等待推送："+JSON.toJSONString(ticketList));

        //SSQ等待推送出票商
        ticketList = ticketList.stream().filter(ticket -> ticket.getGameType().equals(Integer.valueOf(AoliEnum.SSQ.getCode()))).collect(Collectors.toList());
        log.warn("waitPushSSQTikcetListTask推送SSQ："+JSON.toJSONString(ticketList));

        if(ticketList.isEmpty())
            return ;
        Map<Integer, List<TicketDto>> platformTicketsMap = new HashMap<>();
        ticketList.forEach(ticket -> {
            if (!platformTicketsMap.containsKey(ticket.getPlatformId())) {
                List<TicketDto> ticketDtoList = new ArrayList<>();
                platformTicketsMap.put(ticket.getPlatformId(), ticketDtoList);
            }
            List<TicketNumber> nums = ticket.getTicketNumbers();
            TicketDto ticketDto = new TicketDto(ticket.getTicketId(), ticket.getTicketAmount(), nums, ticket
                    .getPlayType(), ticket.getExtra(), ticket.getTimes(),ticket.getGameType(),ticket.getOrderId(), ticket.getCreateTime());
            platformTicketsMap.get(ticket.getPlatformId()).add(ticketDto);
        });
        for (Map.Entry<Integer, List<TicketDto>> entry : platformTicketsMap.entrySet()) {
            List<TicketDto> dto = entry.getValue();
            PlatformInterface pi = PlatformFactory.getInstance().getPlatform(entry.getKey());
            int betNum = PlatformCache.getPlatform(entry.getKey()).getBetNum();
            int total = dto.size() / betNum + (dto.size() % betNum == 0 ? 0 : 1);
            for (int i = 0; i < total; i++) {
                int end = (i + 1) * betNum > dto.size() ? dto.size() : (i + 1) * betNum;
                pi.send2Center(AoliEnum.SSQ.getCode(), dto.subList(i * betNum, end));
            }

        }
        log.warn("waitPushSSQTikcetListTask推送完成："+JSON.toJSONString(platformTicketsMap));
    }

    public void waitPushDPPTikcetListTask() {
        List<BettingTicket> ticketList = bettingTicketService.findByTicketStatus(Integer.valueOf(-3));

        log.warn("waitPushDPPTikcetListTask等待推送："+JSON.toJSONString(ticketList));
        //DLT PL3等待推送出票商
        ticketList = ticketList.stream().filter(ticket ->
                ticket.getGameType().equals(Integer.valueOf(AoliEnum.DLT.getCode())) ||
                        ticket.getGameType().equals(Integer.valueOf(AoliEnum.PL3.getCode())) ||
                        ticket.getGameType().equals(Integer.valueOf(AoliEnum.PL5.getCode()))).collect(Collectors.toList());

        log.warn("waitPushDPPTikcetListTask推送DLT,PL3,PL5："+JSON.toJSONString(ticketList));

        if(ticketList.isEmpty())
            return ;

        Map<Integer, List<BettingTicket>> group = ticketList.stream().collect(Collectors.groupingBy(BettingTicket::getGameType));

        group.forEach((gameType, Tickets) -> {

            Map<Integer, List<TicketDto>> platformTicketsMap = new HashMap<>();
            Tickets.forEach(ticket -> {
                if (!platformTicketsMap.containsKey(ticket.getPlatformId())) {
                    List<TicketDto> ticketDtoList = new ArrayList<>();
                    platformTicketsMap.put(ticket.getPlatformId(), ticketDtoList);
                }
                List<TicketNumber> nums = ticket.getTicketNumbers();
                TicketDto ticketDto = new TicketDto(ticket.getTicketId(), ticket.getTicketAmount(), nums, ticket
                        .getPlayType(), ticket.getExtra(), ticket.getTimes(),ticket.getGameType(),ticket.getOrderId(), ticket.getCreateTime());
                platformTicketsMap.get(ticket.getPlatformId()).add(ticketDto);
            });
            for (Map.Entry<Integer, List<TicketDto>> entry : platformTicketsMap.entrySet()) {
                List<TicketDto> dto = entry.getValue();
                PlatformInterface pi = PlatformFactory.getInstance().getPlatform(entry.getKey());
                int betNum = PlatformCache.getPlatform(entry.getKey()).getBetNum();
                int total = dto.size() / betNum + (dto.size() % betNum == 0 ? 0 : 1);
                for (int i = 0; i < total; i++) {
                    int end = (i + 1) * betNum > dto.size() ? dto.size() : (i + 1) * betNum;
                    pi.send2Center(gameType, dto.subList(i * betNum, end));
                }

            }

        });

        log.warn("waitPushDPPTikcetListTask推送完成"+JSON.toJSONString(group));
    }
}
