package com.linglong.lottery_backend.ticket.runner;

import com.linglong.lottery_backend.lottery.match.cache.TblSaleDateCache;
import com.linglong.lottery_backend.lottery.match.entity.TblSaleDate;
import com.linglong.lottery_backend.order.entity.Order;
import com.linglong.lottery_backend.order.service.IOrderService;
import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.cache.GameCache;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.constant.GameConstant;
import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import com.linglong.lottery_backend.ticket.entity.TicketStatus;
import com.linglong.lottery_backend.ticket.entity.bo.TicketNumber;
import com.linglong.lottery_backend.ticket.service.TblBettingTicketService;
import com.linglong.lottery_backend.ticket.service.cron.SendOrderCoordinator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TicketRunner implements ApplicationRunner {


    @Autowired
    TblBettingTicketService bettingTicketService;

    @Autowired
    IOrderService iOrderService;

    @Autowired
    SendOrderCoordinator sendOrderCoordinator;

    @Autowired
    TblSaleDateCache tblSaleDateCache;

    @Override
    public void run(ApplicationArguments args) {
        log.info("TicketRunner start run");
       List<BettingTicket> tickets =  bettingTicketService.findByTicketStatus(TicketStatus.unknown.getValue());
        Map<Long, List<BettingTicket>> ticketMap = tickets.stream().collect(Collectors.groupingBy(BettingTicket::getOrderId));

        ticketMap.forEach((k,v)->{
            Order order = iOrderService.findByOrderId(k);
            if (order.getGameType().equals(GameCache.getGame(GameConstant.FC_SSQ).getGameType()) ||
                    order.getGameType().equals(GameCache.getGame(GameConstant.DLT).getGameType())){
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
                        log.info("不符合出票时间---->"+k);
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
                    log.info("PL3 不符合出票时间("+startTime+" -> "+endTime+") orderId("+order.getOrderId()+")");
                    return;
                }

            }

            Map<Integer, List<TicketDto>> platformTicketsMap = new HashMap<>();
            for (BettingTicket ticket : v) {
                if (!platformTicketsMap.containsKey(ticket.getPlatformId())) {
                    List<TicketDto> ticketDtoList = new ArrayList<>();
                    platformTicketsMap.put(ticket.getPlatformId(), ticketDtoList);
                }
                List<TicketNumber> nums = ticket.getTicketNumbers();
                TicketDto ticketDto = new TicketDto(ticket.getTicketId(), ticket.getTicketAmount(), nums, ticket.getPlayType(), ticket.getExtra(), ticket.getTimes(),ticket.getGameType(),ticket.getOrderId(),ticket.getCreateTime());
                platformTicketsMap.get(ticket.getPlatformId()).add(ticketDto);
            }
            for (Map.Entry<Integer, List<TicketDto>> entry : platformTicketsMap.entrySet()) {
                sendOrderCoordinator.addToBet(order.getGameType(), entry.getKey(), k, entry.getValue());
            }
        });

    }
}
