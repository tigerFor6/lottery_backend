package com.linglong.lottery_backend.ticket.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.linglong.lottery_backend.common.error.BusinessException;
import com.linglong.lottery_backend.order.entity.OrderDetailsInfo;
import com.linglong.lottery_backend.order.repository.OrderDetailsInfoRepository;
import com.linglong.lottery_backend.ticket.cache.GameCache;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.constant.WordConstant;
import com.linglong.lottery_backend.order.entity.Order;
import com.linglong.lottery_backend.order.repository.OrderRepository;
import com.linglong.lottery_backend.ticket.enums.AoliEnum;
import com.linglong.lottery_backend.ticket.repository.TblBettingTicketRepository;
import com.linglong.lottery_backend.ticket.bean.sport.common.BetOptionConstant;
import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.entity.Weight;
import com.linglong.lottery_backend.ticket.entity.bo.GameBean;
import com.linglong.lottery_backend.ticket.entity.bo.TicketNumber;
import com.linglong.lottery_backend.utils.CommonUtil;
import com.linglong.lottery_backend.utils.DateUtil;
import com.linglong.lottery_backend.utils.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by ynght on 2019-04-20
 */
@Slf4j
public abstract class AbstractGame {
    public static final BigDecimal PRICE_PER_STAKE = new BigDecimal(2);// 单注价格
    public static final BigDecimal PRICE_MAX_PER_TICKET = new BigDecimal(20000);

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TblBettingTicketRepository tblBettingTicketRepository;
    @Autowired
    private OrderDetailsInfoRepository orderDetailsInfoRepository;

    @Transactional
    public void doSplitOrder(Order order, List<TicketNumber> ticketNumberList, List<Weight>
            platforms) {
        // TODO: 2019-04-25 加锁
        OrderDetailsInfo orderDetailsInfo = orderDetailsInfoRepository.findByOrderId(order.getOrderId());
        Integer payStatus = orderDetailsInfo.getPayStatus();
        if (Objects.equals(payStatus, Order.PAY_STATUS_NO_PAY) || Objects.equals(order.getOrderStatus(),
                Order.ORDER_STATUS_CLOSE) || Objects.equals(order.getOrderStatus(), Order.ORDER_STATUS_FAILED)) {
            throw new BusinessException("订单号在彩票系统拆单时支付状态不正确:" + order + "状态:" + payStatus);
        } else if (Objects.equals(payStatus, Order.PAY_STATUS_REFUNDING) || Objects.equals(payStatus,
                Order.PAY_STATUS_REFUND) || Objects.equals(order.getOrderStatus(), Order.ORDER_STATUS_FINISHED)) {
            log.info("订单号在彩票系统拆单直接返回：" + order);
            return;
        }
        Game game = GameCache.getGame(order.getGameType());
        if (game == null) {
            throw new BusinessException("游戏不存在:" + order.getGameType());
        }
        splitOrder(order, platforms, ticketNumberList);
        return;
    }

    public abstract Game getGame();

    public List<GameBean> splitGameBeanList(List<GameBean> userGameBeanList, BigDecimal betTimes) {
        userGameBeanList = splitGameBeanFormat(userGameBeanList);
        userGameBeanList = mergeGameBeanList(userGameBeanList);
        List<GameBean> gameBeanList = splitGameBeanListForMaxBetTimes(userGameBeanList, betTimes);
        gameBeanList = splitGameBeanListForAmount(gameBeanList);
        return gameBeanList;
    }

    public List<GameBean> splitGameBeanFormat(List<GameBean> userGameBeanList) {
        return userGameBeanList;
    }

    public BigDecimal getPrice(TicketNumber ticketNumber) {
        BigDecimal betMutilDecimal = new BigDecimal(ticketNumber.getBetNumber() * ticketNumber.getTimes());
        return ticketNumber.getExtra().equals("1") ? betMutilDecimal.multiply(new BigDecimal(3)) : betMutilDecimal.multiply(PRICE_PER_STAKE);
    }

