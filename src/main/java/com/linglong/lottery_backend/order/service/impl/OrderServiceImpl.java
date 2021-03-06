package com.linglong.lottery_backend.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.linglong.lottery_backend.activity.rules.CouponRules;
import com.linglong.lottery_backend.order.entity.OrderDetails;
import com.linglong.lottery_backend.order.entity.OrderDetailsInfo;
import com.linglong.lottery_backend.order.repository.OrderDetailsInfoRepository;
import com.linglong.lottery_backend.order.repository.OrderDetailsRepository;
import com.linglong.lottery_backend.order.service.bean.*;
import com.linglong.lottery_backend.user.account.service.TblUserBalanceService;
import com.linglong.lottery_backend.activity.entity.TblUserCoupon;
import com.linglong.lottery_backend.activity.service.TblUserCouponService;
import com.linglong.lottery_backend.common.bean.LockKey;
import com.linglong.lottery_backend.lottery.digital.DeadTimeCode;
import com.linglong.lottery_backend.lottery.digital.entity.TblDigitalResults;
import com.linglong.lottery_backend.lottery.digital.repository.TblDigitalResultsRepository;
import com.linglong.lottery_backend.lottery.match.common.Result;
import com.linglong.lottery_backend.lottery.match.entity.TblMatch;
import com.linglong.lottery_backend.lottery.match.entity.TblMatchOdds;
import com.linglong.lottery_backend.lottery.match.repository.TblMatchRepository;
import com.linglong.lottery_backend.lottery.match.service.TblMatchOddsService;
import com.linglong.lottery_backend.lottery.match.service.TblMatchService;
import com.linglong.lottery_backend.order.entity.Order;
import com.linglong.lottery_backend.order.listener.bean.HitPrizeResult;
import com.linglong.lottery_backend.order.model.StatusCode;
import com.linglong.lottery_backend.order.model.order_model.PlayType;
import com.linglong.lottery_backend.order.repository.OrderRepository;
import com.linglong.lottery_backend.order.service.IOrderService;
import com.linglong.lottery_backend.prize.entity.PrizeStatus;
import com.linglong.lottery_backend.prize.repository.TblBettingTicketDetailsRepository;
import com.linglong.lottery_backend.ticket.service.LotteryBetService;
import com.linglong.lottery_backend.ticket.service.SplitService;
import com.linglong.lottery_backend.ticket.thread.AfterPayTask;
import com.linglong.lottery_backend.ticket.thread.ThreadPool;
import com.linglong.lottery_backend.utils.*;
import com.linglong.redisson.configure.annotations.LockAction;
import com.linglong.redisson.configure.util.LockType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.linglong.lottery_backend.utils.BetNumUtil.*;
import static com.linglong.lottery_backend.utils.OrderStatusUtil.fetchOrderDetailStatus;
import static com.linglong.lottery_backend.utils.OrderStatusUtil.fetchOrderListStatus;

