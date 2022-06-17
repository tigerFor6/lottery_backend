package com.linglong.lottery_backend.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.linglong.lottery_backend.order.entity.OrderDetailsInfo;
import com.linglong.lottery_backend.order.model.order_model.OrderDetailStatus;
import com.linglong.lottery_backend.order.model.order_model.OrderListStatus;
import com.linglong.lottery_backend.order.entity.Order;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
public class OrderStatusUtil {

    public static String getStatusNum(OrderDetailsInfo orderDetailsInfo) {
        //支付状态
        Integer payStatus = orderDetailsInfo.getPayStatus();
        //出票状态
        Integer billStatus = orderDetailsInfo.getBillStatus();
        //开奖状态
        Integer openPrizeStatus = orderDetailsInfo.getOpenPrizeStatus();
        //中奖状态
        Integer hitPrizeStatus = orderDetailsInfo.getHitPrizeStatus();
        //派奖状态
        Integer deliveryPrizeStatus = orderDetailsInfo.getDeliveryPrizeStatus();

        return new StringBuilder()
                .append(payStatus)
                .append(billStatus)
                .append(openPrizeStatus)
                .append(hitPrizeStatus)
                .append(deliveryPrizeStatus).toString();
    }

    public static void fetchOrderListStatus(Order order, OrderDetailsInfo orderDetailsInfo,  Map<String, Object> orderMap) {

        String statusNum = getStatusNum(orderDetailsInfo);

        if (statusNum.startsWith("0")) {
            Date deadline = order.getDeadline();
            Date now = new Date();
            if (now.compareTo(deadline) > 0) {
                orderMap.put("status", OrderListStatus.CANCEL.getValue());
                orderMap.put("status_code", OrderListStatus.CANCEL.getCode());
                return;
            } else {
                orderMap.put("status", OrderListStatus.WAIT_PAY.getValue());
                orderMap.put("status_code", OrderListStatus.WAIT_PAY.getCode());
                return;
            }
        }

        if (statusNum.startsWith("10")) {
            orderMap.put("status", OrderListStatus.NOT_ISSUE.getValue());
            orderMap.put("status_code", OrderListStatus.NOT_ISSUE.getCode());
            return;
        }

        if (statusNum.startsWith("11")) {
            orderMap.put("status", OrderListStatus.PROCESSING_ISSUE.getValue());
            orderMap.put("status_code", OrderListStatus.PROCESSING_ISSUE.getCode());
            return;
        }

        if (statusNum.startsWith("3-1") || statusNum.startsWith("3-2")) {
            orderMap.put("status", OrderListStatus.ORDER_FAIL.getValue());
            orderMap.put("status_code", OrderListStatus.ORDER_FAIL.getCode());
            return;
        }

        if (statusNum.startsWith("120") || statusNum.startsWith("1210")) {
            orderMap.put("status", OrderListStatus.NOT_LOTTERY.getValue());
            orderMap.put("status_code", OrderListStatus.NOT_LOTTERY.getCode());
            return;
        }

        if (statusNum.startsWith("230") || statusNum.startsWith("2310")) {
            orderMap.put("status", OrderListStatus.PART_NOT_LOTTERY.getValue());
            orderMap.put("status_code", OrderListStatus.PART_NOT_LOTTERY.getCode());
            return;
        }

        if (statusNum.startsWith("1220") || statusNum.startsWith("2320")) {
            orderMap.put("status", OrderListStatus.BILL_FAIL.getValue());
            orderMap.put("status_code", OrderListStatus.BILL_FAIL.getCode());
            return;
        }

        List<String> statusList = Lists.newArrayList("12210", "12110", "12211", "12111", "23210",
                "23110", "23211", "23111", "231991", "122991", "121991", "122992", "122990",
                "121992", "121990", "122991", "121991", "232992", "232990", "121992", "121990", "232991"
        );

        orderMap.put("status", OrderListStatus.WIN_BONUS.getValue());
        orderMap.put("status_code", OrderListStatus.WIN_BONUS.getCode());
        orderMap.put("bonus", getRealPrice(orderDetailsInfo.getBonus()));
    }

