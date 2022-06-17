package com.linglong.lottery_backend.ticket.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.linglong.lottery_backend.common.bean.LockKey;
import com.linglong.lottery_backend.common.service.JpaBatch;
import com.linglong.lottery_backend.common.error.BusinessException;
import com.linglong.lottery_backend.order.entity.OrderDetails;
import com.linglong.lottery_backend.order.entity.OrderDetailsInfo;
import com.linglong.lottery_backend.order.model.order_model.PlayType;
import com.linglong.lottery_backend.order.entity.Order;
import com.linglong.lottery_backend.order.repository.OrderDetailsInfoRepository;
import com.linglong.lottery_backend.order.repository.OrderDetailsRepository;
import com.linglong.lottery_backend.order.repository.OrderRepository;
import com.linglong.lottery_backend.order.service.bean.TwoColorBallDetails;
import com.linglong.lottery_backend.prize.entity.BettingTicketDigital;
import com.linglong.lottery_backend.ticket.bean.*;
import com.linglong.lottery_backend.ticket.bean.sport.common.BetCounter;
import com.linglong.lottery_backend.ticket.bean.sport.common.BetOptionConstant;
import com.linglong.lottery_backend.ticket.bean.welfare.FcSsqGame;
import com.linglong.lottery_backend.ticket.cache.GamePlatformCache;
import com.linglong.lottery_backend.ticket.cache.WeightCache;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.constant.ResultConstant;
import com.linglong.lottery_backend.ticket.constant.WordConstant;
import com.linglong.lottery_backend.ticket.entity.GamePlatform;
import com.linglong.lottery_backend.ticket.entity.Weight;
import com.linglong.lottery_backend.ticket.entity.bo.GameBean;
import com.linglong.lottery_backend.ticket.entity.bo.TicketNumber;
import com.linglong.lottery_backend.ticket.service.SplitService;
import com.linglong.lottery_backend.ticket.service.TblBettingTicketService;
import com.linglong.lottery_backend.utils.DateUtil;
import com.linglong.lottery_backend.utils.IdWorker;
import com.linglong.redisson.configure.annotations.LockAction;
import com.linglong.redisson.configure.util.LockType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ynght on 2019-04-20
 */