/**
 * @Author: qihua.li
 * @since: 2019-04-03
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class OrderServiceImpl implements IOrderService {

    // private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final IdWorker idWorker;
    private final OrderUtil orderUtil;
    private final TblMatchOddsService matchOddsService;
    private final DateFormatUtil dateFormatUtil;
    private final TblUserBalanceService userBalanceService;
    private final SplitService splitService;
    private final LotteryBetService lotteryBetService;
    private final TblMatchRepository matchRepository;

    @Autowired
    private TblMatchService tblMatchService;

    @Autowired
    private TblBettingTicketDetailsRepository tblBettingTicketDetailsRepository;

    @Autowired
    private TblDigitalResultsRepository tblDigitalResultsRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderDetailsInfoRepository orderDetailsInfoRepository;

    @Autowired
    TblUserCouponService tblUserCouponService;

    @Autowired
    private DateUtil dateUtil;

    @Override
    public void checkOrder(String userId, Map<String, String> orderMap) throws ParseException, TimeoutException {
        Integer lotteryType = Integer.parseInt(orderMap.get("game_type"));
        String programmes = orderMap.get("programmes");
        if (lotteryType == 7){
            //?????????
            if (null == orderMap.get("rankFivePeriods")){
                throw new TimeoutException("????????????????????????");
            }
            int rankFivePeriods = Integer.valueOf(orderMap.get("rankFivePeriods"));
            List<TblDigitalResults> tblDigitalResultsList =  tblDigitalResultsRepository.findByPeriodAndLotteryType(rankFivePeriods,lotteryType);
            if (null == tblDigitalResultsList || tblDigitalResultsList.isEmpty()){
                //??????????????????????????????????????????
            }else{
                SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
                String dateStr = form.format(tblDigitalResultsList.get(0).getLotteryTime()) + DeadTimeCode.RANKFIVE.getCode();
                Date line = DateUtil.formatToDate(dateStr,"yyyy-MM-dd HH:mm:ss");
                if(line.before(new Date())){
                    throw new TimeoutException("?????????????????????");
                }
            }
            List<RankFiveDetails> rankFiveDetailsList = getRankFiveDetails(programmes);
            for (RankFiveDetails rankFiveDetails : rankFiveDetailsList){
                int len = rankFiveDetails.getNumber().split(";").length;
                if (len < 5){
                    throw new IllegalArgumentException("??????????????????1??????");
                }
            }
        }else if (lotteryType == 6){
            //?????????
            if (null == orderMap.get("rankThreePeriods")){
                throw new TimeoutException("????????????????????????");
            }
            int rankThreePeriods = Integer.valueOf(orderMap.get("rankThreePeriods"));
            List<TblDigitalResults> tblDigitalResultsList =  tblDigitalResultsRepository.findByPeriodAndLotteryType(rankThreePeriods,lotteryType);
            if (null == tblDigitalResultsList || tblDigitalResultsList.isEmpty()){
                //??????????????????????????????????????????
            }else{
                throw new TimeoutException("????????????????????????");
            }
            List<RankThreeDetails> rankThreeDetailsList = getRankThreeDetails(programmes);
            for (RankThreeDetails rankThreeDetails : rankThreeDetailsList){
                String rankThreeType = rankThreeDetails.getRankThreeType();
                int len = rankThreeDetails.getNumber().split(",").length;
                if (rankThreeType.equals(PlayType.RANK62.getType())){
                    if (len < 2){
                        throw new IllegalArgumentException("????????????????????????2??????");
                    }else if (BetNumUtil.judgeStrArrayContainsSameStr(rankThreeDetails.getNumber().split(","))){
                        throw new IllegalArgumentException("??????????????????????????????");
                    }
                }else if (rankThreeType.equals(PlayType.RANK63.getType())){
                    if (len < 3){
                        throw new IllegalArgumentException("????????????????????????3??????");
                    }else if (BetNumUtil.judgeStrArrayContainsSameStr(rankThreeDetails.getNumber().split(","))){
                        throw new IllegalArgumentException("??????????????????????????????");
                    }
                }
            }
        } else if (lotteryType == 5){
            //?????????
            //????????????????????????
            if (null == orderMap.get("superLottoPeriods")){
                throw new TimeoutException("????????????????????????");
            }
            int superLottoPeriods = Integer.valueOf(orderMap.get("superLottoPeriods"));
            List<TblDigitalResults> tblDigitalResultsList =  tblDigitalResultsRepository.findByPeriodAndLotteryType(superLottoPeriods,lotteryType);
            if (null == tblDigitalResultsList || tblDigitalResultsList.isEmpty()){
                //??????????????????????????????????????????
            }else{
                throw new TimeoutException("????????????????????????");
            }
            //???????????????????????????????????????????????????????????????????????????????????????
            List<SuperLottoDetails> superLottoDetailsList = getSuperLottoDetails(programmes);
            for (SuperLottoDetails superLottoDetails : superLottoDetailsList) {
                String redStr = superLottoDetails.getRedNumber();
                String blueStr = superLottoDetails.getBlueNumber();
                String superLottoType = superLottoDetails.getSuperLottoType();
                if (PlayType.SUPERLOTTOTHREE.getType().equals(superLottoType)){
                    String redMain = redStr.split(";")[0];
                    String redSlave = redStr.split(";")[1];
                    int mainBlueLen;
                    int slaveBlueLen;
                    if (redMain.split(",").length > 4){
                        throw new IllegalArgumentException("?????????????????????????????????4???");
                    }else if (redMain.split(",").length < 1){
                        throw new IllegalArgumentException("?????????????????????????????????1???");
                    }else if (redSlave.split(",").length < 2){
                        throw new IllegalArgumentException("?????????????????????????????????2???");
                    }else if (blueStr.contains(";")){
                        mainBlueLen = blueStr.split(";")[0].split(",").length;
                        slaveBlueLen = blueStr.split(";")[1].split(",").length;
                        if (mainBlueLen > 1){
                            throw new IllegalArgumentException("?????????????????????????????????1???");
                        }else if (slaveBlueLen < 2){
                            throw new IllegalArgumentException("?????????????????????????????????2???");
                        }
                    }
                }else if (BetNumUtil.judgeStrArrayContainsSameStr(redStr.split(","))){
                    throw new IllegalArgumentException("??????????????????????????????");
                }
            }
        }else if (lotteryType == 4){
            //11???5
            List<ElevenChooseFiveDetails> elevenChooseFiveDetailsList = getElevenChooseFiveDetails(programmes);
            for (ElevenChooseFiveDetails elevenChooseFiveDetails : elevenChooseFiveDetailsList) {
                String numberStr = elevenChooseFiveDetails.getNumber();
                String elevenChoosefiveType = elevenChooseFiveDetails.getElevenChoosefiveType();
                String selectNum = elevenChoosefiveType.split("-")[2];
                if (elevenChoosefiveType.startsWith("4-2")){
                    Integer num = numberStr.split(";")[0].split(",").length;
                    if (1 > num || num >= Integer.valueOf(selectNum)){
                        throw new IllegalArgumentException("??????????????????????????????");
                    }else if (numJewelsInStones(numberStr.split(";")[0].split(","),numberStr.split(";")[1].split(",")) != 0){
                        throw new IllegalArgumentException("????????????????????????????????????");
                    }
                }
            }
        }else if (lotteryType == 3){
            //?????????
            //????????????????????????
            if (null == orderMap.get("twoColorBallPeriods")){
                throw new TimeoutException("????????????????????????");
            }
            int twoColorBallPeriods = Integer.valueOf(orderMap.get("twoColorBallPeriods"));
            List<TblDigitalResults> tblNumberResultsList =  tblDigitalResultsRepository.findByPeriodAndLotteryType(twoColorBallPeriods,lotteryType);
            if (null == tblNumberResultsList || tblNumberResultsList.isEmpty()){
                //??????????????????????????????????????????
            }else{
                throw new TimeoutException("????????????????????????");
            }
            //????????????????????????????????????20???????????????????????????????????????
            List<TwoColorBallDetails> twoColorBallDetailsList = getTwoColorDetail(programmes);
            for (TwoColorBallDetails twoColorBallDetails : twoColorBallDetailsList) {
                String redStr = twoColorBallDetails.getRedNumber();
                String twoColorType = twoColorBallDetails.getTwoColorBallType();
                String[] red = redStr.split(",");
                if (red.length > 20 && PlayType.TWOCOLORTYPETWO.getType().equals(twoColorType)){
                    throw new IllegalArgumentException("??????????????????????????????20???");
                }else if (BetNumUtil.judgeStrArrayContainsSameStr(red)){
                    throw new IllegalArgumentException("??????????????????????????????");
                }else if (PlayType.TWOCOLORTYPETHREE.getType().equals(twoColorType)){
                    //??????????????????????????????6???
                   int len = redStr.split(";")[0].split(",").length + redStr.split(";")[1].split(",").length;
                   if (len < 7){
                       throw new IllegalArgumentException("????????????????????????????????????6???");
                   }
                }
            }
        }else{
            //???????????????
            String deadline = orderMap.get("deadline");
            Date matchEndTime = tblMatchService.getMatchEndTime(lotteryType,DateUtil.formatToDate(deadline,DateUtil.DATE_FORMAT_YYYYMMDD_HH_MM));
            Boolean flag = DateUtil.compareDate(matchEndTime,new Date());
            //log.info("??????????????????????{}", flag);
            if (flag) {
                //???????????????????????????????????????
                String match = orderMap.get("deadline_match");
                throw new TimeoutException(match + ",????????????????????????????????????");
            }
        }
        Integer multiple = Integer.valueOf(orderMap.get("multiple"));
        if (multiple > 9999) {
            throw new IllegalArgumentException("????????????????????????9999???");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createOrder(String userId, Map<String, String> orderMap) {
        OrderDetailsInfo orderDetailsInfo = new OrderDetailsInfo();
        Order order = new Order();
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);
        order.setOrderDetails("0");
        Integer lotteryType = Integer.parseInt(orderMap.get("game_type"));
        order.setGameType(lotteryType);

        BigDecimal fee = new BigDecimal(2.00);
        String multiple = orderMap.get("multiple");
        orderDetailsInfo.setMultiple(Integer.valueOf(multiple));
        String betNumber = orderMap.get("number");
        orderDetailsInfo.setBetNumber(Integer.valueOf(betNumber));

        List<OrderDetails> orderDetailsList = new ArrayList<>();
        String programmes = orderMap.get("programmes");
        if (7 == lotteryType){
            List<RankFiveDetails> rankFiveDetailsList = getRankFiveDetails(programmes);
            if (verifyRankFiveNumber(rankFiveDetailsList,betNumber)){
                throw new IllegalArgumentException("???????????????");
            }else{
                OrderDetails orderDetails = new OrderDetails();
                String rankFivePeriods = orderMap.get("rankFivePeriods");
                orderDetails.setIssue(rankFivePeriods);
                orderDetails.setOrderId(orderId);
                orderDetails.setPlaysDetails(JSON.toJSONString(rankFiveDetailsList));
                order.setDeadline(dateUtil.getRankThreeDeadLine());
                orderDetailsList.add(orderDetails);
            }
        }else if (6 == lotteryType){
            List<RankThreeDetails> rankThreeDetailsList = getRankThreeDetails(programmes);
            if (verifyRankThreeNumber(rankThreeDetailsList,betNumber)){
                throw new IllegalArgumentException("???????????????");
            }else{
                OrderDetails orderDetails = new OrderDetails();
                String rankThreePeriods = orderMap.get("rankThreePeriods");
                orderDetails.setIssue(rankThreePeriods);
                orderDetails.setOrderId(orderId);
                orderDetails.setPlaysDetails(JSON.toJSONString(rankThreeDetailsList));
                order.setDeadline(dateUtil.getRankThreeDeadLine());
                orderDetailsList.add(orderDetails);
            }
        }else if (5 == lotteryType){
            String chasing = orderMap.get("chasing");
            orderDetailsInfo.setChasing(Integer.valueOf(chasing));
            if(orderDetailsInfo.getChasing().equals(Integer.valueOf(1))) {
                fee = new BigDecimal(3.00);
            }

            List<SuperLottoDetails> superLottoDetailsList = getSuperLottoDetails(programmes);
            if (verifySuperLottoNumber(superLottoDetailsList,betNumber)){
                throw new IllegalArgumentException("???????????????");
            }else{
                OrderDetails orderDetails = new OrderDetails();
                String superLottoPeriods = orderMap.get("superLottoPeriods");
                orderDetails.setIssue(superLottoPeriods);
                orderDetails.setOrderId(orderId);
                orderDetails.setPlaysDetails(JSON.toJSONString(superLottoDetailsList));
                orderDetailsInfo.setDeadLine(DateUtil.formatDate(dateUtil.getDeadLine(),"yyyy???M???d??? HH???mm???"));
                order.setDeadline(dateUtil.getSuperLottoDeadLine());
                orderDetailsList.add(orderDetails);
            }
        }else if(4 == lotteryType){
            List<ElevenChooseFiveDetails> elevenChooseFiveDetailsList = getElevenChooseFiveDetails(programmes);
            if (verifyElevenChooseFiveNumber(elevenChooseFiveDetailsList,betNumber)){
                throw new IllegalArgumentException("???????????????");
            }else{
                String elevenChooseFivePeriods = orderMap.get("elevenChooseFivePeriods");
                //??????????????????????????????????????????????????????????????????????????????
                if (StringUtils.isBlank(elevenChooseFivePeriods)){
                    throw new IllegalArgumentException("????????????????????????");
                }else{
                    List<TblDigitalResults> tblDigitalResultsList =  tblDigitalResultsRepository.findByPeriodAndLotteryType(Integer.valueOf(elevenChooseFivePeriods),lotteryType);
                    if (!tblDigitalResultsList.isEmpty()){
                        Date lottery = DateUtil.formatToDate(tblDigitalResultsList.get(0).getLotteryTime(),"yyyy-MM-dd HH:mm:ss");
                        OrderDetails orderDetails = new OrderDetails();
                        orderDetails.setIssue(elevenChooseFivePeriods);
                        orderDetails.setOrderId(orderId);
                        orderDetails.setPlaysDetails(JSON.toJSONString(elevenChooseFiveDetailsList));
                        orderDetailsInfo.setDeadLine(DateUtil.formatDate(dateUtil.getDeadLine(),"yyyy???M???d??? HH???mm???"));
                        order.setDeadline(lottery);
                        orderDetailsList.add(orderDetails);
                    }
                }
            }
        }else if(3 == lotteryType){
            List<TwoColorBallDetails> twoColorBallDetailsList = getTwoColorDetail(programmes);
            if (verifyTwoColorBallNumber(twoColorBallDetailsList,betNumber)){
                throw new IllegalArgumentException("???????????????");
            }else{
                OrderDetails orderDetails = new OrderDetails();
                String twoColorBallPeriods = orderMap.get("twoColorBallPeriods");
                orderDetails.setIssue(twoColorBallPeriods);
                orderDetails.setOrderId(orderId);
                orderDetails.setPlaysDetails(JSON.toJSONString(twoColorBallDetailsList));
                orderDetailsInfo.setDeadLine(DateUtil.formatDate(dateUtil.getDeadLine(),"yyyy???M???d??? HH???mm???"));
                order.setDeadline(dateUtil.getDeadLine());
                orderDetailsList.add(orderDetails);
            }
        }else{
            String chuanguan = orderMap.get("chuan_guan");
            orderDetailsInfo.setChuanGuan(chuanguan);

            //11535:004:[33,00]#11535:003:[2,6];11534:001:[3,1]#11534:002:[3];
            //23167:??????002:????????????,???????????????:004:[33,31,11,00]#23167:??????002:????????????,???????????????:001:[3,1];
            List<Match> matchs = getMatch(programmes, lotteryType);

            StringBuffer bodyBuffer = new StringBuffer();
            for (int i = 0; i < matchs.size(); i++) {
                int size = 0;
                Map<String, PlayDetails> plays = matchs.get(i).getPlays();
                for(String key : plays.keySet()) {
                    size += plays.get(key).getItems().size();
                }
                bodyBuffer.append(size);
                if (i < matchs.size()-1){
                    bodyBuffer.append(",");
                }
            }
            if (verifyNumber(chuanguan,bodyBuffer.toString(),multiple,betNumber)){
                throw new IllegalArgumentException("???????????????");
            }
            matchs.forEach(m -> {
                OrderDetails orderDetails = new OrderDetails();
                orderDetails.setIssue(m.getMatchIssue());
                orderDetails.setMatchId(m.getMatchId());
                orderDetails.setMatchName(m.getMatchName());
                orderDetails.setSn(m.getMatchBoth());
                orderDetails.setOrderId(orderId);
                orderDetails.setPlaysDetails(JSON.toJSONString(m.getPlays()));
                orderDetailsList.add(orderDetails);
            });

            Date deadLine = getMinMatchTime(matchs);
            Date matchEndTime = tblMatchService.getMatchEndTime(lotteryType,deadLine);
            order.setDeadline(matchEndTime);
            //?????????????????????????????????????????????????????????????????????

//            if (chuanguan.equals("1") && matchs.size() >1){
//                throw new IllegalArgumentException("??????????????????????????????");
//            }
        }
        String channelNo = orderMap.get("cn");
        if (Strings.isNotBlank(channelNo)) {
            order.setChannelNo(channelNo);
        }
        orderDetailsInfo.setDeadLine(DateUtil.formatDate(dateUtil.getDeadLine(),"yyyy???M???d??? HH???mm???"));

        //????????????
        //4.??????id
        order.setUserId(userId);
        //5.????????????
        fee = fee.multiply(new BigDecimal(multiple)).multiply(new BigDecimal(betNumber));
        BigDecimal feeUnit = fee.multiply(new BigDecimal(100));
        order.setOrderFee(feeUnit);
        order.setCreatedTime(new Date());
        order.setUpdatedTime(new Date());
        //6.???????????????(?????????????????????)
        order.setTransactionFee(new BigDecimal(0));
        //7.????????????(?????????????????????)
        order.setRefundFee(new BigDecimal(0));
        //8.????????????(??????)
        order.setOrderStatus(0);
        //9.????????????
        orderDetailsInfo.setPayStatus(0);
        //10.????????????
        orderDetailsInfo.setBillStatus(0);
        //11.????????????
        orderDetailsInfo.setOpenPrizeStatus(0);
        //12.????????????
        orderDetailsInfo.setHitPrizeStatus(0);
        //13.????????????
        orderDetailsInfo.setDeliveryPrizeStatus(0);
        orderDetailsInfo.setOrderId(orderId);
        //String jsonDetails = JSONObject.toJSONString(details);
        //String jsonDetails = JsonUtil.toJson(details).get();
        //????????????
        if (null == order.getDeadline() || order.getDeadline().before(new Date())){
            throw new IllegalArgumentException("????????????????????????????????????");
        }
        // ?????????????????????(?????????????????????????????????)
        order.setCouponAmount(new BigDecimal(0));
        orderRepository.saveAndFlush(order);
        orderDetailsInfoRepository.save(orderDetailsInfo);
        orderDetailsRepository.saveAll(orderDetailsList);
        Map<String, Object> map = new HashMap<>();
        map.put("order_id", String.valueOf(orderId));
        map.put("total_fee", fee);
        return map;
    }

    private boolean verifyNumber(String chuanguan, String countList, String multiple, String number) {

        String[] guanArr = chuanguan.split(",");
        String[] countArr = countList.split(",");
        int[] guans = Arrays.stream(guanArr).mapToInt(Integer::valueOf).toArray();
        int[] counts = Arrays.stream(countArr).mapToInt(Integer::parseInt).toArray();

        Integer[] guanLists = Arrays.stream( guans ).boxed().toArray( Integer[]::new );
        Integer[] countLists = Arrays.stream( counts ).boxed().toArray( Integer[]::new );

        int count = loop(countLists, guanLists);
        //count *= Integer.valueOf(multiple);
        if (count != Integer.valueOf(number)) {
            return true;
        }
        return false;
    }

    private boolean verifyTwoColorBallNumber(List<TwoColorBallDetails> twoColorBallDetailsList, String number) {
        long count = 0;
        for (TwoColorBallDetails twoColorBallDetails : twoColorBallDetailsList) {
            String redStr = twoColorBallDetails.getRedNumber();
            String blueStr = twoColorBallDetails.getBlueNumber();
            count += twoColorLoop(redStr, blueStr,twoColorBallDetails.getTwoColorBallType());
        }
        log.info("???????????????:{}",count);
        if (count != Long.valueOf(number)) {
            return true;
        }
        return false;
    }

    private boolean verifyElevenChooseFiveNumber(List<ElevenChooseFiveDetails> elevenChooseFiveDetailsList, String number) {
        long count = 0;
        for (ElevenChooseFiveDetails elevenChooseFiveDetails : elevenChooseFiveDetailsList) {
            String numberStr = elevenChooseFiveDetails.getNumber();
            count += elevenChooseFiveLoop(numberStr, elevenChooseFiveDetails.getElevenChoosefiveType());
        }
        log.info("11???5??????:{}",count);
        if (count != Long.valueOf(number)) {
            return true;
        }
        return false;
    }

    private boolean verifySuperLottoNumber(List<SuperLottoDetails> superLottoDetailsList, String number) {
        long count = 0;
        for (SuperLottoDetails superLottoDetails : superLottoDetailsList) {
            String redStr = superLottoDetails.getRedNumber();
            String blueStr = superLottoDetails.getBlueNumber();
            count += superLottoLoop(redStr, blueStr,superLottoDetails.getSuperLottoType());
        }
        log.info("??????????????????:{}",count);
        if (count != Long.valueOf(number)) {
            return true;
        }
        return false;
    }

    private boolean verifyRankThreeNumber(List<RankThreeDetails> rankThreeDetailsList, String number) {
        long count = 0;
        for (RankThreeDetails rankThreeDetails : rankThreeDetailsList) {
            String str = rankThreeDetails.getNumber();
            count += rankThreeLoop(str,rankThreeDetails.getRankThreeType());
        }
        log.info("???????????????:{}",count);
        if (count != Long.valueOf(number)) {
            return true;
        }
        return false;
    }

    private boolean verifyRankFiveNumber(List<RankFiveDetails> rankFiveDetailsList, String number) {
        long count = 0;
        for (RankFiveDetails rankFiveDetails : rankFiveDetailsList) {
            String str = rankFiveDetails.getNumber();
            count += rankFiveLoop(str,rankFiveDetails.getRankFiveType());
        }
        log.info("???????????????:{}",count);
        if (count != Long.valueOf(number)) {
            return true;
        }
        return false;
    }

    private Date getMinMatchTime(List<Match> matchs) {
        List<Long> ids = Lists.newArrayList();
        for (Match match : matchs) {
            ids.add(match.getMatchId());
        }
        Date date = tblMatchService.getMinMatchTime(ids);
        return date;
    }

    private List<Match> getMatch(String programmes, Integer lotteryType) {
        String[] matchsArray = programmes.split(";");
        List<Match> matchList = new ArrayList<>();
        for (String matchArray : matchsArray) {
            Match match = new Match();
            //23167:20190426002:??????002:????????????,???????????????:004:[33,31,11,00]
            //#23167:??????002:????????????,???????????????:c;
            String[] matchObject = matchArray.split("#");
            String[] st = matchObject[0].split(":");
            Long matchId = Long.valueOf(st[0]);
            match.setMatchId(Long.valueOf(st[0]));
            match.setMatchIssue(st[1]);
            match.setMatchBoth(st[2]);
            match.setMatchName(st[3]);
            List<TblMatchOdds> oddsList = matchOddsService.findMatchOddsByMatchId(matchId);
            String[] pl = new String[matchObject.length];
            for (int i = 0; i < matchObject.length; i++) {
                String[] m = matchObject[i].split(":");
                pl[i] = m[4] + ":" + m[5];
            }
            Map<String, PlayDetails> plays = getOddsDetails(oddsList, pl, lotteryType);
            match.setPlays(plays);
            matchList.add(match);
        }
        return matchList;
    }

    private List<TwoColorBallDetails> getTwoColorDetail(String programmes) {
        //"3-1;15;30;01,02,03,04,05,06;07#3-2;15;30;01,02,03,04,05,06,07;08#3-3;15;30;01,02,03,04;06,07,08,09;10#3-1;15;30;01,02,03,04,05,06;08"
        String[] TwoColorBallDetailsArray = programmes.split("#");
        List<TwoColorBallDetails> twoColorBallDetailsList = new ArrayList<>();
        for (String detailArray : TwoColorBallDetailsArray) {
            TwoColorBallDetails twoColorBallDetails = new TwoColorBallDetails();
            //3-1;15;30;01,02,03,04,05,06;07
            String[] st = detailArray.split(";");
            twoColorBallDetails.setTwoColorBallType(st[0]);
            twoColorBallDetails.setBetNumber(st[1]);
            twoColorBallDetails.setOrderFee(new BigDecimal(st[2]));
            if(PlayType.TWOCOLORTYPETHREE.getType().equals(st[0])){
                //??????+??????--??????
                twoColorBallDetails.setRedNumber(st[3]+";"+st[4]);
                twoColorBallDetails.setBlueNumber(st[5]);
            }else{
                twoColorBallDetails.setRedNumber(st[3]);
                twoColorBallDetails.setBlueNumber(st[4]);
            }
            twoColorBallDetailsList.add(twoColorBallDetails);
        }
        return twoColorBallDetailsList;
    }

    private List<ElevenChooseFiveDetails> getElevenChooseFiveDetails(String programmes) {
        //4-1-1-1;1;2;01#4-3-1-1;3;6;01,02,03
        String[] twoColorBallDetailsArray = programmes.split("#");
        List<ElevenChooseFiveDetails> elevenChooseFiveDetailsList = new ArrayList<>();
        for (String detailArray : twoColorBallDetailsArray) {
            ElevenChooseFiveDetails elevenChooseFiveDetails = new ElevenChooseFiveDetails();
            //4-3-1-1;3;6;01,02,03#
            String[] st = detailArray.split(";");
            elevenChooseFiveDetails.setElevenChoosefiveType(st[0]);
            elevenChooseFiveDetails.setBetNumber(st[1]);
            elevenChooseFiveDetails.setOrderFee(new BigDecimal(st[2]));
            if (PlayType.ELEVENCHOOSEFIVE4131.getType().equals(st[0])){
                elevenChooseFiveDetails.setNumber(st[3]+";"+st[4]+";"+st[5]);
            }else if(PlayType.ELEVENCHOOSEFIVE4121.getType().equals(st[0]) || st[0].startsWith("4-2")){
                elevenChooseFiveDetails.setNumber(st[3]+";"+st[4]);
            }else{
                elevenChooseFiveDetails.setNumber(st[3]);
            }
            elevenChooseFiveDetailsList.add(elevenChooseFiveDetails);
        }
        return elevenChooseFiveDetailsList;
    }

    private List<SuperLottoDetails> getSuperLottoDetails(String programmes) {
        //5-3;8;16;01,02,03,04;06,07,08,09;10;11,12#
        String[] superLottoDetailsArray = programmes.split("#");
        List<SuperLottoDetails> superLottoDetailsList = new ArrayList<>();
        for (String detailArray : superLottoDetailsArray) {
            SuperLottoDetails superLottoDetails = new SuperLottoDetails();
            //3-1;15;30;01,02,03,04,05,06;07
            String[] st = detailArray.split(";");
            superLottoDetails.setSuperLottoType(st[0]);
            superLottoDetails.setBetNumber(st[1]);
            superLottoDetails.setOrderFee(new BigDecimal(st[2]));
            if(PlayType.SUPERLOTTOTHREE.getType().equals(st[0])){
                //??????+??????--??????
                superLottoDetails.setRedNumber(st[3]+";"+st[4]);
                if (st.length > 6){
                    //????????????????????????
                    superLottoDetails.setBlueNumber(st[5]+";"+st[6]);
                }else{
                    superLottoDetails.setBlueNumber(st[5]);
                }
            }else{
                superLottoDetails.setRedNumber(st[3]);
                superLottoDetails.setBlueNumber(st[4]);
            }
            superLottoDetailsList.add(superLottoDetails);
        }
        return superLottoDetailsList;
    }

    private List<RankThreeDetails> getRankThreeDetails(String programmes) {
        //4-1-1-1;1;2;01#4-3-1-1;3;6;01,02,03
        String[] rankThreeDetailsArray = programmes.split("#");
        List<RankThreeDetails> rankThreeDetailsList = new ArrayList<>();
        for (String detailArray : rankThreeDetailsArray) {
            RankThreeDetails rankThreeDetails = new RankThreeDetails();
            //6-1;4;8;0,1;2,3;4
            String[] st = detailArray.split(";");
            rankThreeDetails.setRankThreeType(st[0]);
            rankThreeDetails.setBetNumber(st[1]);
            rankThreeDetails.setOrderFee(new BigDecimal(st[2]));
            if (PlayType.RANK61.getType().equals(st[0])){
                rankThreeDetails.setNumber(st[3]+";"+st[4]+";"+st[5]);
            }else{
                rankThreeDetails.setNumber(st[3]);
            }
            rankThreeDetailsList.add(rankThreeDetails);
        }
        return rankThreeDetailsList;
    }

    private List<RankFiveDetails> getRankFiveDetails(String programmes) {
        String[] rankFiveDetailsArray = programmes.split("#");
        List<RankFiveDetails> rankFiveDetailsList = new ArrayList<>();
        for (String detailArray : rankFiveDetailsArray) {
            RankFiveDetails rankFiveDetails = new RankFiveDetails();
            //7-1;1;2;1;2;3;4;5
            String[] st = detailArray.split(";");
            rankFiveDetails.setRankFiveType(st[0]);
            rankFiveDetails.setBetNumber(st[1]);
            rankFiveDetails.setOrderFee(new BigDecimal(st[2]));
            rankFiveDetails.setNumber(st[3]+";"+st[4]+";"+st[5]+";"+st[6]+";"+st[7]);
            rankFiveDetailsList.add(rankFiveDetails);
        }
        return rankFiveDetailsList;
    }

    private Map<String, PlayDetails> getOddsDetails(List<TblMatchOdds> oddsList, String[] matchArray, Integer lotteryType) {
        String[] nums = new String[matchArray.length];
        String[] pl = new String[matchArray.length];
        for (int i = 0; i < matchArray.length; i++) {
            nums[i] = matchArray[i].split(":")[0];
            pl[i] = matchArray[i].split(":")[1];
        }
        String[] playNums = new String[oddsList.size()];
        for (int m = 0; m < oddsList.size(); m++) {
            playNums[m] = oddsList.get(m).getPlayNum();
        }
        for (String num : nums){
            if (-1 == indexArray(playNums, num)) {
                //?????????????????????????????????????????????
                throw new IllegalArgumentException("??????????????????");
            }
        }
        Map<String, PlayDetails> plays = new HashMap<>();
        for (TblMatchOdds tblMatchOdds : oddsList) {
            String playNum = tblMatchOdds.getPlayNum();
            int index = indexArray(nums, playNum);
            if (-1 != index) {
                PlayDetails details = new PlayDetails();
                HashMap<String, Object> map = JSON.parseObject(tblMatchOdds.getPlayDetails(), HashMap.class);
//                log.info("184----is_danguan:{}", map.get("is_danguan"));
                details.setIsDanGuan(String.valueOf(map.get("is_danguan")));
                String[] pls = orderUtil.getBetTemplate(playNum, lotteryType);
                List<String> sp = JSON.parseArray(map.get("sp").toString(), String.class);
                String[] msp = sp.toArray(new String[0]);
                if (PlayType.RFSF.getType().equals(playNum) || PlayType.RQSPF.getType().equals(playNum)) {
                    details.setHandicap(String.valueOf(map.get("handicap")));
                }
                if (PlayType.DXF.getType().equals(playNum)) {
                    details.setScore(String.valueOf(map.get("odds")));
                }
                String odds = pl[index].replace("[", "").replace("]", "");
                List<Play> mapList = orderUtil.oddsTemplate(odds, msp, pls);
                details.setItems(mapList);
                plays.put(tblMatchOdds.getPlayCode(), details);
            }
        }
        return plays;
    }

    //????????????
    private int indexArray(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return -1;//???if????????????????????????????????????????????????-1
    }

    @Override
    public Map<String, Object> queryOrderList(String userId, String gameType,int page,int size) {
//        final int size = 9;
        if (page == 0) {
            page = 1;
        }
        List<Long> lotteryTypes = new ArrayList<Long>();
        String[] arr = gameType.split(",");
        for (int i = 0; i < arr.length; i++) {
            lotteryTypes.add(Long.valueOf(arr[i]));
        }
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Order> monthsOrder = orderRepository.findByThreeMonthsOrder(userId, lotteryTypes, pageRequest);
        log.info("????????????{}?????????", monthsOrder.getTotalElements());
        Map<String, Object> resultMap = new HashMap<>();
        ArrayList<Map<String, Object>> orders = new ArrayList<>();
        for (Order order : monthsOrder.getContent()) {
            Map<String, Object> orderMap = new HashMap<>();
            //??????id
            orderMap.put("order_id", order.getOrderId().toString());
            //????????????
            orderMap.put("game_type", order.getGameType());
            //????????????
            orderMap.put("order_fee", getRealPrice(order.getOrderFee()));
            //????????????
            Date createdTime = order.getCreatedTime();
            try {
                String createdDate = dateFormatUtil.correcToMinute(createdTime);
                orderMap.put("created_time", createdDate);
            } catch (ParseException e) {
                e.printStackTrace();
                log.error("???????????????????????????????????????");
            }
            //????????????
            //????????????
            OrderDetailsInfo orderDetailsInfo = orderDetailsInfoRepository.findByOrderId(order.getOrderId());
            orderMap.put("bonus", orderDetailsInfo.getBonus());
            //updateOrderStatusUtil.updateListPageOrdersStatus(order.getOrderDetails(), orderMap, order.getOrderStatus());
            fetchOrderListStatus(order, orderDetailsInfo, orderMap);
            orders.add(orderMap);
        }
        //??????????????????????????????????????????3???????????????????????????
        int totalPages = monthsOrder.getTotalPages();
        if (page == totalPages) {
            resultMap.put("order_end", "???3???????????????????????????");
        }
        resultMap.put("total_page", totalPages);
        resultMap.put("orders", orders);
        return resultMap;
    }

    @Override
    public Map<String, Object> queryOrderDetails(String userId, Long orderId) {
        Map<String, Object> orderMap = new HashMap<>();
        Order order = orderRepository.findByOrderIdAndUserId(orderId, userId);
//        String orderDetails = order.getOrderDetails();
//        OrderDetails details = parseObject(orderDetails, OrderDetails.class);
        OrderDetailsInfo orderDetailsInfo = orderDetailsInfoRepository.findByOrderId(orderId);
        //1.????????????
        Integer lotteryType = order.getGameType();
        orderMap.put("game_type", lotteryType);
        //2.????????????
        orderMap.put("order_fee", getRealPrice(order.getOrderFee()));
        //3.???????????????
        BigDecimal bonus = orderDetailsInfo.getBonus();
        BigDecimal baseNum = new BigDecimal(100);
        BigDecimal bonusNum = bonus.divide(baseNum).setScale(2, RoundingMode.FLOOR);
        orderMap.put("bonus", bonusNum);
        //4.????????????
        BigDecimal refundNum = order.getRefundFee().divide(baseNum);
        orderMap.put("refund_fee", refundNum);
        //5.????????????
//        updateOrderStatusUtil.updateDetailsOrderStatus(orderDetails, orderMap, order.getOrderStatus());
//        //6.????????????
//        updateOrderStatusUtil.updateDetailsPayStatus(orderDetails, orderMap, order.getOrderStatus());
        fetchOrderDetailStatus(order, orderDetailsInfo, orderMap);
        //7.????????????
        StringBuilder sb = new StringBuilder();
        String number = String.valueOf(orderDetailsInfo.getBetNumber());
        String multiple = String.valueOf(orderDetailsInfo.getMultiple());

        List<OrderDetails> orderDetailsList = orderDetailsRepository.findByOrderId(orderId);
        if (lotteryType == 1 || lotteryType == 2){
            String chuanGuan = orderDetailsInfo.getChuanGuan();
            String[] chuanGuanArray = chuanGuan.split(",");
            Arrays.sort(chuanGuanArray);
            if (chuanGuan.contains("1")) {
                sb.append("?????????");
            }
            Stream.of(chuanGuanArray).
                    filter(s -> !"1".equals(s)).
                    forEach(s -> sb.append(s).append("???1???")
                    );
            //???????????? {session:match_sn,both,play_code,item,result}

//            List<BigInteger> successMatchIdList = tblBettingTicketDetailsRepository.getSuccessMatchList(order.getOrderId());
//            List<Long> successMatchList = successMatchIdList.stream().map(BigInteger::longValue).collect(Collectors.toList());

            List<Map<String, Object>> matchDetails = getMatchDetails(orderDetailsList, lotteryType);
            orderMap.put("match_details", matchDetails);
        }else if (lotteryType == 3){
            // add ???????????????
            OrderDetails orderDetails = orderDetailsList.get(0);
            String twoColorBallPeriods = orderDetails.getIssue();
            orderMap.put("twoColorBallPeriods", twoColorBallPeriods);
            // add ?????????????????????
            List<TwoColorBallDetails> twoColorBallDetails = JSONArray.parseArray(orderDetails.getPlaysDetails(), TwoColorBallDetails.class);
            List<Map<String, Object>> colorDetails = getColorDetails(twoColorBallDetails);
            orderMap.put("twoColorBall_details", colorDetails);
            //????????????
            List<TblDigitalResults> tblNumberResultsList =  tblDigitalResultsRepository.findByPeriodAndLotteryType(Integer.valueOf(twoColorBallPeriods),lotteryType);
            if (tblNumberResultsList.isEmpty()){
                orderMap.put("lotteryTime", DateUtil.formatDate(order.getDeadline(),DateUtil.DEFAULT_DATE_FORMAT));
            }else{
                orderMap.put("lotteryTime", tblNumberResultsList.get(0).getLotteryTime());
                String result = tblNumberResultsList.get(0).getLotteryResult();
                String[] str = result.split(":");
                if (str.length>1){
                    orderMap.put("redBall", str[0]);
                    orderMap.put("blueBall", str[1]);
                }
                //??????????????????
//                if (tblNumberResultsList.size() == 3 ){
//                    Set<String> set = new HashSet<String>();
//                    for (TblDigitalResults re : tblNumberResultsList){
//                        set.add(re.getLotteryResult());
//                    }
//                    if (set.size() == 1){
//                        String result = tblNumberResultsList.get(0).getLotteryResult();
//                        String[] str = result.split(":");
//                        if (str.length>1){
//                            orderMap.put("redBall", str[0]);
//                            orderMap.put("blueBall", str[1]);
//                        }
//                    }
//                }
            }
            List<Map<String, Object>> matchDetails = getColorDetails(twoColorBallDetails);
            orderMap.put("match_details", matchDetails);
        }else if (lotteryType == 4){
            // add ???11???5??????
            OrderDetails orderDetails = orderDetailsList.get(0);
            String elevenChooseFivePeriods = orderDetails.getIssue();
            orderMap.put("elevenChooseFivePeriods", elevenChooseFivePeriods);
            // add ???11???5????????????
            List<ElevenChooseFiveDetails> elevenChooseFiveDetails = JSONArray.parseArray(orderDetails.getPlaysDetails(), ElevenChooseFiveDetails.class);
            orderMap.put("elevenChooseFive_details", elevenChooseFiveDetails);
            //????????????
            List<TblDigitalResults> tblDigitalResultsList =  tblDigitalResultsRepository.findByPeriodAndLotteryType(Integer.valueOf(elevenChooseFivePeriods),lotteryType);
            if (tblDigitalResultsList.isEmpty()){
                //???20??????????????????????????????????????????????????????????????????????????????????????????
                orderMap.put("lotteryTime", DateUtil.formatDate(DateUtils.addMinutes(order.getDeadline(),5),7));
                orderMap.put("number", "??????");
            }else{
                Date d = DateUtil.formatToDate(tblDigitalResultsList.get(0).getLotteryTime(),DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS);
                orderMap.put("lotteryTime",DateUtil.formatDate(DateUtils.addMinutes(d,5),7));
                String result = tblDigitalResultsList.get(0).getLotteryResult();
                orderMap.put("number", result);
            }
        }else if (lotteryType == 5){
            // ???????????????
            OrderDetails orderDetails = orderDetailsList.get(0);
            String superLottoPeriods = orderDetails.getIssue();
            orderMap.put("superLottoPeriods", superLottoPeriods);
            // ?????????????????????
            List<SuperLottoDetails> superLottoDetails = JSONArray.parseArray(orderDetails.getPlaysDetails(), SuperLottoDetails.class);
            orderMap.put("superLotto_details", superLottoDetails);
            //????????????
            List<TblDigitalResults> tblDigitalResultsList =  tblDigitalResultsRepository.findByPeriodAndLotteryType(Integer.valueOf(superLottoPeriods),lotteryType);
            if (tblDigitalResultsList.isEmpty()){
                orderMap.put("lotteryTime", DateUtil.formatDate(order.getDeadline(),DateUtil.DEFAULT_DATE_FORMAT));
            }else{
                orderMap.put("lotteryTime", tblDigitalResultsList.get(0).getLotteryTime());
                String result = tblDigitalResultsList.get(0).getLotteryResult();
                String[] str = result.split(":");
                if (str.length>1){
                    orderMap.put("redBall", str[0]);
                    orderMap.put("blueBall", str[1]);
                }
            }
        }else if (lotteryType == 6){
            //???????????????
            OrderDetails orderDetails = orderDetailsList.get(0);
            String rankThreePeriods = orderDetails.getIssue();
            orderMap.put("rankThreePeriods", rankThreePeriods);
            // ?????????????????????
            List<RankThreeDetails> rankThreeDetails = JSONArray.parseArray(orderDetails.getPlaysDetails(), RankThreeDetails.class);
            orderMap.put("rankThree_details", rankThreeDetails);
            //????????????
            List<TblDigitalResults> tblDigitalResultsList =  tblDigitalResultsRepository.findByPeriodAndLotteryType(Integer.valueOf(rankThreePeriods),lotteryType);
            if (tblDigitalResultsList.isEmpty()){
                orderMap.put("lotteryTime", DateUtil.formatDate(order.getDeadline(),DateUtil.DEFAULT_DATE_FORMAT));
            }else{
                orderMap.put("lotteryTime", tblDigitalResultsList.get(0).getLotteryTime());
                orderMap.put("number", tblDigitalResultsList.get(0).getLotteryResult());
            }
        }else if (lotteryType == 7){
            //???????????????
            OrderDetails orderDetails = orderDetailsList.get(0);
            String rankFivePeriods = orderDetails.getIssue();
            orderMap.put("rankFivePeriods", rankFivePeriods);
            // ?????????????????????
            List<RankFiveDetails> rankFiveDetails = JSONArray.parseArray(orderDetails.getPlaysDetails(), RankFiveDetails.class);
            orderMap.put("rankFive_details", rankFiveDetails);
            //????????????
            List<TblDigitalResults> tblDigitalResultsList =  tblDigitalResultsRepository.findByPeriodAndLotteryType(Integer.valueOf(rankFivePeriods),lotteryType);
            if (tblDigitalResultsList.isEmpty()){
                orderMap.put("lotteryTime", DateUtil.formatDate(order.getDeadline(),DateUtil.DEFAULT_DATE_FORMAT));
            }else{
                orderMap.put("lotteryTime", tblDigitalResultsList.get(0).getLotteryTime());
                orderMap.put("number", tblDigitalResultsList.get(0).getLotteryResult());
            }
        }
        if (orderDetailsInfo.getChasing().equals(Integer.valueOf(1))){
            sb.append(number).append("??????").append("???????????????").append(multiple).append("???");
        }else{
            sb.append(number).append("??????").append(multiple).append("???");
        }
        String betManner = sb.toString();
        orderMap.put("bet_manner", betManner);
        BigDecimal couponAmount = order.getCouponAmount().divide(baseNum);
        orderMap.put("couponAmount",couponAmount);
        //8.????????????
        String deadline = DateUtil.formatDate(order.getDeadline(),"yyyy???MM???dd??? HH???mm???");
        orderMap.put("deadline", deadline);
        //9.????????????
        orderMap.put("order_id", String.valueOf(order.getOrderId()));
        //10.???????????? {session:match_sn,both,play_code,item,result}
//        List<Match> matches = details.getMatchs();
//
//        List<BigInteger> successMatchIdList = tblBettingTicketDetailsRepository.getSuccessMatchList(order.getOrderId());
//        List<Long> successMatchList = successMatchIdList.stream().map(BigInteger::longValue).collect(Collectors.toList());
        return orderMap;
    }

    private BigDecimal getRealPrice(BigDecimal amount) {
        BigDecimal baseNum = new BigDecimal(100);
        return amount.divide(baseNum);
    }

    private List<Map<String, Object>> getMatchDetails(List<OrderDetails> orderDetailsList, Integer lotteryType) {
        List<Map<String, Object>> detailsList = new ArrayList<>();
        Set<Long> matchIds = orderDetailsList.stream().map(OrderDetails::getMatchId).collect(Collectors.toSet());
        List<TblMatch> matchList = matchRepository.findAllById(matchIds);
        //id match
        Map<Long, TblMatch> matchMap = matchList.stream().collect(Collectors.toMap(TblMatch::getId, s -> s));
        orderDetailsList.forEach(details -> {
            TblMatch tblMatch = matchMap.get(details.getMatchId());
            Map<String, Object> detailMap = new HashMap<>();
            detailMap.put("match_sn", details.getSn());
            detailMap.put("match_name", details.getMatchName());
            detailMap.put("match_issue", details.getIssue());
//            Long matchId = new Long(matchDB.getMatchId());
//            if (successMatchIdList.contains(matchId)) {
//                detailMap.put("bet_result", "success");
//            } else {
//                detailMap.put("bet_result", "fail");
//            }
            Map<String, JSONObject> plays = JSONObject.parseObject(details.getPlaysDetails(), HashMap.class);
            //????????????

            String matchResults = tblMatch.getMatchResults();
            Map<String, String> resultMap = null;
            if (!StringUtils.isBlank(matchResults)) {
                MatchResult matchResult = parseObject(matchResults, MatchResult.class);
                resultMap = matchResult.getResult().stream().collect(Collectors.toMap(MatchResultItem::getCode, MatchResultItem::getResult));
            }
            Set<String> keySet = plays.keySet();
            List<Map<String, Object>> playList = new ArrayList<>();
            for (String key : keySet) {                             //key:spf
                Map<String, Object> itemMap = new HashMap<>();
                String item = "";
                if (resultMap != null) {
                    item = resultMap.get(key);                     //result
                }
                PlayDetails pays = JSONObject.parseObject(plays.get(key).toString(), PlayDetails.class);               //value:{is_dan_guan:0,items:[]}
                PlayDetails playDetails1 = getPlays(key, pays, item);
                String playCode = orderUtil.getPlayCode(key, lotteryType);
                itemMap.put("play_code", playCode);
                List<Play> items = playDetails1.getItems();
                for (Play play : items){
                    if ("success".equals(play.getBetResult())){
                        itemMap.put("betResult", "success");
                    }
                }
                itemMap.put("items", items);
                itemMap.put("is_dan_guan", pays.getIsDanGuan());
                String handicap = playDetails1.getHandicap();
                if (!StringUtils.isEmpty(handicap)) {
                    itemMap.put("handicap", playDetails1.getHandicap());
                }
                String score = playDetails1.getScore();
                if (!StringUtils.isEmpty(score)) {
                    itemMap.put("score", playDetails1.getScore());
                }
                playList.add(itemMap);
            }
            detailMap.put("plays", playList);
            detailsList.add(detailMap);
        });
        return detailsList;
    }

    private PlayDetails getPlays(String playCode, PlayDetails details, String itemDB) {
        PlayDetails playDetails = new PlayDetails();
        String isDanGuan = details.getIsDanGuan();
//        log.info("??????{}", isDanGuan);
        playDetails.setIsDanGuan(isDanGuan);
        if (PlayType.RQSPF.getValue("002").equals(playCode) || PlayType.RFSF.getValue("002").equals(playCode)) {
            playDetails.setHandicap(details.getHandicap());
        }
        if (PlayType.DXF.getValue("003").equals(playCode)) {
            playDetails.setScore(details.getScore());
        }
        List<Play> items = details.getItems();
        List<Play> itemList = new ArrayList<>();
        for (Play playOrder : items) {
            Play play = new Play();
            String item = playOrder.getItem();
            String result = playOrder.getResult();
            String odds = playOrder.getOdds();
            String betItem = orderUtil.getBetItem(item, playCode);//   ?????????/??????
            if (StringUtils.isEmpty(itemDB)) {
                play.setResult(result);
            } else {
                String resultEnd = orderUtil.getBetItem(itemDB, playCode);
                log.info("???????????????:{}", resultEnd);
                play.setResult(resultEnd);
            }
            //?????????????????????
            if (play.getResult().equals(betItem)){
                play.setBetResult("success");
            }else {
                play.setBetResult("fail");
            }
            play.setItem(betItem + "(" + odds + ")");
            play.setOdds(odds);
            //?????????????????????
            if (play.getResult().equals(betItem)){
                play.setBetResult("success");
            }else {
                play.setBetResult("fail");
            }
            itemList.add(play);
        }
        playDetails.setItems(itemList);
        return playDetails;
    }

    private List<Map<String, Object>> getColorDetails(List<TwoColorBallDetails> twoColorBallDetails){
        List<Map<String, Object>> detailsList = new ArrayList<>();
        for (TwoColorBallDetails ballDetails : twoColorBallDetails){
            Map<String, Object> itemMap = new HashMap<>();
            String twoColorBallType = ballDetails.getTwoColorBallType();
            if (PlayType.TWOCOLORTYPEONE.getType().equals(twoColorBallType)){
                itemMap.put("twoColorBallTypeName", PlayType.TWOCOLORTYPEONE.getValue(twoColorBallType));
                itemMap.put("redNumber", ballDetails.getRedNumber());
            }else if (PlayType.TWOCOLORTYPETWO.getType().equals(twoColorBallType)){
                itemMap.put("twoColorBallTypeName", PlayType.TWOCOLORTYPETWO.getValue(twoColorBallType));
                itemMap.put("redNumber", ballDetails.getRedNumber());
            }else if (PlayType.TWOCOLORTYPETHREE.getType().equals(twoColorBallType)){
                itemMap.put("twoColorBallTypeName", PlayType.TWOCOLORTYPETHREE.getValue(twoColorBallType));
                String redBall = ballDetails.getRedNumber();
                String[] str = redBall.split(";");
                itemMap.put("mainRedNumber", str[0]);
                itemMap.put("slaveRedNumber", str[1]);
            }
            itemMap.put("blueNumber", ballDetails.getBlueNumber());
            detailsList.add(itemMap);
        }
        return detailsList;
    }

    @Override
    public Result minusOrderFee(String userId, Long orderId,Long userCouponId) {
        Order order = orderRepository.findByOrderIdAndUserId(orderId, userId);
        BigDecimal orderFee = order.getOrderFee();
        orderFee = orderFee.divide(new BigDecimal(100));
        Integer gameType = order.getGameType();
        return userBalanceService.deduction(userId, orderFee, orderId, userCouponId,gameType);
    }

    @Override
    public void updatePayRefundingStatus(Long orderId) {

    }

    @Override
    @LockAction(key = LockKey.lockOrder, value = "#orderId", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public void updatePayRefundedStatus(Long orderId, BigDecimal amount, Integer status) {
        Order order = orderRepository.findByOrderId(orderId);
        OrderDetailsInfo orderDetailsInfo = orderDetailsInfoRepository.findByOrderId(orderId);

        if (amount.compareTo(BigDecimal.ZERO) == 1) {
            orderDetailsInfo.setPayStatus(Order.PAY_STATUS_REFUND);
        }
        orderDetailsInfo.setBillStatus(status);
        if (amount.compareTo(order.getRefundFee()) == 1) {
            order.setRefundFee(amount);
        }

        orderDetailsInfoRepository.saveAndFlush(orderDetailsInfo);
        orderRepository.saveAndFlush(order);
    }

    @Override
    @LockAction(key = LockKey.lockOrder, value = "#orderId", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public void updatePartBillStatus(Long orderId, BigDecimal amount) {

        log.info("updatePartBillStatus:{}", orderId);

        Order order = orderRepository.findByOrderId(orderId);

        OrderDetailsInfo orderDetailsInfo = orderDetailsInfoRepository.findByOrderId(orderId);
        orderDetailsInfo.setBillStatus(Order.BILL_STATUS_PART);
        if (amount.compareTo(BigDecimal.ZERO) == 1) {
            orderDetailsInfo.setPayStatus(Order.PAY_STATUS_REFUNDING);
        }

        order.setRefundFee(amount);
        order.setTransactionFee(order.getTransactionFee().subtract(amount));

        orderDetailsInfoRepository.saveAndFlush(orderDetailsInfo);
        orderRepository.saveAndFlush(order);
    }

    @Override
    @LockAction(key = LockKey.lockOrder, value = "#orderId", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public void updateAllBillStatus(Long orderId, Integer status) {
        OrderDetailsInfo orderDetailsInfo = orderDetailsInfoRepository.findByOrderId(orderId);
        if (orderDetailsInfo.getBillStatus() == status) {
            return;
        }
        orderDetailsInfo.setBillStatus(status);
        orderDetailsInfoRepository.saveAndFlush(orderDetailsInfo);
    }

    @Override
    @LockAction(key = LockKey.lockOrder, value = "#orderId", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public void updateOpenAllPrizeStatus(Long orderId, Integer state, Integer hitStatus, BigDecimal amount) {
        OrderDetailsInfo orderDetailsInfo = orderDetailsInfoRepository.findByOrderId(orderId);
        orderDetailsInfo.setOpenPrizeStatus(state);
        orderDetailsInfo.setBonus(amount);
        orderDetailsInfo.setHitPrizeStatus(hitStatus);
        if (hitStatus.equals(PrizeStatus.wait.getValue())) {
            orderDetailsInfo.setDeliveryPrizeStatus(0);
        }
        if (hitStatus.equals(PrizeStatus.awarded.getValue())) {
            orderDetailsInfo.setDeliveryPrizeStatus(1);
        }
        if (hitStatus.equals(PrizeStatus.bigAwarded.getValue())) {
            orderDetailsInfo.setDeliveryPrizeStatus(2);
        }
        orderDetailsInfoRepository.saveAndFlush(orderDetailsInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateOpenAndHitPrizeStatus(List<HitPrizeResult> hitPrizeResults) {
        Set<Long> set = new HashSet<>();
        for (int i = 0; i < hitPrizeResults.size(); i++) {
            set.add(hitPrizeResults.get(i).getOrderId());
        }
        log.info("orderIds={}", set);
        //????????????????????????
        List<Order> orders = orderRepository.findByOrderIdIn(new ArrayList<>(set));
        log.info("orders={}", orders);
        //???????????? orderId:order

        hitPrizeResults.forEach(hitPrizeResult ->
                {
                    Long orderId = hitPrizeResult.getOrderId();
                    OrderDetailsInfo orderDetailsInfo = orderDetailsInfoRepository.findByOrderId(orderId);
                    if (orderDetailsInfo != null) {
                        //????????????
                        orderDetailsInfo.setOpenPrizeStatus(1);
                        if (hitPrizeResult.getHitPrizeStatus() == 2) {
                            orderDetailsInfo.setOpenPrizeStatus(2);
                        }
                        if (hitPrizeResult.getBonus().compareTo(orderDetailsInfo.getBonus()) > 0) {
                            //???????????????
                            //??????
                            orderDetailsInfo.setHitPrizeStatus(1);
                            orderDetailsInfo.setBonus(hitPrizeResult.getBonus());
                        }

                        orderDetailsInfoRepository.saveAndFlush(orderDetailsInfo);
                    }
                }
        );


    }

    @Override
    public void updateInvalidOrder() {
        orderRepository.updateInvaidOrder();
    }

    @Override
    public Order findByOrderId(Long orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    @Override
    public Order findByOrderIdAndUserId(Long orderId, String userId) {
        return orderRepository.findByOrderIdAndUserId(orderId, userId);
    }

    @Override
    @LockAction(key = LockKey.lockOrder, value = "#order.orderId", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public void updateOrderOdds(Order order) {
        orderRepository.saveAndFlush(order);
    }

    @Override
    public void updateStatus(String orderId) {
        orderRepository.updateStatus(orderId);
    }

    @Override
    public void updateOrder(Order order) {
        orderRepository.saveAndFlush(order);
    }

    @Override
    public Map<String, Object> queryAvailableCouponList(Long orderId) {
        Order order = orderRepository.findByOrderId(orderId);
        if (order == null) {
            throw new NullPointerException("???????????????");
        }
        Long userId = Long.valueOf(order.getUserId());
        BigDecimal orderFee = order.getOrderFee();
        Integer gameType = order.getGameType();
        List<TblUserCoupon> tblUserCouponList = tblUserCouponService.findAllowedToUseCoupon(userId,orderFee,gameType);
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> couponList = new ArrayList<>();
        for (TblUserCoupon tblUserCoupon : tblUserCouponList){
            Map<String, Object> map = new HashMap<>();
            map.put("activityId",String.valueOf(tblUserCoupon.getActivityId()));
            map.put("couponAmount",tblUserCoupon.getCouponAmount());
            map.put("couponId",String.valueOf(tblUserCoupon.getCouponId()));
            map.put("couponName",tblUserCoupon.getCouponName());

            String[] ruleArr = tblUserCoupon.getCouponRules().split("@");
            for (int i = 0; i < ruleArr.length; i++) {
                String[] rules = ruleArr[i].split(":");
                if (rules[0].equals(CouponRules.use001)) {
                    map.put("use001", rules[1]);
                }
                if (rules[0].equals(CouponRules.use002)) {
                    map.put("use002", rules[1]);
                }
            }
//            map.put("couponRules",tblUserCoupon.getCouponRules());
            map.put("couponStatus",tblUserCoupon.getCouponStatus());
            map.put("couponType",tblUserCoupon.getCouponType());
            map.put("startTime", DateFormatUtils.format(tblUserCoupon.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
            map.put("endTime", DateFormatUtils.format(tblUserCoupon.getEndTime(), "yyyy/MM/dd HH:mm:ss"));
            map.put("formatEndDate",tblUserCoupon.getFormatEndDate());
            map.put("formatStartDate",tblUserCoupon.getFormatStartDate());
            map.put("userCouponId",String.valueOf(tblUserCoupon.getUserCouponId()));
            map.put("userId",String.valueOf(tblUserCoupon.getUserId()));
            couponList.add(map);
        }
        resultMap.put("coupons",couponList);
        return resultMap;
    }

    @Override
    public Result checkOrderOdds(String userId, Long orderId, Long userCouponId) throws ParseException, TimeoutException {
        Order order = orderRepository.findByOrderIdAndUserId(orderId, userId);
        if (order == null) {
            throw new NullPointerException("???????????????");
        }
        //1.??????????????????
        if (order.getOrderStatus() != 0) {
            throw new IllegalStateException("??????????????????,???????????????");
        }

        OrderDetailsInfo orderDetailsInfo = orderDetailsInfoRepository.findByOrderId(orderId);

        if (!orderDetailsInfo.getPayStatus().equals(Integer.valueOf(0))) {
            throw new IllegalStateException("??????????????????");
        }
        Date deadline = order.getDeadline();
        Boolean flag = DateUtil.compareDate(deadline,new Date());
        if (flag) {
            //???????????????????????????????????????
//            String match = detailsObject.get("deadline_match").toString();
//            //??????????????????---?????????
//            order.setOrderStatus(1);
//            orderRepository.save(order);
            throw new TimeoutException("????????????????????????????????????");
        }
        //4.????????????
            Result result = minusOrderFee(userId, orderId, userCouponId);
            Integer code = result.getCode();
            if (code == StatusCode.OK.getCode()){
                //??????????????????
//                objectDetails.put("user_id", order.getUserId());
                orderDetailsInfo.setPayStatus(1);
                orderDetailsInfo.setBillStatus(0);
                //????????????????????????????????????Id
                order.setUserCouponId(userCouponId);
                if (userCouponId == 0){
                    order.setCouponAmount(BigDecimal.ZERO);
                }else{
                    TblUserCoupon userCoupon = tblUserCouponService.findFirstByUserCouponId(userCouponId);
                    BigDecimal couponAmount = userCoupon.getCouponAmount();
                    order.setCouponAmount(couponAmount);
                }
                orderRepository.save(order);
                log.info("????????????????????????????????????");
                //??????????????????????????????????????????
                ThreadPool.getInstance().executeRunnable(new AfterPayTask(order.getOrderId(), order.getUserId(),
                        order.getGameType(), new Timestamp(deadline.getTime()),
                        splitService, lotteryBetService));
            }
            return result;
    }
}
