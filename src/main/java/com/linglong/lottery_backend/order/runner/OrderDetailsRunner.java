package com.linglong.lottery_backend.order.runner;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.linglong.lottery_backend.order.entity.Order;
import com.linglong.lottery_backend.order.entity.OrderDetails;
import com.linglong.lottery_backend.order.entity.OrderDetailsInfo;
import com.linglong.lottery_backend.order.repository.OrderDetailsInfoRepository;
import com.linglong.lottery_backend.order.repository.OrderDetailsRepository;
import com.linglong.lottery_backend.order.repository.OrderRepository;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.enums.AoliEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class OrderDetailsRunner implements ApplicationRunner {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderDetailsInfoRepository orderDetailsInfoRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Order> orders = orderRepository.findByOrderDetailsIsNot0();
        if(orders.isEmpty())
            return ;

        orders.forEach(order -> {
            OrderDetailsInfo orderDetailsInfo = new OrderDetailsInfo();
            orderDetailsInfo.setOrderId(order.getOrderId());

            JSONObject details = JSONObject.parseObject(order.getOrderDetails());
            orderDetailsInfo.setBetNumber(details.getInteger("bet_number"));
            orderDetailsInfo.setBillStatus(details.getInteger("bill_status"));
            orderDetailsInfo.setPayStatus(details.getInteger("pay_status"));
            orderDetailsInfo.setChuanGuan(details.get("chuan_guan") == null ? "0" : details.getString("chuan_guan"));
            orderDetailsInfo.setChasing(details.get("chasing") == null ? 0 : details.getInteger("chasing"));
            orderDetailsInfo.setDeadLine(details.getString("deadline"));
            orderDetailsInfo.setMultiple(details.getInteger("multiple"));
            orderDetailsInfo.setBonus(details.getBigDecimal("bonus"));
            orderDetailsInfo.setOpenPrizeStatus(details.getInteger("open_prize_status"));
            orderDetailsInfo.setHitPrizeStatus(details.getInteger("hit_prize_status"));
            orderDetailsInfo.setDeliveryPrizeStatus(details.getInteger("delivery_prize_status"));
            orderDetailsInfo.setSuccessTickets(details.getInteger("success_tickets"));
            orderDetailsInfo.setTotalTickets(details.getInteger("total_tickets"));
            orderDetailsInfo.setSplitStatus(details.getInteger("split_status"));
            orderDetailsInfo.setCreateTime(details.getDate("create_time"));
            orderDetailsInfo.setUpdateTime(details.getDate("update_time"));

            List<OrderDetails> orderDetailsList = new ArrayList<>();
            if(order.getGameType().equals(AoliEnum.JCZQ.getCode()) || order.getGameType().equals(AoliEnum.JCLQ.getCode())) {
                JSONArray matchsJSON = details.getJSONArray("matchs");

                for (int i = 0; i < matchsJSON.size(); i++) {
                    JSONObject matchsData = matchsJSON.getJSONObject(i);

                    OrderDetails orderDetails = new OrderDetails();
                    orderDetails.setMatchId(matchsData.getLong("match_id"));
                    orderDetails.setMatchName(matchsData.getString("match_name").replaceAll(CommonConstant.COMMA_SPLIT_STR, "VS"));
                    orderDetails.setOrderId(order.getOrderId());
                    orderDetails.setIssue(matchsData.getString("match_issue"));
                    orderDetails.setSn(matchsData.getString("match_sn"));
                    orderDetails.setPlaysDetails(matchsData.getString("plays"));
                    orderDetailsList.add(orderDetails);
                }

            }else {
                String period = "";
                String plays = "";
                for(String key : details.keySet()) {
                    if(key.indexOf("tails") >= 0)
                        plays = details.getString(key);

                    if(key.indexOf("eriods") >= 0) {
                        period = details.getString(key);
                    }
                }
                OrderDetails orderDetails = new OrderDetails();
                orderDetails.setOrderId(order.getOrderId());
                orderDetails.setIssue(period);
                orderDetails.setPlaysDetails(plays);
                orderDetailsList.add(orderDetails);
            }

            order.setOrderDetails("0");
            orderRepository.saveAndFlush(order);
            if(orderDetailsInfoRepository.findByOrderId(order.getOrderId()) == null) {
                orderDetailsInfoRepository.save(orderDetailsInfo);
            }

            List<OrderDetails> check = orderDetailsRepository.findByOrderId(order.getOrderId());
            if(check.isEmpty()) {
                orderDetailsRepository.saveAll(orderDetailsList);
            }

        });
        log.info("转换Order_details 完成......");
    }

}