@Service
@Slf4j
public class SplitServiceImpl implements SplitService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JpaBatch jpaBatch;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TblBettingTicketService tblBettingTicketService;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderDetailsInfoRepository orderDetailsInfoRepository;

    @Autowired
    private GamePlatformCache gamePlatformCache;

    @Autowired
    private WeightCache weightCache;

    public static final BigDecimal PRICE_PER_STAKE = new BigDecimal(2);// 单注价格

    public static final BigDecimal PRICE_PER_TICKET = new BigDecimal(20000);// 单注价格

    @Override
    @LockAction(key = LockKey.lockOrder, value = "#orderId", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public int splitOrder(Long orderId, String userId, Timestamp deadline) {
        Order order = orderRepository.findByOrderIdAndUserId(orderId, userId);
        try {
            boolean flag = tblBettingTicketService.existsByOrderId(orderId);
            if (flag){return ResultConstant.ERROR_REPEAT; }
            OrderDetailsInfo orderDetailsInfo = orderDetailsInfoRepository.findByOrderId(orderId);
            Integer payStatus = orderDetailsInfo.getPayStatus();
            if (!Objects.equals(payStatus, Order.PAY_STATUS_PAID)) {
                return ResultConstant.ERROR_REPEAT;
            }
            Integer billStatus = orderDetailsInfo.getBillStatus();
            if (!Objects.equals(billStatus, Order.BILL_STATUS_INIT)) {
                return ResultConstant.ERROR_REPEAT;
            }
            AbstractGame gi = GameFactory.getInstance().getGameBean(order.getGameType());
            List<Weight> platforms = choosePlatforms(gi, order, deadline);
            if (platforms.size() == 0) {
                log.error("no platformIds can split order." + order);
                return ResultConstant.ERROR_PLATFORM;
            }
            List<TicketNumber> tnList = getTicketNumberList(order);
            if (tnList == null || tnList.isEmpty()) {
                log.error("[SPLIT] : 订单号没有对应的用户投注号码:" + order);
                throw new BusinessException("[SPLIT] : 订单号没有对应的用户投注号码:" + order);
            }
            gi.doSplitOrder(order, tnList, platforms);
        }catch (Exception e){
            log.error("SplitServiceImpl ",e);
            order.setOrderStatus(-1);
            orderRepository.saveAndFlush(order);
            return ResultConstant.ERROR_PLATFORM;
        }

        return ResultConstant.SUCCESS;
    }

    private List<Weight> choosePlatforms(AbstractGame gi, Order order, Timestamp deadline) {
        Timestamp now = DateUtil.getCurrentTimestamp();
        List<Weight> platforms = new ArrayList<>();
        List<Weight> allWeights = weightCache.getWeightList(order.getGameType());
        for (Weight weight : allWeights) {
            if (weight.getWeight().intValue() <= 0) {
                continue;
            }
            GamePlatform gamePlatform = gamePlatformCache.getGamePlatform(order.getGameType(), weight.getPlatformId());
            platforms.add(weight);
            log.info("GamePlatform：{}",gamePlatform);
//            if (now.before(DateUtil.getIntervalSeconds(deadline, -1 * gamePlatform.getBetTimeOffset()))) {
//
//            }
        }
        return platforms;
    }

    public List<TicketNumber> getTicketNumberList(Order order) {
        AbstractGame gi = GameFactory.getInstance().getGameBean(order.getGameType());
        List<GameBean> gbList = new LinkedList<>();
        //log.info("getTicketNumberList: {}", JSON.toJSONString(gbList));

        //福彩双色球
        if (gi instanceof AbstractWelfareGame) {
            //System.out.println("234");
            AbstractWelfareGame awg = (AbstractWelfareGame) gi;
            gbList.addAll(assemblyWelfareGameBean(awg, order));
        }

        //高频彩 陕11选5
        if (gi instanceof AbstractHighWelfareGame) {
            AbstractHighWelfareGame ahwg = (AbstractHighWelfareGame) gi;
            gbList.addAll(ahwg.assemblyHighWelfareGameBean(ahwg, order));
        }

        //大乐透
        if (gi instanceof AbstractBigHappyGame) {
            AbstractBigHappyGame ahwg = (AbstractBigHappyGame) gi;
            gbList.addAll(ahwg.assemblyBigHappyGameBean(ahwg, order));
        }

        //排列3
        if (gi instanceof AbstractPl3Game) {
            AbstractPl3Game ahwg = (AbstractPl3Game) gi;
            gbList.addAll(ahwg.assemblyPl3GameBean(ahwg, order));
        }
        //排列五
        if (gi instanceof AbstractPl5Game) {
            AbstractPl5Game ahwg = (AbstractPl5Game) gi;
            gbList.addAll(ahwg.assemblyGameBean(ahwg, order));
        }
        /*
//        String number = "20190414006:1:0 20190414007:1:0 20190414009:10003:0 20190414010:10001:0 20190414011:10001:0 " +
//                "20190414012:10001:0 20190414013:10000:0 20190414014:1:0";
        String number = "20190414007:3:0 20190414016:1033:0 20190414024:3:0";
        GameBean gb = new GameBean();
        gb.setLotteryNumber(number);
        gb.setExtra("3_1");
        gb.setBetNumber(1);//注数
        gb.setPlayType(WordConstant.SINGLE);
        gb.setPrice(new BigDecimal("2").multiply(new BigDecimal(9)));
        gbList.add(gb);
        AbstractSportGame gi = new JczqMixPGame();
        */
        //竞猜足球篮球
        if (gi instanceof AbstractSportGame) {
            //竞彩拆单，计算本地投注号码分布
            AbstractSportGame asg = (AbstractSportGame) gi;
            Map<String, BetCounter> betCountMap;
            List<GameBean> splitGameBean = new ArrayList<>();
            gbList = parseLotteryNumberByOrderDetails(order, gi);
            for (GameBean gameBean : gbList) {
                //此处的splitGameBean包含了所有的最碎的票，gbList里面的gameBean都包含了碎票的引用
                betCountMap = asg.split(gameBean, order.getBetTimes(), splitGameBean, asg.isSplitToOne());//拆单
                if (betCountMap == null) {
                    throw new BusinessException("投注格式有错误：" + gameBean.getLotteryNumber());
                }
            }
            gbList = splitGameBean;
        }

        List<GameBean> splitGameBeanList = gi.splitGameBeanList(gbList, new BigDecimal(order.getBetTimes()));

        List<TicketNumber> ticketNumberList = new ArrayList<>();
        for (GameBean gameBean : splitGameBeanList) {
            TicketNumber ticketNumber = new TicketNumber(gameBean.getLotteryNumber(), gameBean.getBetNumber(),
                    gameBean.getPlayType(), gameBean.getExtra(), gameBean.getBetTimes().intValue(),gameBean.getPeriod());
            //加入截期时间
            ticketNumberList.add(ticketNumber);
        }
        if (ticketNumberList.isEmpty()) {
            log.error("no lottery number record for orderId=" + order);
            throw new BusinessException("订单没有对应的投注号码");
        }
        return ticketNumberList;
    }

    private List<GameBean> assemblyWelfareGameBean(AbstractWelfareGame awg, Order order) {
        List<GameBean> result = new LinkedList<>();
        OrderDetailsInfo orderDetailsInfo = orderDetailsInfoRepository.findByOrderId(order.getOrderId());
        OrderDetails orderDetail = orderDetailsRepository.findByOrderId(order.getOrderId()).get(0);
        List<TwoColorBallDetails> details = JSONArray.parseArray(orderDetail.getPlaysDetails(), TwoColorBallDetails.class);
        order.setBetTimes(orderDetailsInfo.getMultiple().intValue());
        Map<String, List<TwoColorBallDetails>> group = details.stream().collect(Collectors.groupingBy(TwoColorBallDetails::getTwoColorBallType));
        group.forEach((k, v) -> {
            if (PlayType.TWOCOLORTYPEONE.getType().equals(k)) {
                if (v.size() > 5) {
                    List<List<TwoColorBallDetails>> ballDetails = splitList(v, 5);
                    for (int i = 0; i < ballDetails.size(); i++) {
                        if (ballDetails.get(i).isEmpty()) {
                            continue;
                        }
                        List<TwoColorBallDetails> list = ballDetails.get(i);
                        String lotteryNumber = Joiner.on(CommonConstant.SPACE_SPLIT_STR).join(list.stream().map(e -> Joiner.on(CommonConstant.COMMON_COLON_STR).join(e.getRedNumber(), e.getBlueNumber())).collect(Collectors.toList()));
                        GameBean gameBean = new GameBean(list.size(), WordConstant.SINGLE, Joiner.on(CommonConstant.PERCENT_SPLIT_STR).join(orderDetail.getIssue(),lotteryNumber), PRICE_PER_STAKE.multiply(BigDecimal.valueOf(list.size())), PlayType.TWOCOLORTYPEONE.getType(),  order.getEndTime(),Integer.valueOf(orderDetail.getIssue()));
                        result.add(gameBean);
                    }
                } else {
                    String lotteryNumber = Joiner.on(CommonConstant.SPACE_SPLIT_STR).join(v.stream().map(e -> Joiner.on(CommonConstant.COMMON_COLON_STR).join(e.getRedNumber(), e.getBlueNumber())).collect(Collectors.toList()));
                    GameBean gameBean = new GameBean(v.size(), WordConstant.SINGLE, Joiner.on(CommonConstant.PERCENT_SPLIT_STR).join(orderDetail.getIssue(),lotteryNumber), PRICE_PER_STAKE.multiply(BigDecimal.valueOf(v.size())), PlayType.TWOCOLORTYPEONE.getType(),  order.getEndTime(),Integer.valueOf(orderDetail.getIssue()));
                    result.add(gameBean);
                }
            } else if (PlayType.TWOCOLORTYPETWO.getType().equals(k)) {
                //复式
                v.forEach(e -> {
                    String lotteryNumber = Joiner.on(CommonConstant.COMMON_COLON_STR).join(e.getRedNumber(), e.getBlueNumber());
                    GameBean gameBean = new GameBean(Integer.valueOf(e.getBetNumber()), WordConstant.MULTIPLE, Joiner.on(CommonConstant.PERCENT_SPLIT_STR).join(orderDetail.getIssue(),lotteryNumber), PRICE_PER_STAKE.multiply(BigDecimal.valueOf(Integer.valueOf(e.getBetNumber()))), PlayType.TWOCOLORTYPETWO.getType(),  order.getEndTime(),Integer.valueOf(orderDetail.getIssue()));
                    result.add(gameBean);
                });

            } else if (PlayType.TWOCOLORTYPETHREE.getType().equals(k)) {
                //胆拖
                v.forEach(e->{
                    String[] allNum = e.getRedNumber().split(CommonConstant.SEMICOLON_SPLIT_STR);
                    String lotteryNumber = Joiner.on(CommonConstant.COMMON_COLON_STR).join(Joiner.on(CommonConstant.COMMON_AND_STR).join(allNum[0], allNum[1]), e.getBlueNumber());
                    GameBean gameBean = new GameBean(Integer.valueOf(e.getBetNumber()), WordConstant.DANTUO, Joiner.on(CommonConstant.PERCENT_SPLIT_STR).join(orderDetail.getIssue(),lotteryNumber), PRICE_PER_STAKE.multiply(BigDecimal.valueOf(Integer.valueOf(e.getBetNumber()))), PlayType.TWOCOLORTYPETHREE.getType(),  order.getEndTime(),Integer.valueOf(orderDetail.getIssue()));
                    result.add(gameBean);
                });
            }
        });
        //Collections.sort(result, (o1, o2) -> o1.getLotteryNumber().compareTo(o2.getLotteryNumber()));
        return result;
    }

//    public static String [] loop(String[] arr, Integer num) {
//        List<String> list = new ArrayList<String>();
//        Generator.combination(arr)
//                .simple(num)
//                .stream()
//                .forEach(data -> list.add(Joiner.on(CommonConstant.COMMA_SPLIT_STR).join(data)));
//        //System.out.println(JSONArray.toJSONString(list));
//        return list.toArray(new String[list.size()]);
//    }
//
//    public static List<String> loop(String[] redArr, String[] blueArr){
//        List<String> result = new ArrayList<>();
//        Generator.cartesianProduct(Arrays.asList(redArr), Arrays.asList(blueArr))
//                .stream()
//                .forEach(data-> result.add(Joiner.on(CommonConstant.COMMON_COLON_STR).join(data)));
//        //result.forEach(e->e.replace(", ",":"));
//        //System.out.println(JSON.toJSONString(result));
//        return result;
//    }

    private List<List<TwoColorBallDetails>> splitList(List<TwoColorBallDetails> all, int i) {
        //分割list
        List<List<TwoColorBallDetails>> list = Stream.iterate(0, n -> n + 1).limit(all.size()).parallel().map(a -> {
            List<TwoColorBallDetails> sendList = all.stream().skip(a * i).limit(i).parallel().collect(Collectors.toList());
            return sendList;
        }).collect(Collectors.toList());
        return list;
    }

    public List<GameBean> parseLotteryNumberByOrderDetails(Order order, AbstractGame gi) {
        List<GameBean> result = Lists.newArrayList();

        Set<String> numberSet = Sets.newLinkedHashSet();
        OrderDetailsInfo orderDetailsInfo = orderDetailsInfoRepository.findByOrderId(order.getOrderId());
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findByOrderId(order.getOrderId());
        orderDetailsList.forEach(details -> {

            JSONObject playsDetails = JSONObject.parseObject(details.getPlaysDetails());
            Map<String, Object> plays = JSONObject.parseObject(playsDetails.toJSONString(), HashMap.class);

            Set<String> optionSet = Sets.newLinkedHashSet();
            for (String key : plays.keySet()) {
                Map<String, String> map = BetOptionConstant.SYSTEM_PLAY_TYPE_MAPPING_TICKET_BET_OPTION.get(key);
                JSONObject jsonObject = JSONObject.parseObject(String.valueOf(plays.get(key)));
                JSONArray items = jsonObject.getJSONArray("items");

                for (int j = 0; j < items.size(); j++) {
                    JSONObject singleItem = items.getJSONObject(j);
                    String item = singleItem.getString("item");
                    String option = map.get(item);
                    optionSet.add(option);
                }
            }

            String tempNumber = Joiner.on(CommonConstant.COMMON_COLON_STR).join(details.getIssue(), Joiner.on(CommonConstant.POUND_SPLIT_STR).join(optionSet), "0");//0代表不是胆拖,竞彩都传0即可
            log.info("parseLotteryNumberByOrderDetails: {}", tempNumber);
            numberSet.add(tempNumber);
        });

        String lotteryNumber = Joiner.on(CommonConstant.SPACE_SPLIT_STR).join(numberSet);
        Integer multiple = orderDetailsInfo.getMultiple();
        order.setBetTimes(multiple);
        String chuanGuan = orderDetailsInfo.getChuanGuan();
        Integer betNumber = orderDetailsInfo.getBetNumber();
        GameBean gameBean = new GameBean();
        gameBean.setLotteryNumber(lotteryNumber);
        chuanGuan = chuanGuan.replaceAll(CommonConstant.COMMA_SPLIT_STR, "_1|") + "_1";
        gameBean.setExtra(chuanGuan);
        gameBean.setBetNumber(betNumber);//注数
        gameBean.setPrice(gi.getPrice(gameBean));
        gameBean.setEndTime(order.getEndTime());
        result.add(gameBean);
        return result;
    }

    /**
     * 双色球复式和胆拖排列组合
     * 胆拖 2019061%03,17&11,16,24,26,27,28:04,11@30
     * 复式 2019061%03,11,16,24,26,27,28:04,11@30
     * @param lotteryNumber
     * @param matchSn
     * @param ticketDto
     * @return 胆拖号码
     */
    @Override
    public void sqlitMuDandcolorBall(String lotteryNumber, String matchSn, TicketDto ticketDto) {
        int isDantuo = lotteryNumber.indexOf("&");
        int goupCount = 0;
        String[] ball;
        String dantuo = null;
        if(isDantuo < 0) {
            ball = lotteryNumber.split(CommonConstant.COMMON_COLON_STR);

        }else {
            String[] ballandDantuo = lotteryNumber.split(CommonConstant.COMMON_AND_STR);
            ball = ballandDantuo[1].split(CommonConstant.COMMON_COLON_STR);
            goupCount = ballandDantuo[0].split(CommonConstant.COMMA_SPLIT_STR).length;
            dantuo = ballandDantuo[0];
        }

        String[] redGroup = FcSsqGame.loop(ball[0].split(CommonConstant.COMMA_SPLIT_STR),6-goupCount);
        if(dantuo != null) {
            for (int i = 0; i < redGroup.length; i++) {
                redGroup[i] = dantuo + "," + redGroup[i];
//                Arrays.sort(redGroup[i].split(CommonConstant.COMMA_SPLIT_STR));
            }
        }

        List<String> unitsGroups = FcSsqGame.loop(redGroup, ball[1].split(CommonConstant.COMMA_SPLIT_STR));
        List<BettingTicketDigital> ticketDigitals = new ArrayList<>();
        for (int i = 0; i < unitsGroups.size(); i++) {
            String unitsGroup = unitsGroups.get(i);

            BettingTicketDigital bettingTicketDigital
                    = new BettingTicketDigital(ticketDto.getOrderId(), idWorker.nextId(), ticketDto.getTicketId(),matchSn,
                    3,ticketDto.getPlayType(), unitsGroup, 0, 0, BigDecimal.ZERO, BigDecimal.ZERO,
                    ticketDto.getTimes(), null, null, null, 1L);
            ticketDigitals.add(bettingTicketDigital);
        }

        if(ticketDigitals.isEmpty())
            return;
        jpaBatch.batchInsert(ticketDigitals);
    }



    public static void main(String[] args) {
//        String details = "{\"bet_number\":11,\"bill_status\":0,\"bonus\":0,\"chuan_guan\":\"2,3,4\"," +
//                "\"deadline\":\"2019年04月25日 12点00分\",\"deadline_match\":\"布里斯班狮吼VS阿德莱德联\"," +
//                "\"delivery_prize_status\":0,\"hit_prize_status\":0,\"matchs\":[{\"match_id\":20190425001," +
//                "\"match_name\":\"西雅图海湾人,圣何塞地震\",\"match_sn\":\"周三037\"," +
//                "\"plays\":{\"spf\":{\"items\":[{\"item\":\"0\",\"odds\":\"6.80\",\"result\":\"--\"}]}}}," +
//                "{\"match_id\":20190425003,\"match_name\":\"布里斯班狮吼,阿德莱德联\",\"match_sn\":\"周四001\"," +
//                "\"plays\":{\"rqspf\":{\"handicap\":\"+1\",\"items\":[{\"item\":\"1\",\"odds\":\"3.65\",\"result\":\"--\"}]}}},{\"match_id\":20190425004,\"match_name\":\"海牙,SBV精英\",\"match_sn\":\"周四002\",\"plays\":{\"rqspf\":{\"handicap\":\"-1\",\"items\":[{\"item\":\"1\",\"odds\":\"3.85\",\"result\":\"--\"}]}}},{\"match_id\":20190425005,\"match_name\":\"佐加顿斯,北雪平\",\"match_sn\":\"周四003\",\"plays\":{\"spf\":{\"items\":[{\"item\":\"1\",\"odds\":\"3.25\",\"result\":\"--\"}]}}}],\"multiple\":11,\"open_prize_status\":0,\"order_id\":1121234997529939968,\"pay_status\":0}";
//        Order order = new Order();
//        order.setOrderDetails(details);
//        SplitServiceImpl splitService = new SplitServiceImpl();
//        List<TicketNumber> ticketNumberList = splitService.getTicketNumberList(order);
//        JczqMixPGame gi = new JczqMixPGame();
//        gi.splitOrder(order, Lists.newArrayList(), ticketNumberList);

    }
}
