package com.linglong.lottery_backend.lottery.match;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linglong.lottery_backend.lottery.match.ctrl.MatchController;
import com.linglong.lottery_backend.order.entity.Order;
import com.linglong.lottery_backend.order.service.IOrderService;
import com.linglong.lottery_backend.prize.service.TblBettingTicketGroupService;
import com.linglong.lottery_backend.prize.task.PrizeAsyncExecutorTask;
import com.linglong.lottery_backend.ticket.service.LotteryBetService;
import com.linglong.lottery_backend.ticket.service.SplitService;
import com.linglong.lottery_backend.ticket.service.TblBettingTicketService;
import com.linglong.lottery_backend.ticket.task.TicketAsyncExecutorTask;
import com.linglong.lottery_backend.ticket.thread.AfterPayTask;
import com.linglong.lottery_backend.ticket.thread.ThreadPool;
import com.linglong.lottery_backend.utils.DateUtil;
import com.linglong.lottery_backend.utils.SmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RedisTests {

    @Autowired
    MatchController controller;

    @Autowired
    TicketAsyncExecutorTask ticketAsyncExecutorTask;

    @Autowired
    PrizeAsyncExecutorTask prizeAsyncExecutorTask;

    @Autowired
    TblBettingTicketGroupService bettingTicketGroupService;

    @Autowired
    TblBettingTicketService bettingTicketService;

    @Autowired
    IOrderService iOrderService;

    @Autowired
    private SplitService splitService;

    @Autowired
    private LotteryBetService lotteryBetService;

    @Autowired
    SmsUtil smsUtil;

    @Test
    public void contextLoads() throws UnsupportedEncodingException {
//       String body = "msg=%3Cmessage%3E%3Cbody%3E%3Ctickets%3E%3Cticket+tid%3D%222019041123106916768%22+status%3D%222%22+sp%3D%223%4012.0%3B0%4012.0%22+srno%3D%22200243-945712-189922-67%22+ckcode%3D%223D17B2C5%22+pname%3D%22%3F%3F%3F%3F%3F%3F%3F%3F%3F%3F%3FB2%3F109%3F%22+pcode%3D%22M13504%22+ptime%3D%2219%2F04%2F30+14%3A01%3A03%22+%2F%3E%3C%2Ftickets%3E%3C%2Fbody%3E%3C%2Fmessage%3E&cmd=3003";
//        body = URLDecoder.decode(body,"utf-8");
//        List<TicketBody> ticketBody = assemblyTicketBody(body);
//        log.info("receive from 3303 body: {}", JSON.toJSONString(ticketBody));
//        if (!ticketBody.isEmpty()){
//            ticketAsyncExecutorTask.doUpdateTicketStatus(ticketBody.get(0).getTickets());
//        }
//        List<Ticket> tickets = new ArrayList<>();
//        Ticket ticket = new Ticket();
//        ticket.setTicketId("1126767532280254464");
////        Ticket ticket1 = new Ticket();
////        ticket1.setTicketId("1125640564038045696");
////
////        //ticket.setStatus(2);
//        tickets.add(ticket);


        //ticketAsyncExecutorTask.doUpdateTicketStatus(tickets);
//        Ticket ticket2 = new Ticket();
//        ticket2.setTicketId("1124972596018941952");
//        tickets.add(ticket2);
//        List<List<Ticket>> averageTicket = Stream.iterate(0, n->n+1).limit(tickets.size()).parallel().map(a->{
//            List<Ticket> sendList = tickets.stream().skip(a*5).limit(5).parallel().collect(Collectors.toList());
//            return sendList;
//        }).collect(Collectors.toList());
//        System.out.println(JSON.toJSONString(averageTicket));
//        ticketAsyncExecutorTask.doRepeatQueryTicketStatus(tickets);
        //ticketAsyncExecutorTask.doRepeatQueryTicketPrizeStatus("201904302002","300");


//        String body = "[{\"bettingTicketId\":1125640564038045696,\"chuanguan\":\"1_1\",\"matchId\":44160,\"matchIssue\":\"20190507001\",\"matchSn\":\"周二001\",\"odds\":12.0,\"oddsCode\":\"1\",\"orderId\":1125640557750784000,\"playCode\":\"jqs\",\"ticketDetailsId\":1125739931373801472}]";
//        List<TblBettingTicketDetails> details = JSON.parseArray(body, TblBettingTicketDetails.class);
//        prizeAsyncExecutorTask.doSaveBettingTicketDetails(details);


//        List<Long> l = new LinkedList<>();
//        l.add(Long.valueOf("1126034317047369728"));
//        System.out.println(JSON.toJSONString(bettingTicketGroupService.findByTicketIds(l)));
//
//        PrizeResult result = JSON.parseObject("{\"match_id\": 27366, \"result\": [{\"code\": \"spf\", \"result\": \"1\", \"odds\": \"3.34\", \"is_danguan\": \"0\"}, {\"code\": \"rqspf\", \"result\": \"3\", \"odds\": \"1.62\", \"is_danguan\": \"0\"}, {\"code\": \"bifen\", \"result\": \"31\", \"odds\": \"9.50\", \"is_danguan\": \"1\"}, {\"code\": \"jqs\", \"result\": \"0\", \"odds\": \"3.65\", \"is_danguan\": \"1\"}, {\"code\": \"bqc\", \"result\": \"02\", \"odds\": \"30.00\", \"is_danguan\": \"1\"}]}", PrizeResult.class);
////
//        prizeAsyncExecutorTask.doUpdateBatchBettingTicketDetails(result);

//        List<Long> l = new ArrayList<>();
//
//
//        List<BettingTicket> tickets = bettingTicketService.findByOrderId(Long.valueOf("1127184516885123072"));
//        tickets.forEach(e->{
//            l.add(e.getTicketId());
//        });
//        prizeAsyncExecutorTask.updateOrderStatus(l);
//        List<BettingTicket> tickets = bettingTicketService.findByOrderId(Long.valueOf("1126319021877235712"));
//        List<BettingTicket> success = tickets.stream().filter(e -> e.getTicketStatus().equals(2)).collect(Collectors.toList());
//        System.out.println(JSON.toJSONString(success));

        Order order = iOrderService.findByOrderId(Long.valueOf("1127785779540660224"));

        String orderDetails = order.getOrderDetails();
        JSONObject detailsObject = JSON.parseObject(orderDetails);
        //2.查看截止时间-年月日点分
        String deadline = detailsObject.get("deadline").toString();

        //int ret = splitService.splitOrder(order.getOrderId(), order.getUserId(), DateUtil.formatString(deadline, DateUtil.DATE_FORMAT_YYYYMMDD_HH_MM));
        ThreadPool.getInstance().executeRunnable(new AfterPayTask(order.getOrderId(), order.getUserId(),
                order.getGameType(), DateUtil.formatString(deadline, DateUtil.DATE_FORMAT_YYYYMMDD_HH_MM),
                splitService, lotteryBetService));

        //smsUtil.sendWarningMessage("13106532014", "158210");

        //controller.getMatchList(1);
//        List<Long> r = new ArrayList<>();
//        r.add(Long.valueOf("1128986259004461056"));
//         prizeAsyncExecutorTask.updateBettingBatchTicket(r);
        //System.out.printf(JSON.toJSONString(bettingTickets));
    }


}
