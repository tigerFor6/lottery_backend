package com.linglong.lottery_backend.order.service;

import com.linglong.lottery_backend.lottery.match.common.Result;
import com.linglong.lottery_backend.order.entity.Order;
import com.linglong.lottery_backend.order.listener.bean.HitPrizeResult;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @Author: qihua.li
 * @since: 2019-04-03
 */
public interface IOrderService {
    /**
     * 比赛时间的校验
     */
    void checkOrder(String userId, Map<String, String> orderMap) throws ParseException, TimeoutException;

    /**
     * 创建订单
     */
    Map<String, Object> createOrder(String userId, Map<String, String> orderMap);

    /**
     * 订单列表页
     */
    Map<String, Object> queryOrderList(String userId, String gameType, int page, int size);

    /**
     * 订单详情页
     */
    Map<String, Object> queryOrderDetails(String userId, Long orderId);

    /**
     * 订单校验
     * 成功，则修改为已完成
     */
    Result checkOrderOdds(String userId, Long orderId,Long userCouponId) throws ParseException, TimeoutException;

    /**
     * 支付获取订单金额(扣减余额)支付成功修改支付状态为已支付 pay_status=1
     */
    Result minusOrderFee(String userId, Long orderId, Long userCouponId);

    /**
     * 出票失败，修改为退款中
     */
    void updatePayRefundingStatus(Long orderId);

    /**
     * 出票失败，查询到退款成功，修改为已退款
     */
    void updatePayRefundedStatus(Long orderId, BigDecimal amount, Integer status);

    /**
     * 部分出票，修改为部分出票
     */
    void updatePartBillStatus(Long orderId, BigDecimal amount);

    /**
     * 全出票
     */
    void updateAllBillStatus(Long orderId, Integer status);

    /**
     * 全部开奖
     */
    void updateOpenAllPrizeStatus(Long orderId, Integer state, Integer hitStatus,BigDecimal amount);

    /**
     * 更新开奖和中奖状态
     * @param hitPrizeResults
     */
    void updateOpenAndHitPrizeStatus(List<HitPrizeResult> hitPrizeResults);


    void updateInvalidOrder();

    Order findByOrderId(Long orderId);

    Order findByOrderIdAndUserId(Long orderId, String userId);

    void updateOrderOdds(Order e);

    void updateStatus(String orderId);

    void updateOrder(Order order);

    Map<String, Object> queryAvailableCouponList(Long orderId);
}