    /**
     * 合并同样的投注号码
     **/
    public List<GameBean> mergeGameBeanList(List<GameBean> gameBeanList) {
        Map<String, GameBean> beanMap = new HashMap<>();
        for (GameBean gb : gameBeanList) {
            String key = Joiner.on(CommonConstant.COMMON_VERTICAL_STR).join(gb.getLotteryNumber(), gb.getPlayType(),
                    gb.getExtra(), gb.getBetNumber());
            GameBean mapGameBean = beanMap.get(key);
            if (mapGameBean == null) {
                beanMap.put(key, gb);
            } else {
                mapGameBean.setBetTimes(mapGameBean.getBetTimes().add(gb.getBetTimes()));
            }
        }
        return new ArrayList<>(beanMap.values());
    }

    public BigDecimal getPrice(GameBean gameBean) {
        return PRICE_PER_STAKE.multiply(new BigDecimal(gameBean.getBetNumber()));
    }

    /**
     * 为满足用户投注倍数不受限制的功能，根据用户投注的倍数对投注号码进行拆分
     *
     * @param userGameBeanList
     * @param betTimes
     * @return
     */
    public List<GameBean> splitGameBeanListForMaxBetTimes(List<GameBean> userGameBeanList, BigDecimal betTimes) {
        List<GameBean> splitGameBeanList = new ArrayList<>();
        for (GameBean gb : userGameBeanList) {
            int theBetTimes = betTimes.intValue() * gb.getBetTimes().intValue();//100
            BigDecimal oneTimePrice = getPrice(gb);
            int currentMaxBetTimes = 1;
            // currentMaxBetTimes最小值为1，如果用户选择就是1，下面判断不做
            if (theBetTimes > 1) {
                // 小于单笔2w元上限的最多倍数
                currentMaxBetTimes = PRICE_MAX_PER_TICKET.divideToIntegralValue(oneTimePrice).intValue();
                // 可能单笔即大于2w元，这种情况下要设置为1，后面有拆号码逻辑
                if (currentMaxBetTimes < 1) {
                    currentMaxBetTimes = 1;
                }
            }
            // 这里解析出需要拆分的倍数(key)以及对应的gameBean个数(value)
            Map<Integer, Integer> parseScheme = parseToSplitTimesPeer(theBetTimes, currentMaxBetTimes);
            for (Map.Entry<Integer, Integer> m : parseScheme.entrySet()) {
                GameBean gameBean = gb.cloneGameBean();
                gameBean.setBetTimes(new BigDecimal(m.getKey()));
                // 这里存的数值是每一倍的价格
                gameBean.setPrice(oneTimePrice);
                for (int i = 0; i < m.getValue(); i++) {
                    // 这里也不需要反复clone，使用同一个实例即可，因为没有id之类的字段，所有字段确实都是一样的
                    splitGameBeanList.add(gameBean);
                }
            }
        }
        return splitGameBeanList;
    }

    /**
     * 金额>2w的订单，或者胆+拖数目超限的，要拆分
     */
    protected List<GameBean> splitGameBeanListForAmount(List<GameBean> userGameBeanList) {
        List<GameBean> gameBeanList = new ArrayList<>();
        for (GameBean gameBean : userGameBeanList) {
            BigDecimal oneBetPrice = gameBean.getPrice();//一倍订单价格
            boolean ifExceedDantuoRedBallLimits = ifExceedDantuoRedBallLimits(gameBean); // 红球胆+拖数目是否超限
            if (oneBetPrice.compareTo(PRICE_MAX_PER_TICKET) >= 0 || ifExceedDantuoRedBallLimits) {
                //单倍价格大于2w，或者胆拖数目超限，需走拆方案流程
                gameBeanList.addAll(splitGameBeanListForAmountByScheme(gameBean));
            } else if ((oneBetPrice.multiply(gameBean.getBetTimes())).compareTo(PRICE_MAX_PER_TICKET) >= 0)//拆倍数流程
            {
                gameBeanList.addAll(splitGameBeanListForAmountByTimes(gameBean));
            } else {
                gameBeanList.add(gameBean);
            }
        }
        return gameBeanList;
    }

