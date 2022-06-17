package com.linglong.lottery_backend.ticket.impl;

import com.alibaba.fastjson.JSONObject;
import com.linglong.lottery_backend.lottery.match.entity.TblSaleDate;
import com.linglong.lottery_backend.lottery.match.repository.TblSaleDateRepository;
import com.linglong.lottery_backend.lottery.match.cache.TblSaleDateCache;
import com.linglong.lottery_backend.order.entity.Order;
import com.linglong.lottery_backend.order.entity.OrderDetailsInfo;
import com.linglong.lottery_backend.order.repository.OrderDetailsInfoRepository;
import com.linglong.lottery_backend.order.repository.OrderRepository;
import com.linglong.lottery_backend.ticket.bean.BetResult;
import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.cache.GameCache;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.constant.GameConstant;
import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import com.linglong.lottery_backend.ticket.entity.bo.TicketNumber;
import com.linglong.lottery_backend.ticket.remote.bjgc.entity.part.Ticket;
import com.linglong.lottery_backend.ticket.repository.TblBettingTicketRepository;
import com.linglong.lottery_backend.ticket.service.LotteryBetService;
import com.linglong.lottery_backend.ticket.service.cron.SendOrderCoordinator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by ynght on 2019-04-26
 */
@Service
@Slf4j
public class LotteryBetServiceImpl implements LotteryBetService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TblBettingTicketRepository tblBettingTicketRepository;
    @Autowired
    private SendOrderCoordinator sendOrderCoordinator;
    @Autowired
    private OrderDetailsInfoRepository orderDetailsInfoRepository;
    @Autowired
    private TblSaleDateCache tblSaleDateCache;

    private static final Logger logger = LoggerFactory.getLogger(LotteryBetServiceImpl.class);

    @Override
    public void doBet(Long orderId) {
        logger.info("doBet start---------"+orderId);
        Order order = orderRepository.findByOrderId(orderId);
        if (order.getGameType().equals(GameCache.getGame(GameConstant.FC_SSQ).getGameType()) ||
                order.getGameType().equals(GameCache.getGame(GameConstant.DLT).getGameType()) ){

            int weekDay = LocalDate.now().getDayOfWeek().getValue();
            TblSaleDate saleDate = tblSaleDateCache.getTblSaleDate(order.getGameType(),0);
            String saleTime = saleDate.getSaleTime();
            String[] saleArr = saleTime.split(CommonConstant.COMMON_AT_STR);
            String[] dayArr = saleArr[0].split(CommonConstant.COMMON_VERTICAL_STR);
            Set<String> daySet = new HashSet<>(Arrays.asList(dayArr));
            if (daySet.contains(String.valueOf(weekDay))){
                LocalTime now = LocalTime.now();
                String[] time = saleArr[1].split(CommonConstant.SPACE_SPLIT_STR);
                String startTime = time[0];
                String endTime = time[1];
                LocalTime s = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
                LocalTime e = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
                if (now.isAfter(s) && now.isBefore(e)){
                    log.info("不符合出票时间---->"+orderId);
                    return;
                }
            }
        }else if(order.getGameType().equals(GameCache.getGame(GameConstant.SHX115).getGameType())) {
            return ;

        }else if(order.getGameType().equals(GameCache.getGame(GameConstant.PL3).getGameType()) ||
                order.getGameType().equals(GameCache.getGame(GameConstant.PL5).getGameType())) {
            TblSaleDate saleDate = tblSaleDateCache.getTblSaleDate(order.getGameType(),0);
            String saleTime = saleDate.getSaleTime();
            String[] saleArr = saleTime.split(CommonConstant.COMMON_AT_STR);
            String[] time = saleArr[1].split(CommonConstant.SPACE_SPLIT_STR);
            String startTime = time[0];
            String endTime = time[1];
            LocalTime s = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
            LocalTime e = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
            LocalTime now = LocalTime.now();
            if (now.isAfter(s) && now.isBefore(e)){
                log.info("PL3、PL5 不符合出票时间("+startTime+" -> "+endTime+") orderId("+orderId+")");
                return;
            }

        }
//        else if(order.getGameType().equals(GameCache.getGame(GameConstant.JCZQ_MIX_P).getGameType()) ||
//                order.getGameType().equals(GameCache.getGame(GameConstant.JCLQ_MIX_P).getGameType())) {
//            JSONArray saleTime = JSONObject.parseObject(saleDate.getSaleTime()).getJSONArray("tblSaleTimes");
//            JSONObject saleDateByWeekDay = saleTime.getJSONObject(weekDay);
//
//            String startTime = saleDateByWeekDay.getString("startTime");
//            String[] startTimes = startTime.split(CommonConstant.COMMON_COLON_STR);
//
//            String endTime = saleDateByWeekDay.getString("endTime");
//            String[] endTimes = endTime.split(CommonConstant.COMMON_COLON_STR);
//
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(nowDate);
//            int hour = calendar.get(Calendar.HOUR_OF_DAY);
//            int min = calendar.get(Calendar.MINUTE);
//            int second = calendar.get(Calendar.SECOND);
//
//            if((hour < Integer.valueOf(startTimes[0]).intValue() && min < Integer.valueOf(startTimes[1]).intValue() && second < Integer.valueOf(startTimes[2]).intValue())
//            || (hour >= Integer.valueOf(endTimes[0]).intValue() && min >= Integer.valueOf(endTimes[1]).intValue() && second >= Integer.valueOf(endTimes[2]).intValue())) {
//                log.info("不符合出票时间---->"+orderId);
//                return;
//            }
//
//        }
        OrderDetailsInfo orderDetailsInfo = orderDetailsInfoRepository.findByOrderId(orderId);
        Integer splitStatus = orderDetailsInfo.getSplitStatus();
        if (Objects.equals(splitStatus, Order.SPLIT_STATUS_SPLIT)) {
            Integer billStatus = orderDetailsInfo.getBillStatus();
            if (Objects.equals(billStatus, Order.BILL_STATUS_INIT)) {
                List<BettingTicket> tickets = tblBettingTicketRepository.findTicketsByOrderId(orderId);
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
                    //拦截校验
                    sendOrderCoordinator.addToBet(order.getGameType(), entry.getKey(), orderId, entry.getValue());
                }
            }
        }
        logger.info("doBet end---------");
    }

    @Override
    public void sendTicket2Prize(Long orderId) {

    }

    @Override
    public void doBet(Integer gameType, Integer platformId, Long orderId, List<Long> ticketIds) {
        List<TicketDto> ticketDtoList = new ArrayList<>();
        for (Long ticketId : ticketIds) {
            BettingTicket ticket = tblBettingTicketRepository.findTicketsByTicketId(ticketId);
            List<TicketNumber> nums = ticket.getTicketNumbers();
            TicketDto ticketDto = new TicketDto(ticket.getTicketId(), ticket.getTicketAmount(), nums, ticket
                    .getPlayType(), ticket.getExtra(), ticket.getTimes(),ticket.getGameType(),ticket.getOrderId(), ticket.getCreateTime());
            ticketDtoList.add(ticketDto);
        }
        sendOrderCoordinator.addToBet(gameType, platformId, orderId, ticketDtoList);

    }

    @Override
    @Transactional
    public List<Runnable> updateSuccessTickets(Long orderId, Integer platformId, List<BetResult> results) {
        List<Runnable> resultList = new ArrayList<>();
        return resultList;
    }

    @Override
    public void sendTicket2Prize(List<Ticket> tickets) {

    }

