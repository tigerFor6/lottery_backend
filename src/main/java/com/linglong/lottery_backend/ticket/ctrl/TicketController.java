package com.linglong.lottery_backend.ticket.ctrl;

import com.linglong.lottery_backend.lottery.match.common.Result;
import com.linglong.lottery_backend.lottery.match.common.ResultGenerator;
import com.linglong.lottery_backend.order.service.IOrderService;
import com.linglong.lottery_backend.prize.entity.BettingTicketDigital;
import com.linglong.lottery_backend.prize.entity.TblBettingTicketDetails;
import com.linglong.lottery_backend.prize.entity.TblBettingTicketGroup;
import com.linglong.lottery_backend.prize.service.TblBettingTicketDetailsService;
import com.linglong.lottery_backend.prize.service.TblBettingTicketGroupService;
import com.linglong.lottery_backend.ticket.cache.GameCache;
import com.linglong.lottery_backend.ticket.cache.WeightCache;
import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import com.linglong.lottery_backend.ticket.service.TblBettingTicketDigitalService;
import com.linglong.lottery_backend.ticket.service.TblBettingTicketService;
import com.linglong.lottery_backend.ticket.task.TicketAsyncExecutorTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
@RestController
@RequestMapping(value = "/api/ticket")
@Slf4j
public class TicketController {

    @Autowired
    TblBettingTicketService bettingTicketService;

    @Autowired
    TblBettingTicketDigitalService tblBettingTicketDigitalService;

    @Autowired
    IOrderService iOrderService;

    @Autowired
    TblBettingTicketGroupService bettingTicketGroupService;

    @Autowired
    TblBettingTicketDetailsService bettingTicketDetailsService;

    @Autowired
    TicketAsyncExecutorTask ticketAsyncExecutorTask;

    @Autowired
    GameCache gameCache;


    @Autowired
    WeightCache weightCache;

    @GetMapping("getBettingTicketList")
    public Result getBettingTicketList(@RequestParam("orderId") Long orderId) {
        List<BettingTicket> tickets = bettingTicketService.findByOrderId(orderId);
        List<BettingTicket> success = tickets.stream().filter(e -> e.getTicketStatus().equals(2)).collect(Collectors.toList());
        List<Map<String,Object>> result = new LinkedList<>();
        if (success.isEmpty()){
            return ResultGenerator.genProcessingResult(result);
        }
        List<Long> ticketIds = success.stream().map(e -> e.getTicketId()).collect(Collectors.toList());
        List<TblBettingTicketDetails> ticketDetails = bettingTicketDetailsService.findByTicketIds(ticketIds);
        //查询组合
        List<TblBettingTicketGroup> ticketGroups = bettingTicketGroupService.findByTicketIds(ticketIds);
        Map<Long, List<TblBettingTicketDetails>> detailMap = ticketDetails.stream().collect(Collectors.groupingBy(TblBettingTicketDetails::getBettingTicketId));
        Map<Long, List<TblBettingTicketGroup>> groupMap = ticketGroups.stream().collect(Collectors.groupingBy(TblBettingTicketGroup::getBettingTicketId));
        success.forEach(e -> {
            Map<String, Object> m = new HashMap<>();
            m.put("voucherNo",String.valueOf(e.getTicketId()));
            m.put("ticketStatus",String.valueOf(e.getTicketStatus()));
            m.put("prizeStatus",String.valueOf(e.getPrizeStatus()));
            String bettingType = e.getExtra().split("_")[0];
            List<Map<String, Object>> detailList = new ArrayList<>();
            List<TblBettingTicketGroup> groups = groupMap.get(e.getTicketId());
            List<TblBettingTicketDetails> details = detailMap.get(e.getTicketId());
            Map<Long, TblBettingTicketDetails> detailsMap = details.stream().collect(Collectors.toMap(TblBettingTicketDetails::getTicketDetailsId, Function.identity()));
            groups.forEach(g->{
                Map<String, Object> resGroup = new HashMap<>();
                resGroup.put("bettingType", bettingType);
                resGroup.put("multiple", e.getTimes());
                resGroup.put("groupStatus",g.getStatus());
                String detail = g.getDetail();
                String[] detailIds = detail.split(",");
                List<TblBettingTicketDetails> group = new LinkedList<>();
                for (int i = 0; i < detailIds.length; i++) {
                    group.add(detailsMap.get(Long.valueOf(detailIds[i])));
                }
                List<Map<String, Object>> r = group.stream().map(d -> {
                    Map<String, Object> md = new HashMap<>();
                    md.put("playCode", d.getPlayCode());
                    md.put("matchSn", d.getMatchSn());
                    md.put("oddsCode", d.getOddsCode());
                    md.put("odds", String.valueOf(d.getOdds()));
                    return md;
                }).collect(Collectors.toList());
                resGroup.put("detail",r);
                detailList.add(resGroup);
            });
            m.put("group",detailList);
            result.add(m);
        });
        return ResultGenerator.genSuccessResult(result);
    }

    @GetMapping("refundTicket")
    public Result refundTicket(@RequestParam("ticketId") Long ticketId) {

        if(ticketId == null) {
            return ResultGenerator.genFailResult("票号不能为空!");
        }
        BettingTicket bettingTicket = bettingTicketService.findByTicketId(ticketId);
        if(bettingTicket == null) {
            return ResultGenerator.genFailResult("票不存在!");
        }

        if(!(bettingTicket.getTicketStatus().equals(Integer.valueOf(0)) || bettingTicket.getTicketStatus().equals(Integer.valueOf(1)) || bettingTicket.getTicketStatus().equals(Integer.valueOf(-3)))) {
            return ResultGenerator.genFailResult("票不是待出票或出票中的状态!");
        }
        bettingTicket.setTicketStatus(-2);
        bettingTicketService.updateBettingTicket(bettingTicket);

        List<Long> ids = new ArrayList<>();
        ids.add(bettingTicket.getTicketId());
        ticketAsyncExecutorTask.doUpdateOrderStatus(ids);

        return ResultGenerator.genSuccessResult();
    }



    @GetMapping("getBettingTicketExample")
    public Result getBettingTicketExample(Integer pageNum,Integer pageSize,BettingTicket ticket) {
        Page<BettingTicket> tickets = bettingTicketService.findAllByExample(pageNum,pageSize,ticket);
        return ResultGenerator.genSuccessResult(tickets);
    }

    @GetMapping("refresh")
    public void refresh() {

        gameCache.refresh();
        weightCache.refresh();
    }

}