    public static void fetchOrderDetailStatus(Order order, OrderDetailsInfo orderDetailsInfo,  Map<String, Object> orderMap) {
        String statusNum = getStatusNum(orderDetailsInfo);
        log.info("fetchOrderDetailStatus: {}", statusNum);
        if (statusNum.startsWith("0")) {
            Date deadline = order.getDeadline();
            Date now = new Date();
            if (now.compareTo(deadline) > 0) {
                orderMap.put("code", OrderDetailStatus.CANCEL_NOT_PAY.getCode());
                orderMap.put("orderStatus", OrderDetailStatus.CANCEL_NOT_PAY.getOrderStatus());
                orderMap.put("payStatus", OrderDetailStatus.CANCEL_NOT_PAY.getPayStatus());
                return;
            } else {
                orderMap.put("code", OrderDetailStatus.WAIT_PAY.getCode());
                orderMap.put("orderStatus", OrderDetailStatus.WAIT_PAY.getOrderStatus());
                orderMap.put("payStatus", OrderDetailStatus.WAIT_PAY.getPayStatus());
                return;
            }
        }
//
//        if (statusNum.startsWith("31") || statusNum.startsWith("32")) {
//            orderMap.put("code", OrderDetailStatus.BILL_FAIL_WITHDRAWRLING.getCode());
//            orderMap.put("orderStatus", OrderDetailStatus.BILL_FAIL_WITHDRAWRLING.getOrderStatus());
//            orderMap.put("payStatus", OrderDetailStatus.BILL_FAIL_WITHDRAWRLING.getPayStatus());
//            return;
//        }

        if (statusNum.startsWith("3-1") || statusNum.startsWith("3-2")) {
            orderMap.put("code", OrderDetailStatus.BILL_FAIL_WITHDRAWRLING.getCode());
            orderMap.put("orderStatus", OrderDetailStatus.BILL_FAIL_WITHDRAWRLING.getOrderStatus());
            orderMap.put("payStatus", OrderDetailStatus.BILL_FAIL_WITHDRAWRLING.getPayStatus());
            return;
        }

        if (statusNum.startsWith("10")) {
            orderMap.put("code", OrderDetailStatus.BILLING_PAIED.getCode());
            orderMap.put("orderStatus", OrderDetailStatus.BILLING_PAIED.getOrderStatus());
            orderMap.put("payStatus", OrderDetailStatus.BILLING_PAIED.getPayStatus());
            return;
        }

        if (statusNum.startsWith("11")) {
            orderMap.put("code", OrderDetailStatus.PROCESSING_AND_PAIED.getCode());
            orderMap.put("orderStatus", OrderDetailStatus.PROCESSING_AND_PAIED.getOrderStatus());
            orderMap.put("payStatus", OrderDetailStatus.PROCESSING_AND_PAIED.getPayStatus());
            return;
        }

        if (statusNum.startsWith("120") || statusNum.startsWith("1210")) {
            orderMap.put("code", OrderDetailStatus.OPENING_PAIED.getCode());
            orderMap.put("orderStatus", OrderDetailStatus.OPENING_PAIED.getOrderStatus());
            orderMap.put("payStatus", OrderDetailStatus.OPENING_PAIED.getPayStatus());
            return;
        }

        if (statusNum.startsWith("1220")) {
            orderMap.put("code", OrderDetailStatus.NOT_PRIZING_PAIED.getCode());
            orderMap.put("orderStatus", OrderDetailStatus.NOT_PRIZING_PAIED.getOrderStatus());
            orderMap.put("payStatus", OrderDetailStatus.NOT_PRIZING_PAIED.getPayStatus());
            return;
        }

        List<String> verifyCodeList = Lists.newArrayList("12210", "12110", "122992", "122990", "121992", "121990");
        if (verifyCodeList.contains(statusNum)) {
            orderMap.put("code", OrderDetailStatus.PRIZED_VERIFYING.getCode());
            orderMap.put("orderStatus", OrderDetailStatus.PRIZED_VERIFYING.getOrderStatus());
            orderMap.put("payStatus", OrderDetailStatus.PRIZED_VERIFYING.getPayStatus());
            return;
        }

        List<String> deliveredCodeList = Lists.newArrayList("12211", "12111", "122991", "121991");
        if (deliveredCodeList.contains(statusNum)) {
            orderMap.put("code", OrderDetailStatus.PRIZED_DELIVERED.getCode());
            orderMap.put("orderStatus", OrderDetailStatus.PRIZED_DELIVERED.getOrderStatus());
            orderMap.put("payStatus", OrderDetailStatus.PRIZED_DELIVERED.getPayStatus());
            return;
        }

        if (statusNum.startsWith("230") || statusNum.startsWith("2310")) {
            orderMap.put("code", OrderDetailStatus.PART_BILLING_WITHDRAWALED.getCode());
            orderMap.put("orderStatus", OrderDetailStatus.PART_BILLING_WITHDRAWALED.getOrderStatus());
            orderMap.put("payStatus", OrderDetailStatus.PART_BILLING_WITHDRAWALED.getPayStatus());
            return;
        }

        if (statusNum.startsWith("2320")) {
            orderMap.put("code", OrderDetailStatus.PART_BILLING_NOT_PRIZED.getCode());
            orderMap.put("orderStatus", OrderDetailStatus.PART_BILLING_NOT_PRIZED.getOrderStatus());
            orderMap.put("payStatus", OrderDetailStatus.PART_BILLING_NOT_PRIZED.getPayStatus());
            return;
        }

        List<String> VerifyAndWithdrawal = Lists.newArrayList("23210", "23110", "232992", "232990", "121992", "121990");
        if (VerifyAndWithdrawal.contains(statusNum)) {
            orderMap.put("code", OrderDetailStatus.PART_BILLING_PRIZED_VERIFYING.getCode());
            orderMap.put("orderStatus", OrderDetailStatus.PART_BILLING_PRIZED_VERIFYING.getOrderStatus());
            orderMap.put("payStatus", OrderDetailStatus.PART_BILLING_PRIZED_VERIFYING.getPayStatus());
            return;
        }

        List<String> VerifyAndPrized = Lists.newArrayList("23211", "23111", "232991", "231991");
        if (VerifyAndPrized.contains(statusNum)) {
            orderMap.put("code", OrderDetailStatus.PART_BILLING_PRIZED_DELIVERED.getCode());
            orderMap.put("orderStatus", OrderDetailStatus.PART_BILLING_PRIZED_DELIVERED.getOrderStatus());
            orderMap.put("payStatus", OrderDetailStatus.PART_BILLING_PRIZED_DELIVERED.getPayStatus());
            return;
        }
    }

    private static BigDecimal getRealPrice(BigDecimal amount) {
        BigDecimal baseNum = new BigDecimal(100);
        return amount.divide(baseNum);
    }
}