//    private String getSuccessJCTicketLotteryNumberWithSp(String lotteryNumber,
//                                                         Map<String, Map<String, Map<String, String>>> spMap) {
//        String[] split = lotteryNumber.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_DOLLAR_STR);
//        StringBuffer lotteryNumberSb = new StringBuffer();
//        for (String number : split) {
//            //            20170610001:3 20170610002:3 20170610003:0@1
//            String[] numberSplit = number.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_AT_STR);
//            String[] options = numberSplit[0].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.SPACE_SPLIT_STR);
//            StringBuffer optionSb = new StringBuffer();
//            for (String option : options) {
//                //20170610002:3#1
//                String[] singleBetOption = option.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant
//                        .COMMON_COLON_STR);
//                Map<String, Map<String, String>> singleSpMap = spMap.get(singleBetOption[0]);
//                if (singleSpMap == null || singleSpMap.isEmpty()) {
//                    throw new BusinessException("platform return ticket success, but sp is not complete.");
//                }
//                optionSb.append(singleBetOption[0]).append(CommonConstant.COMMON_COLON_STR);
//                String[] betCode = singleBetOption[1].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant
//                        .POUND_SPLIT_STR);
//                StringBuffer betCodeBuffer = new StringBuffer();
//                for (String code : betCode) {
//                    String[] split1 = code.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_WAVE_STR);
//                    Map<String, String> map = singleSpMap.get(split1[0]);
//                    String singleCodeSp = map.get(split1[1]);
//                    String[] singleCodeSpArray = singleCodeSp.split(CommonConstant.COMMON_ESCAPE_STR +
//                            CommonConstant.COMMON_VERTICAL_STR);
//                    if (StringUtils.isBlank(singleCodeSp) || BigDecimal.ONE.compareTo(new BigDecimal
//                            (singleCodeSpArray[0])) > 0) {
//                        throw new BusinessException("platform return ticket success, but sp is not complete or sp " +
//                                "less than 1");
//                    }
//                    betCodeBuffer.append(code).append(CommonConstant.COMMON_DASH_STR).append(singleCodeSp).append
//                            (CommonConstant.POUND_SPLIT_STR);
//                }
//                betCodeBuffer = betCodeBuffer.deleteCharAt(betCodeBuffer.length() - 1);
//                optionSb.append(betCodeBuffer).append(CommonConstant.SPACE_SPLIT_STR);
//            }
//            optionSb = optionSb.deleteCharAt(optionSb.length() - 1);
//            lotteryNumberSb.append(optionSb).append(CommonConstant.COMMON_AT_STR).append(numberSplit[1]).append
//                    (CommonConstant.COMMON_DOLLAR_STR);
//        }
//        lotteryNumberSb = lotteryNumberSb.deleteCharAt(lotteryNumberSb.length() - 1);
//        return lotteryNumberSb.toString();
//    }

//
//    public static void main(String[] args) {
//
//        LocalTime currentDate = LocalTime.now();
//        LocalDate localDate = LocalDate.now();
//
//        LocalTime.parse()
//        boolean s = currentDate.isBefore(LocalTime.of(9,30,01));
//        System.out.println(s);
//
//
//        System.out.println(localDate.getDayOfWeek().getValue());
//        if (localDate.getDayOfWeek().getValue() == 2 || localDate.getDayOfWeek().getValue() == 5 ||localDate.getDayOfWeek().getValue() == 7){
//            System.out.println("aaa");
//        }
//    }
}
