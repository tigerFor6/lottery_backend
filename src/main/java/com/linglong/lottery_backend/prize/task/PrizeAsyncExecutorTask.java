package com.linglong.lottery_backend.prize.task;

import com.alibaba.fastjson.JSON;
import com.linglong.lottery_backend.listener.MqTopicConstant;
import com.linglong.lottery_backend.prize.bean.part.SsqBonusCalculation;
import com.linglong.lottery_backend.prize.service.impl.Pl5CalculationServiceImpl;
import com.linglong.lottery_backend.user.account.service.TblUserBalanceService;
import com.linglong.lottery_backend.common.bean.LockKey;
import com.linglong.lottery_backend.common.service.JpaBatch;
import com.linglong.lottery_backend.lottery.digital.entity.TblDigitalResults;
import com.linglong.lottery_backend.lottery.digital.service.TblDigitalResultsService;
import com.linglong.lottery_backend.listener.LotteryTags;
import com.linglong.lottery_backend.listener.bean.PrizeResult;
import com.linglong.lottery_backend.listener.bean.PrizeResultDetails;
import com.linglong.lottery_backend.order.service.IOrderService;
import com.linglong.lottery_backend.prize.entity.*;
import com.linglong.lottery_backend.prize.service.TblBettingTicketDetailsService;
import com.linglong.lottery_backend.prize.service.TblBettingTicketGroupService;
import com.linglong.lottery_backend.prize.service.TblMatchResultsService;
import com.linglong.lottery_backend.prize.service.impl.DltCalculationServiceImpl;
import com.linglong.lottery_backend.prize.service.impl.Pl3CalculationServiceImpl;
import com.linglong.lottery_backend.prize.service.impl.Shx115CalculationServiceImpl;
import com.linglong.lottery_backend.prize.util.PrizeStatusUtil;
import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import com.linglong.lottery_backend.ticket.enums.AoliEnum;
import com.linglong.lottery_backend.ticket.platform.al.AoliDoBetHandler;
import com.linglong.lottery_backend.ticket.service.TblBettingTicketDigitalService;
import com.linglong.lottery_backend.ticket.service.TblBettingTicketService;
import com.linglong.lottery_backend.utils.IdWorker;
import com.linglong.lottery_backend.utils.SmsUtil;
import com.linglong.redisson.configure.annotations.LockAction;
import com.linglong.redisson.configure.util.LockType;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PrizeAsyncExecutorTask {

    @Autowired
    TblBettingTicketService bettingTicketService;

    @Autowired
    TblBettingTicketDetailsService bettingTicketDetailsService;

    @Autowired
    TblBettingTicketGroupService bettingTicketGroupService;

    @Autowired
    TblUserBalanceService userBalanceService;
//
//    @Autowired
//    TblMatchService tblMatchService;

    @Autowired
    IOrderService orderService;

    @Autowired
    IdWorker idWorker;

    @Autowired
    JpaBatch jpaBatch;

    @Autowired
    TblMatchResultsService matchResultsService;

    @Autowired
    SmsUtil smsUtil;

    @Value("${sms.template.code}")
    private String templateCode;

    @Resource
    RocketMQTemplate rocketMQTemplate;

    @Autowired
    TblDigitalResultsService digitalResultsService;

    @Autowired
    Shx115CalculationServiceImpl shx115CalculationService;

    @Autowired
    DltCalculationServiceImpl dltCalculationService;

    @Autowired
    Pl3CalculationServiceImpl pl3CalculationService;

    @Autowired
    Pl5CalculationServiceImpl pl5CalculationService;

    @Autowired
    TblBettingTicketDigitalService bettingTicketDigitalService;

    @Autowired
    AoliDoBetHandler aoliDoBetHandler;

    public final static BigDecimal amount = new BigDecimal(2.0);
    public final static BigDecimal bigAmount = new BigDecimal(1000000);

    /**
     * 开奖计算
     * 更改ticketdetails
     *
     * @param ticketIds
     */
    @Async("taskExecutor")
    public void openPrize(List<Long> ticketIds) {
        try {

            //计算每注中奖状态
            this.doStatisticsTicketResult(ticketIds);
            //更新ticket的开奖和中奖状态
            this.updateBettingBatchTicket(ticketIds);
            //返奖
            //this.updateUserReturnBonus(ticketIds);
            //更改订单状态
            this.updateOrderStatus(ticketIds);

        } catch (Exception e) {
            log.error("doUpdateBatchBettingTicketDetails error: {}", e);
        }
    }

    @Transactional
    public void updateOrderStatus(List<Long> ticketIds) {
        //查询所有票
        List<BettingTicket> tickets = bettingTicketService.findAllByTicketIds(ticketIds);

        List<Long> orderIds = tickets.stream().map(e -> e.getOrderId()).collect(Collectors.toList());
        List<BettingTicket> all = bettingTicketService.findAllByOrderIds(orderIds);
        List<BettingTicket> fAll = all.stream().filter(e -> e.getTicketStatus().equals(2)).collect(Collectors.toList());
        Map<Long, List<BettingTicket>> group = fAll.stream().collect(Collectors.groupingBy(BettingTicket::getOrderId));
        group.forEach((k, v) -> {
            Set<Integer> openPrizeArray = new HashSet<>();
            v.forEach(f -> openPrizeArray.add(f.getOpenPrizeStatus()));
            boolean openedPart = openPrizeArray.contains(OpenPrizeStatus.openedPart.getValue());
            boolean opened = openPrizeArray.contains(OpenPrizeStatus.opened.getValue());
            boolean wait = openPrizeArray.contains(PrizeStatus.wait.getValue());
            Integer openPrizeStatus = null;
            if (wait && opened  ||  openedPart) {
                //判断是否部分开奖
                openPrizeStatus = OpenPrizeStatus.openedPart.getValue();
            } else {
                if (wait) {
                    return;
                }
                openPrizeStatus = OpenPrizeStatus.opened.getValue();
            }
            Set<Integer> prizeArray = new HashSet<>();
            v.forEach(f -> prizeArray.add(f.getPrizeStatus()));
            boolean awarded = prizeArray.contains(PrizeStatus.awarded.getValue());
            boolean losing = prizeArray.contains(PrizeStatus.wait.getValue());
            BigDecimal totalAmount = new BigDecimal(0.00);
            boolean bigAwarded = false;
            try {
                if (awarded) {
                    Set<Integer> bigPrizeArray = new HashSet<>();
                    v.forEach(f -> bigPrizeArray.add(f.getBigPrizeStatus() ? PrizeStatus.bigAwarded.getValue() : 0));
                    bigAwarded = bigPrizeArray.contains(PrizeStatus.bigAwarded.getValue());
                    for (int i = 0; i < v.size(); i++) {
                        if (v.get(i).getBigPrizeStatus()) {
                            continue;
                        }
                        totalAmount = totalAmount.add(v.get(i).getBonus());
                    }
                    orderService.updateOpenAllPrizeStatus(k, openPrizeStatus, (bigAwarded && totalAmount.compareTo(BigDecimal.ZERO) == 0) ? PrizeStatus.bigAwarded.getValue() : PrizeStatus.awarded.getValue(), totalAmount);
                    return;
                }
                if (losing) {
                    orderService.updateOpenAllPrizeStatus(k, openPrizeStatus, 0, totalAmount);
                    return;
                }
            }catch (Exception e) {
                log.error("updateOrderStatus error 1："+ e.getMessage());
                log.error("updateOrderStatus error 2："+ "orderService.updateOpenAllPrizeStatus("+k+", "+openPrizeStatus+", "+((bigAwarded && totalAmount.compareTo(BigDecimal.ZERO) == 0) ? PrizeStatus.bigAwarded.getValue() : PrizeStatus.awarded.getValue())+", "+totalAmount+");");
            }

        });
    }

    @Transactional
    protected List<Long> updateBatchBettingTicketDetails(PrizeResult result) {
        log.info("doUpdateBatchBettingTicketDetails: {}", JSON.toJSONString(result));
        List<TblBettingTicketDetails> details = bettingTicketDetailsService.findByMatchId(result.getMatchId());
        details = details.stream().filter(e -> e.getResult().equals(PrizeStatus.wait.getValue())).collect(Collectors.toList());

        Map<String, PrizeResultDetails> prdMap = result.getResult().stream().collect(Collectors.toMap(PrizeResultDetails::getCode, Function.identity()));
        List<TblBettingTicketDetails> ticketDetails = new LinkedList<>();
        Set<Long> ticketIdSet = new HashSet<>();
        details.forEach(e -> {
            PrizeResultDetails resultDetails = prdMap.get(e.getPlayCode());
            if (null != resultDetails) {
                e.setResult(e.getOddsCode().equals(resultDetails.getResult()) ? PrizeStatus.awarded.getValue() : PrizeStatus.losing.getValue());
                ticketDetails.add(e);
                ticketIdSet.add(e.getBettingTicketId());
            }
        });
        if (!ticketDetails.isEmpty()) {
            jpaBatch.batchUpdate(ticketDetails);
        }
        return new ArrayList<>(ticketIdSet);
    }

    /**
     * 计算中奖结果
     *
     * @param ticketIds
     */
    @Transactional
    public void doStatisticsTicketResult(List<Long> ticketIds) {
        //查询详情
        List<TblBettingTicketDetails> ticketDetails = bettingTicketDetailsService.findByTicketIds(ticketIds);
        //查询组合
        List<TblBettingTicketGroup> ticketGroups = bettingTicketGroupService.findByTicketIds(ticketIds);
        Map<Long, List<TblBettingTicketDetails>> detailMap = ticketDetails.stream().collect(Collectors.groupingBy(TblBettingTicketDetails::getBettingTicketId));
        Map<Long, List<TblBettingTicketGroup>> groupMap = ticketGroups.stream().collect(Collectors.groupingBy(TblBettingTicketGroup::getBettingTicketId));
        List<TblBettingTicketGroup> groupsAll = new LinkedList<>();
        for (int i = 0; i < ticketIds.size(); i++) {
            List<TblBettingTicketDetails> details = detailMap.get(ticketIds.get(i));
            Map<Long, TblBettingTicketDetails> detailsMap = details.stream().collect(Collectors.toMap(TblBettingTicketDetails::getTicketDetailsId, Function.identity()));
            //计算中奖注数
            List<TblBettingTicketGroup> groups = groupMap.get(ticketIds.get(i));
            groups.forEach(g -> {
                String detail = g.getDetail();
                String[] detailIds = detail.split(",");
                List<TblBettingTicketDetails> group = new LinkedList<>();
                for (int j = 0; j < detailIds.length; j++) {
                    group.add(detailsMap.get(Long.valueOf(detailIds[j])));
                }
                Integer state = PrizeStatusUtil.judgeTicketGroupStatus(group);
                if (state.equals(PrizeStatus.wait.getValue())) {
                    return;
                }
                if (state.equals(PrizeStatus.awarded.getValue())) {
                    //该注已中奖
                    BigDecimal deamount = calculationTicket(group);

                    int s = deamount.compareTo(bigAmount);
                    deamount = deamount.multiply(BigDecimal.valueOf(g.getMultiple()));
                    //税前奖金
                    g.setPreTaxBonus(deamount);
                    if (s == 1 || s == 0) {
                        //大奖状态
                        BigDecimal aft = deamount.multiply(new BigDecimal(0.8));
                        g.setAftTaxBonus(aft);
                        g.setStatus(PrizeStatus.bigAwarded.getValue());
                        groupsAll.add(g);
                    } else {
                        g.setAftTaxBonus(deamount);
                        g.setStatus(PrizeStatus.awarded.getValue());
                        groupsAll.add(g);
                    }
                    return;
                } else if (state.equals(PrizeStatus.losing.getValue())) {
                    g.setStatus(PrizeStatus.losing.getValue());
                    groupsAll.add(g);
                    return;
                }
            });
        }
        if (!groupsAll.isEmpty()) {
            jpaBatch.batchUpdate(groupsAll);
        }
    }

    /**
     * 更新票的状态
     *
     * @param ticketIds
     */
    @Transactional
    public void updateBettingBatchTicket(List<Long> ticketIds) {

        for (int i = 0; i < ticketIds.size(); i++) {
            this.updateBettingTicket(ticketIds.get(i));
        }
    }

    @LockAction(key = LockKey.lockBettingTicket, value = "#ticketId", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    private void updateBettingTicket(Long ticketId) {

        BettingTicket bettingTicket = bettingTicketService.findByTicketId(ticketId);
        log.info("updateBettingTicket: {}",JSON.toJSONString(bettingTicket));
        List<TblBettingTicketGroup> ticketGroups = bettingTicketGroupService.findByTicketId(bettingTicket.getTicketId());
        Set<Integer> array = new HashSet<>();
        ticketGroups.forEach(f -> array.add(f.getStatus()));

        boolean bigAwarded = array.contains(PrizeStatus.bigAwarded.getValue());
        boolean awarded = array.contains(PrizeStatus.awarded.getValue());
        boolean losing = array.contains(PrizeStatus.losing.getValue());
        boolean wait = array.contains(PrizeStatus.wait.getValue());
        BigDecimal amount = new BigDecimal(0.00);
        if (wait && (bigAwarded || awarded || losing)) {
            //部分开奖
            bettingTicket.setOpenPrizeStatus(OpenPrizeStatus.openedPart.getValue());
        } else {
            if (wait) {
                return;
            }
            bettingTicket.setOpenPrizeStatus(OpenPrizeStatus.opened.getValue());
        }
        if (bigAwarded || awarded) {
            bettingTicket.setPrizeStatus(PrizeStatus.awarded.getValue());
            //设置大奖标识
            bettingTicket.setBigPrizeStatus(bigAwarded ? true : false);
            for (int i = 0; i < ticketGroups.size(); i++) {
                amount = amount.add(ticketGroups.get(i).getAftTaxBonus());
            }
            bettingTicket.setBonus(amount);
        }

        if (bettingTicket.getPrizeStatus().equals(PrizeStatus.awarded.getValue())) {
            if (bettingTicket.getBigPrizeStatus()){
                bettingTicket.setReturnPrizeStatus(2);
            }else{
                userBalanceService.reward(String.valueOf(bettingTicket.getUserId()), bettingTicket.getTicketId(), bettingTicket.getBonus());
                bettingTicket.setReturnPrizeStatus(1);
            }

        }
        bettingTicket.setBonusTime(new Date());
        bettingTicketService.updateBettingTicket(bettingTicket);
    }

    /**
     * 排列算法
     */
    private List<List<BigInteger>> arrange(List<BigInteger> elements, int num) {
        List<List<BigInteger>> result = new ArrayList<>();
        elements.forEach(data -> {
            List<BigInteger> list = new ArrayList<>();
            list.add(data);
            result.add(list);
        });
        return combiner(elements, num, result);
    }

    private List<List<BigInteger>> combiner(List<BigInteger> elements, int num, List<List<BigInteger>> result) {
        if (num == 1) {
            return result;
        }
        int len = result.size();
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < elements.size(); j++) {
                if (!result.get(i).contains(elements.get(j))) {
                    List<BigInteger> list = new ArrayList<>();
                    for (int k = 0; k < result.get(i).size(); k++) {
                        list.add(result.get(i).get(k));
                    }
                    list.add(elements.get(j));
                    Collections.sort(list);
                    result.add(list);
                }
            }
        }
        for (int i = 0; i < len; i++) {
            result.remove(0);
        }
        Iterator<List<BigInteger>> it = result.iterator();
        List<List<BigInteger>> listTemp = new ArrayList<>();
        while (it.hasNext()) {
            List<BigInteger> a = it.next();
            if (listTemp.contains(a)) {
                it.remove();
            } else {
                listTemp.add(a);
            }
        }
        combiner(elements, num - 1, result);
        return result;
    }

    /**
     * 笛尔卡积
     *
     * @param dimValue
     * @param result
     * @param layer
     * @param curList
     */
    private void recursive
    (List<List<TblBettingTicketDetails>> dimValue, List<List<TblBettingTicketDetails>> result,
     int layer, List<TblBettingTicketDetails> curList) {
        if (layer < dimValue.size() - 1) {
            if (dimValue.get(layer).size() == 0) {
                recursive(dimValue, result, layer + 1, curList);
            } else {
                for (int i = 0; i < dimValue.get(layer).size(); i++) {
                    List<TblBettingTicketDetails> list = new ArrayList<>(curList);
                    list.add(dimValue.get(layer).get(i));
                    recursive(dimValue, result, layer + 1, list);
                }
            }
        } else if (layer == dimValue.size() - 1) {
            if (dimValue.get(layer).size() == 0) {
                result.add(curList);
            } else {
                for (int i = 0; i < dimValue.get(layer).size(); i++) {
                    List<TblBettingTicketDetails> list = new ArrayList<>(curList);
                    list.add(dimValue.get(layer).get(i));
                    result.add(list);
                }
            }
        }
    }

    private BigDecimal calculationTicket(List<TblBettingTicketDetails> details) {
        BigDecimal deamount = new BigDecimal(1.00);
        for (int j = 0; j < details.size(); j++) {
            BigDecimal odds = details.get(j).getOdds();
            deamount = deamount.multiply(odds);
        }
        deamount = deamount.multiply(amount).setScale(3,  BigDecimal.ROUND_DOWN).setScale(2,BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(100));
        return deamount;
    }