    /**
     * 是否超过胆拖数目限制
     */
    protected boolean ifExceedDantuoRedBallLimits(GameBean gameBean) {
        return false;
    }

    /**
     * 对于单倍订单大于2w的--拆分方案
     */
    protected List<GameBean> splitGameBeanListForAmountByScheme(GameBean userGameBean) {
        List<GameBean> gameBeanList = new ArrayList<>();
        gameBeanList.add(userGameBean);
        return gameBeanList;
    }

    /**
     * 对于单倍订单小于2w的--拆分倍数
     *
     * @param userGameBean
     * @return
     */
    protected List<GameBean> splitGameBeanListForAmountByTimes(GameBean userGameBean) {
        List<GameBean> gameBeanList = new ArrayList<>();
        BigDecimal oneBetPrice = userGameBean.getPrice(); // 1倍订单价格
        BigDecimal maxBetTimes = PRICE_MAX_PER_TICKET.divideToIntegralValue(oneBetPrice); // 拆分后一单最大倍数
        BigDecimal betTimes = userGameBean.getBetTimes(); // 该单总倍数
        while (betTimes.compareTo(BigDecimal.ZERO) > 0) {
            //当前拆分的投注号码，需要标示的倍数
            BigDecimal currentBetTimes = betTimes.compareTo(maxBetTimes) >= 0 ? maxBetTimes : betTimes;
            betTimes = betTimes.subtract(currentBetTimes);
            GameBean gameBean = (GameBean) userGameBean.clone();
            gameBean.setBetTimes(currentBetTimes);
            gameBeanList.add(gameBean);
        }
        return gameBeanList;
    }

    /**
     * @param theBetTimes        原始投注倍数
     * @param currentMaxBetTimes 最多单笔投注倍数
     * @return
     */
    private Map<Integer, Integer> parseToSplitTimesPeer(int theBetTimes, int currentMaxBetTimes) {
        Map<Integer, Integer> result = new HashMap<>();
        //100   909
        int max = Math.min(getGame().getTicketBetTimes(), currentMaxBetTimes);
        int maxBetTimesSize = theBetTimes / max;
        int leftOneBetTimes = theBetTimes % max;
        if (maxBetTimesSize > 0) {
            result.put(max, maxBetTimesSize);
        }
        if (leftOneBetTimes > 0) {
            result.put(leftOneBetTimes, 1);
        }
        return result;
    }