//    public static void main(String[] args) {
//        BigDecimal deamount = new BigDecimal(1.00);
//        //deamount =new BigDecimal((new BigDecimal("3.72").multiply(new BigDecimal("2.95"))).toString()).multiply(amount).setScale(3,BigDecimal.ROUND_DOWN).setScale(2, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal("25"));
//        deamount = deamount.multiply(new BigDecimal("3.72")).multiply(new BigDecimal("2.95")).multiply(amount).setScale(3,  BigDecimal.ROUND_DOWN).setScale(2,BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal("25"));
//        System.out.println(deamount);
//    }

    @Async("taskExecutor")
    @Transactional
    public void doSaveBettingTicketDetails(List<TblBettingTicketDetails> details) {
        //TODO:保存出票详情
        List<TblBettingTicketDetails> l = this.saveSaveBettingTicketDetails(details);
        log.info("doSaveBettingTicketDetails: {}", JSON.toJSONString(l));
        if (l.isEmpty()) {
            return;
        }
        //TODO:拆分组合
        this.doSaveBettingTicketGroup(l);
    }

//    public static void main(String[] args) {
//        BigDecimal deamount = new BigDecimal(1.00);
//        deamount = deamount.multiply(BigDecimal.valueOf(1.48)).multiply(BigDecimal.valueOf(1.11)).setScale(2, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(2.00)).multiply(BigDecimal.valueOf(100));
//        System.out.println(deamount);
//    }
    protected List<TblBettingTicketDetails> saveSaveBettingTicketDetails(List<TblBettingTicketDetails> details) {
        Map<Long, List<TblBettingTicketDetails>> group = details.stream().collect(Collectors.groupingBy(TblBettingTicketDetails::getBettingTicketId));
        List<TblBettingTicketDetails> insert = new ArrayList<>();
        group.forEach((k, v) -> {
            List<TblBettingTicketDetails> rep = bettingTicketDetailsService.findByTicketId(k);
            if (rep.isEmpty()) {
                insert.addAll(v);
            }
        });
        if (!insert.isEmpty()) {
            jpaBatch.batchInsert(insert);
            return insert;
        }
        return new ArrayList<>();
    }

    protected void doSaveBettingTicketGroup(List<TblBettingTicketDetails> insert) {
        Map<Long, List<TblBettingTicketDetails>> groupMap = insert.stream().collect(Collectors.groupingBy(TblBettingTicketDetails::getBettingTicketId));
        List<TblBettingTicketGroup> groups = new LinkedList<>();
        groupMap.forEach((k, v) -> {
            BettingTicket ticket = bettingTicketService.findByTicketId(k);
            Map<String, List<TblBettingTicketDetails>> g = v.stream().collect(Collectors.groupingBy(TblBettingTicketDetails::getMatchIssue));
            Set<BigInteger> set = new HashSet<>();
            g.forEach((k1, v1) -> set.add(new BigInteger(k1)));
            String num = ticket.getExtra().split("_")[0];
            //通过排列算法算出有几种比赛组合方式
            List<List<BigInteger>> list = arrange(new ArrayList<>(set), Integer.valueOf(num));
            list.stream().forEach(i -> {
                List<List<TblBettingTicketDetails>> ll = new ArrayList<>();//比赛组合赔率
                i.forEach(key -> {
                    List<TblBettingTicketDetails> l = g.get(String.valueOf(key));
                    ll.add(l);
                });
                List<List<TblBettingTicketDetails>> recursiveResult = new ArrayList<>();
                //递归实现笛卡尔积  拆分组合  计算出投注项组合方式
                recursive(ll, recursiveResult, 0, new ArrayList<>());
                recursiveResult.forEach(e -> {
                            TblBettingTicketGroup ticketGroup = new TblBettingTicketGroup();
                            ticketGroup.setBettingTicketId(k);
                            Long[] str = new Long[e.size()];
                            for (int j = 0; j < e.size(); j++) {
                                str[j] = e.get(j).getTicketDetailsId();
                            }
                            ticketGroup.setTicketGroupId(idWorker.nextId());
                            ticketGroup.setMultiple(ticket.getTimes());
                            ticketGroup.setDetail(StringUtils.join(str, ","));
                            groups.add(ticketGroup);
                        }
                );
            });
        });
        log.info("拆注成功:{}",JSON.toJSONString(groups));
        jpaBatch.batchInsert(groups);
    }

    @Async("taskExecutor")
    @LockAction(key = LockKey.lockPrizeMatch, value = "#matchId", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public void doCalculateResult(Long matchId) {
        try {
            int count = matchResultsService.countByMatchId(matchId);
            if (count < 2) {
                return;
            }
            List<TblMatchResults> results = matchResultsService.findByMatchId(matchId);
            Map<Integer, List<TblMatchResults>> g = results.stream().collect(Collectors.groupingBy(TblMatchResults::getSourceType));
            if (g.size() < 2) {
                return;
            }
            Set<String> set = results.stream().map(e -> e.getMatchResult()).collect(Collectors.toSet());
            if (set.size() != 1) {
                log.info("doCalculateResult warning message:{}",matchId);
                return;
            }
            set.forEach(e -> {
                PrizeResult prizeResult = new PrizeResult();
                prizeResult.setMatchId(matchId);
                List<PrizeResultDetails> resultDetails = JSON.parseArray(e, PrizeResultDetails.class);
                prizeResult.setResult(resultDetails);
                List<Long> ticketIds = this.updateBatchBettingTicketDetails(prizeResult);
                if (ticketIds.isEmpty()){
                    return;
                }
                this.openPrize(ticketIds);
            });
        }catch (Exception e){
            log.error("doCalculateResult matchId {}",matchId);
            log.error("doCalculateResult exception {}",e);
        }

    }

    @Async("taskExecutor")
    @LockAction(key = LockKey.lockPrizeMatch, value = "#matchId", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public void doCalculateSpecialResult(Long matchId) {
        try{
            List<TblBettingTicketDetails> details = bettingTicketDetailsService.findByMatchId(matchId);
            if (details.isEmpty()){
                return;
            }
            Set<Long> ticketIdSet = new HashSet<>();
            details.forEach(e -> {
                e.setResult(PrizeStatus.awarded.getValue());
                e.setOdds(BigDecimal.ONE);
                ticketIdSet.add(e.getBettingTicketId());
            });
            jpaBatch.batchUpdate(details);
            List<Long> ticketIds = new ArrayList<>(ticketIdSet);
            this.openPrize(ticketIds);
        }catch (Exception e){
            log.error("doCalculateSpecialResult matchId {}",matchId);
            log.error("doCalculateSpecialResult exception {}",e);
        }

    }

    /**
     * 陕11选5返奖
     * @param tblDigitalResults
     */
    @Async("taskExecutor")
    @LockAction(key = LockKey.lockPrizeDigital, value = "#tblDigitalResults.period", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public void doCalculateShx115Result(TblDigitalResults tblDigitalResults) {

        try {
            JSONObject resultJSON = aoliDoBetHandler.reqLotteryResult(tblDigitalResults.getPeriod().toString());

            log.warn("doCalculateShx115Result resultJSON("+tblDigitalResults.getPeriod()+") ->"+resultJSON.toString());
            String resultCode = resultJSON.getString("resultCode");

            //奖期不存在
            if(resultCode.equals(AoliEnum.ISSUE_EXIT_ERROR.getStatus())) {
                return;

            }else if(resultCode.equals(AoliEnum.ISSUE_OPEN_ERROR.getStatus())){
                //还未开奖
                throw new Exception(tblDigitalResults.getPeriod().toString() + "->还未开奖");
            }

            JSONObject message = resultJSON.getJSONObject("message");
            String issueNumber = message.getString("issueNumber");

            if(!tblDigitalResults.getPeriod().toString().equals(issueNumber)) {
                log.error("reqLotteryResult message.issueNumber=：{}，tblDigitalResults.period=：{}",issueNumber, tblDigitalResults.getPeriod());
                return ;
            }

            String openResult = message.getString("openResult");
            digitalResultsService.updateLotteryResultById(openResult, tblDigitalResults.getId());

            List<BettingTicket> ticketList = bettingTicketService.findByPeriodAndGameTypeAndTicketStatus(tblDigitalResults.getPeriod(), tblDigitalResults.getLotteryType(), 2);
            if(ticketList.isEmpty()){
                log.warn(tblDigitalResults.getPeriod()+"(SHX115)没有投注");
                return;
            }

            ticketList = ticketList.stream().filter(e -> e.getPrizeStatus().equals(Integer.valueOf(0))).collect(Collectors.toList());

            shx115CalculationService.shx115Calculation(ticketList, openResult);
        }catch (Exception e) {
            log.error("陕11选5开奖："+e.getMessage());
            sendLagMQMessage(MessageBuilder.withPayload(JSON.toJSONString(tblDigitalResults)).build(), MqTopicConstant.lotteryTopic +":" + LotteryTags.bettingDigitalshx, 5);
            return ;
        }
    }

    /**
     * 大乐透返奖
     * @param ssqBonusCalculation
     */
    @Async("taskExecutor")
    @LockAction(key = LockKey.lockPrizeDigital, value = "#ssqBonusCalculation.period", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public void doCalculateDLTResult(SsqBonusCalculation ssqBonusCalculation) {

        try {

            List<TblDigitalResults> tblDigitalResultsList = digitalResultsService.findByPeriodAndLotteryType(Integer.valueOf(ssqBonusCalculation.getPeriod()), 5);
            if(tblDigitalResultsList.isEmpty()) {
                throw new Exception(ssqBonusCalculation.getPeriod()+"(DLT)奖期结果不存在");
            }

            if(tblDigitalResultsList.size() != 3) {
                throw new Exception(ssqBonusCalculation.getPeriod()+"(DLT)奖期结果不满足3条");
            }

            List<String> lotteryResultList = tblDigitalResultsList.stream().map(e -> e.getLotteryResult()).collect(Collectors.toList());
            Set<String> repeatLotteryResultSet = new HashSet<>();
            lotteryResultList.forEach(lotteryResult -> {
                repeatLotteryResultSet.add(lotteryResult);
            });

            if(repeatLotteryResultSet.size() != 1) {
//            smsUtil.sendWarningMessage("13691226986", templateCode);
//            smsUtil.sendWarningMessage("13106532014", templateCode);
//            smsUtil.sendWarningMessage("18538119860", templateCode);
                throw new Exception(ssqBonusCalculation.getPeriod()+"(DLT)奖期结果不一致");
            }


            TblDigitalResults tblDigitalResults = tblDigitalResultsList.get(0);
            String openResult = tblDigitalResults.getLotteryResult();
            if(StringUtils.isEmpty(openResult)) {
                throw new Exception(ssqBonusCalculation.getPeriod()+"(DLT)奖期结果不存在");
            }

            List<BettingTicket> ticketList = bettingTicketService.findByPeriodAndGameTypeAndTicketStatus(tblDigitalResults.getPeriod(), tblDigitalResults.getLotteryType(), 2);
            if(ticketList.isEmpty()){
                log.warn(ssqBonusCalculation.getPeriod()+"(DLT)没有投注");
                return;
            }

            ticketList = ticketList.stream().filter(e -> e.getPrizeStatus().equals(Integer.valueOf(0))).collect(Collectors.toList());

            dltCalculationService.dltCalculation(ticketList, tblDigitalResults.getLotteryResult());

        }catch (Exception e) {
            log.error("大乐透返奖："+e.getMessage());
            sendLagMQMessage(MessageBuilder.withPayload(JSON.toJSONString(ssqBonusCalculation)).build(), MqTopicConstant.lotteryTopic +":" + LotteryTags.bettingDigitaldlt, 10);
            return ;
        }

    }

    /**
     * 排列三返奖
     * @param ssqBonusCalculation
     */
    @Async("taskExecutor")
    @LockAction(key = LockKey.lockPrizeDigital, value = "#ssqBonusCalculation.period", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public void doCalculatePl3Result(SsqBonusCalculation ssqBonusCalculation) {
        try {
            List<TblDigitalResults> tblDigitalResultsList = digitalResultsService.findByPeriodAndLotteryType(Integer.valueOf(ssqBonusCalculation.getPeriod()), 6);
            if(tblDigitalResultsList.isEmpty()) {
                throw new Exception(ssqBonusCalculation.getPeriod()+"(PL3)奖期结果不存在");
            }

            if(tblDigitalResultsList.size() != 3) {
                throw new Exception(ssqBonusCalculation.getPeriod()+"(PL3)奖期结果不满足3条");
            }

            List<String> lotteryResultList = tblDigitalResultsList.stream().map(e -> e.getLotteryResult()).collect(Collectors.toList());
            Set<String> repeatLotteryResultSet = new HashSet<>();
            lotteryResultList.forEach(lotteryResult -> {
                repeatLotteryResultSet.add(lotteryResult);
            });

            if(repeatLotteryResultSet.size() != 1) {
//            smsUtil.sendWarningMessage("13691226986", templateCode);
//            smsUtil.sendWarningMessage("13106532014", templateCode);
//            smsUtil.sendWarningMessage("18538119860", templateCode);
                throw new Exception(ssqBonusCalculation.getPeriod()+"(PL3)奖期结果不一致");
            }


            TblDigitalResults tblDigitalResults = tblDigitalResultsList.get(0);
            String openResult = tblDigitalResults.getLotteryResult();
            if(StringUtils.isEmpty(openResult)) {
                throw new Exception(ssqBonusCalculation.getPeriod()+"(PL3)奖期结果不存在");
            }

            List<BettingTicket> ticketList = bettingTicketService.findByPeriodAndGameTypeAndTicketStatus(tblDigitalResults.getPeriod(), tblDigitalResults.getLotteryType(), 2);
            if(ticketList.isEmpty()){
                log.warn(ssqBonusCalculation.getPeriod()+"(PL3)没有投注");
                return;
            }

            ticketList = ticketList.stream().filter(e -> e.getPrizeStatus().equals(Integer.valueOf(0))).collect(Collectors.toList());

            pl3CalculationService.pl3Calculation(ticketList, tblDigitalResults.getLotteryResult());
        }catch (Exception e) {
            log.error("排列三返奖："+e.getMessage());
            sendLagMQMessage(MessageBuilder.withPayload(JSON.toJSONString(ssqBonusCalculation)).build(), MqTopicConstant.lotteryTopic +":"+ LotteryTags.bettingDigitalpl3, 10);
            return ;
        }
    }

    /**
     * 排列五返奖
     */
    @Async("taskExecutor")
    @LockAction(key = LockKey.lockPrizeDigital, value = "#ssqBonusCalculation.period", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public void doCalculatePl5Result(SsqBonusCalculation ssqBonusCalculation) {
        try {

            List<TblDigitalResults> tblDigitalResultsList = digitalResultsService.findByPeriodAndLotteryType(Integer.valueOf(ssqBonusCalculation.getPeriod()), 7);
            if(tblDigitalResultsList.isEmpty()) {
                throw new Exception(ssqBonusCalculation.getPeriod()+"(PL5)奖期结果不存在");
            }

            if(tblDigitalResultsList.size() != 3) {
                throw new Exception(ssqBonusCalculation.getPeriod()+"(PL5)奖期结果不满足3条");
            }

            List<String> lotteryResultList = tblDigitalResultsList.stream().map(e -> e.getLotteryResult()).collect(Collectors.toList());
            Set<String> repeatLotteryResultSet = new HashSet<>();
            lotteryResultList.forEach(lotteryResult -> {
                repeatLotteryResultSet.add(lotteryResult);
            });

            if(repeatLotteryResultSet.size() != 1) {
                throw new Exception(ssqBonusCalculation.getPeriod()+"(PL5)奖期结果不一致");
            }

            TblDigitalResults tblDigitalResults = tblDigitalResultsList.get(0);
            String openResult = tblDigitalResults.getLotteryResult();
            if(StringUtils.isEmpty(openResult)) {
                throw new Exception(ssqBonusCalculation.getPeriod()+"(PL5)奖期结果不存在");
            }

            List<BettingTicket> ticketList = bettingTicketService.findByPeriodAndGameTypeAndTicketStatus(tblDigitalResults.getPeriod(), tblDigitalResults.getLotteryType(), 2);
            if(ticketList.isEmpty()){
                log.warn(ssqBonusCalculation.getPeriod()+"(PL5)没有投注");
                return;
            }

            ticketList = ticketList.stream().filter(e -> e.getPrizeStatus().equals(Integer.valueOf(0))).collect(Collectors.toList());

            pl5CalculationService.pl5Calculation(ticketList, tblDigitalResults.getLotteryResult());

        }catch (Exception e) {
            log.error("排列五返奖："+e.getMessage());
            sendLagMQMessage(MessageBuilder.withPayload(JSON.toJSONString(ssqBonusCalculation)).build(), MqTopicConstant.lotteryTopic +":"+ LotteryTags.bettingDigitalpl3, 10);
            return ;
        }

    }


    public void sendLagMQMessage(Message build, String destination, int delayTimeLevel) {
        DefaultMQProducer defaultMQProducer = rocketMQTemplate.getProducer();
        org.apache.rocketmq.common.message.Message rocketMsg = RocketMQUtil.convertToRocketMessage(rocketMQTemplate.getObjectMapper(),
                rocketMQTemplate.getCharset(), destination, build);
        rocketMsg.setDelayTimeLevel(delayTimeLevel);
        try{
            defaultMQProducer.send(rocketMsg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info(MqTopicConstant.lotteryTopic +":" + destination+" onSuccess");
                }

                @Override
                public void onException(Throwable throwable) {
                    log.info(MqTopicConstant.lotteryTopic +":" + destination +" onException");
                }
            }, 5000L);
        }catch (Exception e) {
            log.error("destination defaultMQProducer.send -> "+MqTopicConstant.lotteryTopic +":" + destination,e);
            return ;
        }
    }

//    /**
//     * 数字彩开奖
//     * @param results
//     */
//    @Async("taskExecutor")
//    @LockAction(key = LockKey.lockPrizeDigital, value = "#results.period", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
//    public void doStatisticsBettingDigital(TblDigitalResults results) {
//        List<TblDigitalResults> r = digitalResultsService.findByPeriodAndLotteryType(results.getPeriod(),results.getLotteryType());
//        if (r.size() == 3){
//            Map<String, List<TblDigitalResults>> g = r.stream().collect(Collectors.groupingBy(TblDigitalResults::getSource));
//            if (g.size() == 3){
//                Set<String> lotteryResult = r.stream().map(e -> e.getLotteryResult()).collect(Collectors.toSet());
//                Set<String> lotteryDetail = r.stream().map(e -> e.getLotteryDetail()).collect(Collectors.toSet());
//                if (lotteryResult.isEmpty() || lotteryDetail.isEmpty()){
//                    return;
//                }
//                if (lotteryResult.size()>1 || lotteryDetail.size()>1){
//                    //出现异常结果
//                    //插入异常开奖结果信息
//                    return;
//                }
//                String de = lotteryDetail.toArray(new String[lotteryDetail.size()])[0];
//                String re = lotteryResult.toArray(new String[lotteryResult.size()])[0];
//                List<DigitalDetail> details = JSON.parseArray(de, DigitalDetail.class);
//                this.doStatisticsBettingDigital(re,details,results.getPeriod(),results.getLotteryType());
//            }
//        }
//
//    }
//
//    /**
//     * 计算双色球中奖结果
//     * @param re
//     * @param details
//     * @param period
//     * @param lotteryType
//     */
//    private void doStatisticsBettingDigital(String re, List<DigitalDetail> details, Integer period, Integer lotteryType) {
//        List<BettingTicket> bettingTickets = bettingTicketService.findByPeriodAndGameType(period,lotteryType);
//        if (bettingTickets.isEmpty()){
//            return;
//        }
//
//        String[] lotteryNum = re.split(CommonConstant.COMMON_COLON_STR);
//
//        String[] lotteryRedBall = lotteryNum[0].split(CommonConstant.COMMA_SPLIT_STR);
//        String lotteryBlueBall = lotteryNum[1].split(CommonConstant.COMMA_SPLIT_STR)[0];
//        //暂时先这样写，后期如何数据量大的话，用多线程或消息队列处理
//        bettingTickets.forEach(e->{
//            String lotteryNumber = e.getLotteryNumber();
//            String[] all = lotteryNumber.split(CommonConstant.COMMON_AT_STR)[0].split(CommonConstant.PERCENT_SPLIT_STR)[1].split(CommonConstant.COMMON_COLON_STR);
//
//            String[] redAllArr = all[0].split(CommonConstant.COMMON_AND_STR);
//            String[] danMa = {};
//            String[] tuoMa = null;
//            String[] blueBall = all[1].split(CommonConstant.COMMA_SPLIT_STR);
//            if (redAllArr.length == 2){
//                danMa = redAllArr[0].split(CommonConstant.COMMA_SPLIT_STR);
//                tuoMa = redAllArr[1].split(CommonConstant.COMMA_SPLIT_STR);
//            }else{
//                tuoMa = redAllArr[0].split(CommonConstant.COMMA_SPLIT_STR);
//            }
//            int [] awardInfo = analyseBidAwardLevels(danMa,tuoMa,blueBall,lotteryRedBall,lotteryBlueBall);
//            for (int i = 0; i < awardInfo.length; i++) {
//                if (awardInfo[i] > 0){
//                    int level = i+1;
//                }
//            }
//        });
//
//    }
//
//    private static int[][] levelInfo = {
//            {1, 6, 1},
//            {2, 6, 0},
//            {3, 5, 1},
//            {4, 5, 0},
//            {4, 4, 1},
//            {5, 4, 0},
//            {5, 3, 1},
//            {6, 2, 1},
//            {6, 1, 1},
//            {6, 0, 1}
//    };
//
//    /**
//     * @param bidCoreRedBall 胆码红球
//     * @param bidRedBall     拖码红球
//     * @param bidBlueBall    蓝球
//     * @param targetRedBall  中奖红球
//     * @param targetBlueBall 中奖蓝球
//     * @return
//     */
//    public int[] analyseBidAwardLevels(String[] bidCoreRedBall, String[] bidRedBall, String[] bidBlueBall,
//                                       String[] targetRedBall, String targetBlueBall) {
//        int[] result = new int[]{0, 0, 0, 0, 0, 0};
//        int[] bingoNum = bingoNum(bidCoreRedBall, bidRedBall, bidBlueBall, targetRedBall, targetBlueBall);
//        // 返回{拖码红球中奖数，蓝球中奖数，胆码红球中奖数}
//        for (int i = 0; i < levelInfo.length; i++) {
//            int cout = bingoRedMultiple(levelInfo[i][1], bingoNum[2], bingoNum[0], bidCoreRedBall.length,
//                    bidRedBall.length) * bingoBlueMultiple(levelInfo[i][2], bingoNum[1], bidBlueBall);
//            result[levelInfo[i][0] - 1] += cout;
//        }
//        return result;
//    }
//
//    /*
//     * 返回篮球中奖的倍数
//     */
//    private int bingoBlueMultiple(int requestBlue, int bingoBlue, String[] bidBlueBall) {
//        if (requestBlue == 1) // 要求篮球要中
//        {
//            return bingoBlue; // 篮球中返回1，否则返回0
//        } else {
//            return bidBlueBall.length - bingoBlue; // 所选篮球的个数减去中了的
//        }
//    }
//
//    /**
//     * 返回红球中奖的倍数
//     *
//     * @param requestRed           要中奖红球数
//     * @param bingoCore            胆码中奖数
//     * @param bingoRed             拖码中奖数
//     * @param bidCoreRedBallLength 胆码红球数
//     * @param bidCoreRedBallLength 拖码红球数
//     */
//    private int bingoRedMultiple(int requestRed, int bingoCore, int bingoRed, int bidCoreRedBallLength,
//                                 int bidRedBallLength) {
//        int maxBingoRed = Math.min(6 - bidCoreRedBallLength, bingoRed); // 最多中奖拖码个数
//        /*
//         * 如果胆码+拖码中奖数不足，则不可能 如果是胆拖，且胆码中奖数已经超过要求，则不可能
//         */
//        if (maxBingoRed + bingoCore < requestRed || (maxBingoRed >= 0 && bingoCore > requestRed)) {
//            return 0;
//        }
//        return combine(bingoRed, requestRed - bingoCore) * combine(bidRedBallLength -
//                bingoRed, 6 - bidCoreRedBallLength - requestRed + bingoCore);
//    }
//
//    /**
//     * 计算排列组合的值
//     *
//     * @param total
//     * @param select
//     * @return
//     */
//    public static int combine(int total, int select) {
//        if (select > total) {
//            return 0;
//        } else if (select == total) {
//            return 1;
//        } else if (total == 0) {
//            return 1;
//        } else {
//            if (select > total / 2)
//                select = total - select;
//
//            long result = 1;
//            for (int i = total; i > total - select; i--) {
//                result *= i;
//                if (result < 0)
//                    return -1;
//            }
//            for (int j = select; j > 0; j--) {
//                result /= j;
//            }
//            if (result > Integer.MAX_VALUE)
//                return -1;
//            return (int) result;
//        }
//    }
//
//    /**
//     * 返回红蓝球中了的个数
//     *
//     * @return
//     */
//    private int[] bingoNum(String[] bidCoreRedBall, String[] bidRedBall, String[] bidBlueBall, String[] targetRedBalls,
//                           String targetBlueBall) {
//        int[] numArray = {0, 0, 0};
//        int j = 0;
//        for (int i = 0; i < bidRedBall.length; i++) {
//            int compareResult = bidRedBall[i].compareTo(targetRedBalls[j]);
//            if (compareResult == 0) {
//                j++;
//                numArray[0]++;
//            } else if (compareResult > 0) {
//                j++;
//                i--;
//            }
//            if (j >= targetRedBalls.length) {
//                break;
//            }
//            continue;
//        }
//
//        j = 0;
//        for (int i = 0; i < bidCoreRedBall.length; i++) {
//            int compareResult = bidCoreRedBall[i].compareTo(targetRedBalls[j]);
//            if (compareResult == 0) {
//                j++;
//                numArray[2]++;
//            } else if (compareResult > 0) {
//                j++;
//                i--;
//            }
//            if (j >= targetRedBalls.length) {
//                break;
//            }
//            continue;
//        }
//
//        for (String blueBall : bidBlueBall) {
//            if (blueBall.equalsIgnoreCase(targetBlueBall)) {
//                numArray[1] = 1;
//            }
//        }
//        return numArray;
//    }
//
//    public static void main(String[] args) {
//        String lotteryNumber ="13,14,15,16,17&01,02,03,04,05,06,07,08,09,10,11,12:01@12";
//        String[] all = lotteryNumber.split("@")[0].split(":");
//        String redAll = all[0];
//        String[] l = {"01","13","14","15","16","17"};
//        String[] s = {"01"};
//        String[] redAllArr = redAll.split("&");
//
//        String blueAll = all[1];
//
//        PrizeAsyncExecutorTask task = new PrizeAsyncExecutorTask();
//
//        /**
//         * @param bidCoreRedBall 胆码红球
//         * @param bidRedBall     拖码红球
//         * @param bidBlueBall    蓝球
//         * @param targetRedBall  中奖红球
//         * @param targetBlueBall 中奖蓝球
//         * @return
//         */
//        int [] ss = task.analyseBidAwardLevels(redAllArr[0].split(","),redAllArr[1].split(","),blueAll.split(","),l,"01");
//        System.out.println(JSON.toJSONString(ss));
//
//        for (int i = 0; i < ss.length; i++) {
//            int addBetTimes = ss[i];
//            if (addBetTimes <= 0) {
//                continue;
//            }
//            for (AwardInfo lotteryAwardInfo : SSQ_AWARD_INFO_LIST) {
//                // 奖金信息
//                if (lotteryAwardInfo.getAwardLevel().equals(String.valueOf(i + 1))) {
//                    System.out.println(lotteryAwardInfo.getAwardLevel());
//                }
//                if (lotteryAwardInfo.getAwardLevel().equals(String.valueOf(i + 1 + "_z"))) {
//                    System.out.println("1");
//                }
//            }
//        }
//    }
}