    public void splitOrder(Order order, List<Weight> platforms, List<TicketNumber> ticketNumberList) {
        // 1.获得tb_lottery_number表的用户投注号码记录
        List<BettingTicket> ticketList = new ArrayList<>();
        if (ticketNumberList == null || ticketNumberList.isEmpty()) {
            log.error("订单号没有对应的用户投注号码:" + order);
            throw new BusinessException("订单号没有对应的用户投注号码:" + order);
        }
        // 2.分类装（playType+extra）到map里
        Map<String, List<TicketNumber>> ticketMap = new HashMap<>();
        for (TicketNumber tn : ticketNumberList) {
            String playType = tn.getPlayType();
            if (StringUtils.isBlank(playType)) {
                log.error("订单号号码没有对应的玩法字段:" + tn);
                throw new BusinessException("订单号号码没有对应的玩法字段:" + tn);
            }
            String tktType = playType;
            if (StringUtils.isNotBlank(tn.getExtra())) {
                tktType = new StringBuffer(playType).append(CommonConstant.COMMON_WAVE_STR).append(tn.getExtra())
                        .toString();
            }
            if (!ticketMap.containsKey(tktType)) {
                List<TicketNumber> lst = new ArrayList<>();
                ticketMap.put(tktType, lst);
            }
            ticketMap.get(tktType).add(tn);
        }
        // 3.开拆，
        BigDecimal orderAmount = BigDecimal.ZERO;
        for (Map.Entry<String, List<TicketNumber>> m : ticketMap.entrySet()) {
            List<TicketNumber> lst = m.getValue();
            //每票号数
            String[] playTypeExtra = m.getKey().split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant
                    .COMMON_WAVE_STR);
            String playType = playTypeExtra[0];
            String extra = playTypeExtra.length == 2 ? playTypeExtra[1] : null;
            int numberPerTicket = getLotteryNumberPerTicket(playType, extra);
            //进入到这个循环里面的，都是同一个玩法, 各个票的倍数不一致，不能放到一张票中, 同一个投注号码不能放到同一个票中
            Map<Integer, List<TicketNumber>> betTimeLotteryNumberMap = new HashMap<>();
            // 按照投注倍数将所有票拆分到不同的List中
            for (TicketNumber ln : lst) {
                Integer snBetTime = ln.getTimes();
                if (!betTimeLotteryNumberMap.containsKey(snBetTime)) {
                    List<TicketNumber> snList = new ArrayList<>();
                    betTimeLotteryNumberMap.put(snBetTime, snList);
                }
                betTimeLotteryNumberMap.get(snBetTime).add(ln);
            }

            for (Map.Entry<Integer, List<TicketNumber>> betTimeLottNumEntry : betTimeLotteryNumberMap.entrySet()) {
                List<TicketNumber> sameBetTimeNumbers = betTimeLottNumEntry.getValue();
                // 按照投注号码进行排序
                Collections.sort(sameBetTimeNumbers, (o1, o2) -> o1.getLotteryNumber().compareTo(o2
                        .getLotteryNumber()));
                // 逐个进行拆单，拆分票
                String lastLotteryNumber = null;
                List<TicketNumber> currentTicketNumberList = new ArrayList<>();
                for (TicketNumber tn : sameBetTimeNumbers) {
                    if (lastLotteryNumber != null && tn.getLotteryNumber().equals(lastLotteryNumber)) {
                        // 上一个号码和这一个投注号码一样，需要换一个ticket存储 这步包含存储lotteryNumber表
                        List<BettingTicket> subTicketList = new ArrayList<>();
                        splitTicket(order, currentTicketNumberList, numberPerTicket, platforms, subTicketList);
                        //orderAmount = orderAmount.add(ticketAmount);
                        currentTicketNumberList.clear();
                        ticketList.addAll(subTicketList);
                    }
                    lastLotteryNumber = tn.getLotteryNumber();
                    currentTicketNumberList.add(tn);
                }
                // 如果最后还有
                if (!currentTicketNumberList.isEmpty()) {
                    List<BettingTicket> subTicketList = new ArrayList<>();
                    splitTicket(order, currentTicketNumberList, numberPerTicket, platforms, subTicketList);
                    //orderAmount = orderAmount.add(ticketAmount);
                    ticketList.addAll(subTicketList);
                }
            }
        }
        for (int i = 0; i < ticketList.size(); i++) {
            orderAmount = orderAmount.add(ticketList.get(i).getTicketAmount());
        }
        orderAmount = orderAmount.multiply(new BigDecimal(100));
        if (order.getOrderFee().compareTo(orderAmount) != 0) {
            log.error("拆分后的票面总金额与订单金额不相符合，订单金额：" + order.getOrderFee() + ",票面金额：" + orderAmount + "orderID=" + order.getOrderId());
            throw new BusinessException("拆分后的票面总金额与订单金额不相符合!");

        }
        // 将用户订单状态置为”已拆分“，并填写拆分后的票数
        // 拆票成功后，默认successTickets的数量是0，以方便在后续的操作中，对数据库中已成功的票数做运算。
        OrderDetailsInfo orderDetailsInfo = orderDetailsInfoRepository.findByOrderId(order.getOrderId());
        orderDetailsInfo.setTotalTickets(ticketList.size());
        orderDetailsInfo.setSuccessTickets(0);
        orderDetailsInfo.setSplitStatus(Order.SPLIT_STATUS_SPLIT);
        orderDetailsInfoRepository.saveAndFlush(orderDetailsInfo);
    }

    public int getLotteryNumberPerTicket(String playType, String extra) {
        switch (playType) {
            case WordConstant.SINGLE:
                return 5;
            case WordConstant.DIRECT:
            case WordConstant.MULTIPLE:
            case WordConstant.DANTUO:
                return 1;
        }
        throw new BusinessException("playType is  error." + CommonUtil.mergeUnionKey(playType, extra));
    }

    //拆分成票
    private BigDecimal splitTicket(Order order, List<TicketNumber> currentTicketNumberList, int numberPerTicket,
                                   List<Weight> platforms, List<BettingTicket> ticketList) {
        int idx = 0;
        BigDecimal ticketAmount = BigDecimal.ZERO;
        Long ticketId = null;
        BigDecimal betAmount = BigDecimal.ZERO;
        BettingTicket lastInsertTicket = null;
        Timestamp now = DateUtil.getCurrentTimestamp();
        String lotteryNumber = null;
        // 用于计算出当前stakeOrder对应的LotteryStakeNumber
        log.info("currentTicketNumberList:{}", JSON.toJSONString(currentTicketNumberList));

        if(order.getGameType().equals(AoliEnum.SHX115.getCode()) ||
                order.getGameType().equals(AoliEnum.SSQ.getCode()) ||
                order.getGameType().equals(AoliEnum.PL3.getCode()) ||
                order.getGameType().equals(AoliEnum.DLT.getCode()) ||
                order.getGameType().equals(AoliEnum.PL5.getCode())) {
            numberPerTicket = 1;
        }

        for (TicketNumber tn : currentTicketNumberList) {
            int times = tn.getTimes();
            String playType = tn.getPlayType();
            String extra = tn.getExtra();
            if (idx % numberPerTicket == 0) {
                ticketId = idWorker.nextId();// 生成票表的ID
                Integer platformId = getRandomPlatformId(platforms);
                //int p = tn.getPeriod();
                BettingTicket ticket = new BettingTicket(ticketId, Long.valueOf(order.getUserId()), order.getOrderId(),
                        order.getGameType(), platformId, BettingTicket.TICKET_STATUS_INIT, times, playType, extra,
                        BettingTicket.PRIZE_STATUS_NO_OPEN, now,tn.getPeriod());
                // ticket是一开始创建的，但amount字段却是最后才确定的
                if (null != lastInsertTicket) {
                    lastInsertTicket.setLotteryNumber(lotteryNumber);
                    lastInsertTicket.setTicketAmount(betAmount);
                    ticketAmount = ticketAmount.add(betAmount);
                }
                lastInsertTicket = ticket;
                ticketList.add(ticket);
                betAmount = BigDecimal.ZERO;
                lotteryNumber = null;
            }
            betAmount = betAmount.add(getPrice(tn));
            lotteryNumber = (StringUtils.isBlank(lotteryNumber) ? "" : lotteryNumber + CommonConstant.COMMON_DOLLAR_STR)
                    + tn.getLotteryNumber() + CommonConstant.COMMON_AT_STR + tn.getBetNumber();
            idx++;
        }
        if (null != lastInsertTicket) {
            lastInsertTicket.setLotteryNumber(lotteryNumber);
            lastInsertTicket.setTicketAmount(betAmount);
            ticketAmount = ticketAmount.add(betAmount);
        }
        Game game = GameCache.getGame(order.getGameType());
        if (Objects.equals(Integer.valueOf(1), game.getType())) {
            parseNumberToSystem(ticketList, game);
        }

        // TODO: 2019-04-20  保存ticket
//        ticketService.insertBatch(ticketList);
        tblBettingTicketRepository.saveAll(ticketList);
        return ticketAmount;
    }

    public void parseNumberToSystem(List<BettingTicket> ticketList, Game game) {
        String number = null;
        String[] split = null;
        String[] allMatches = null;
        Map<String, String> codeMappingExtra = null;
        Map<String, String> codeMap = null;
        if (game.getGameEn().startsWith("jclq")) {
            codeMappingExtra = BetOptionConstant.JCLQ_TICKET_OPTION_MAPPING_SYSTEM_PLAY_TYPE;
            codeMap = BetOptionConstant.SYSTEM_JCLQ_BET_CODE_MAPPING_MAP;
        } else {
            codeMappingExtra = BetOptionConstant.JCZQ_TICKET_OPTION_MAPPING_SYSTEM_PLAY_TYPE;
            codeMap = BetOptionConstant.SYSTEM_JCZQ_BET_CODE_MAPPING_MAP;
        }
        for (BettingTicket ticket : ticketList) {
            StringBuffer buffer = new StringBuffer();
            number = ticket.getLotteryNumber();
            split = number.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_AT_STR);
            allMatches = split[0].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.SPACE_SPLIT_STR);
            for (String single : allMatches) {
                String[] optionsAndIssue = single.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_COLON_STR);
                String[] strings = optionsAndIssue[1].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.POUND_SPLIT_STR);
                buffer.append(optionsAndIssue[0]).append(CommonConstant.COMMON_COLON_STR);
                if (Objects.equals(WordConstant.MULTIPLE, ticket.getPlayType())) {
                    String extra = null;
                    StringBuffer codeNumber = new StringBuffer();
                    for (String s : strings) {
                        extra = codeMappingExtra.get(s);
                        codeNumber.append(codeMap.get(s)).append(CommonConstant.POUND_SPLIT_STR);
                    }
                    codeNumber = codeNumber.deleteCharAt(codeNumber.length() - 1);
                    buffer.append(extra).append(CommonConstant.COMMON_WAVE_STR).append(codeNumber.toString());
                } else {
                    for (String s : strings) {
                        buffer.append(codeMappingExtra.get(s)).append(CommonConstant.COMMON_WAVE_STR).
                                append(codeMap.get(s)).append(CommonConstant.POUND_SPLIT_STR);
                    }
                    buffer = buffer.deleteCharAt(buffer.length() - 1);
                }
                buffer.append(CommonConstant.SPACE_SPLIT_STR);
            }
            buffer = buffer.deleteCharAt(buffer.length() - 1);
            buffer.append(CommonConstant.COMMON_AT_STR).append(split[1]);
            ticket.setLotteryNumber(buffer.toString());
        }
    }

    public String getDisplayNumber(String lotteryNumber, String extra) {
        return lotteryNumber;
    }

    public static Integer getRandomPlatformId(List<Weight> weightList) {
        int size = weightList.size();
        if (1 == size) {
            return weightList.get(0).getPlatformId();
        } else {
            int totalWeight = 0;
            for (Weight w : weightList) {
                totalWeight = totalWeight + w.getWeight();
            }
            Integer platformId = null;
            // 随机选择
            Collections.sort(weightList, (o1, o2) -> o1.getWeight() - o2.getWeight());
            int weight = 0;
            int random = new Random().nextInt(totalWeight);
            for (int i = 0; i < size; i++) {
                weight += weightList.get(i).getWeight();
                if (random < weight) {
                    platformId = weightList.get(i).getPlatformId();
                    break;
                }
            }
            if (platformId == null) {
                platformId = weightList.get(size - 1).getPlatformId();
            }
            return platformId;
        }
    }
}
