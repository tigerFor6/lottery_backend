package com.linglong.lottery_backend.order.aop;

import com.google.common.collect.Lists;
import com.linglong.lottery_backend.lottery.match.common.Code;
import com.linglong.lottery_backend.order.entity.Order;
import com.linglong.lottery_backend.order.entity.OrderDetailsInfo;
import com.linglong.lottery_backend.order.repository.OrderDetailsInfoRepository;
import com.linglong.lottery_backend.order.repository.OrderRepository;
import com.linglong.lottery_backend.utils.OrderStatusUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @Author: qihua.li
 * @since: 2019-04-12
 */

@Aspect
@Component
@Slf4j
public class OrderAspect {

    @Autowired
    private OrderDetailsInfoRepository orderDetailsInfoRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    /**
     * @description: 切开奖service，如果中奖则记录用户开奖次数，有效期一天
     **/
    @Pointcut(value = "execution(* com.linglong.lottery_backend.order.service.IOrderService.updateOpenAllPrizeStatus(..))&& args(orderId,state,hitStatus,amount)")
    public void updateOpenAllPrizeStatus(Long orderId, Integer state, Integer hitStatus, BigDecimal amount) {

    }

    @After(value = "updateOpenAllPrizeStatus(orderId,state,hitStatus,amount)")
    public void afterUpdateOpenAllPrizeStatus(Long orderId, Integer state, Integer hitStatus, BigDecimal amount) {
        OrderDetailsInfo orderDetailsInfo = orderDetailsInfoRepository.findByOrderId(orderId);

        Order order = orderRepository.findByOrderId(orderId);

        //得到订单状态的code组合
        String statusNum = OrderStatusUtil.getStatusNum(orderDetailsInfo);

        List<String> statusList = Lists.newArrayList("12210", "12110", "122992", "122990", "121992", "121990", "12211", "12111", "122991", "121991", "23210", "23110", "232992", "232990", "231992", "231990", "23211", "23111", "232991", "231991"
        );

        //判断用户是否中奖(中奖进入if)
        if (statusList.contains(statusNum)) {

            String rewardNum = redisTemplate.opsForValue().get(Code.Redis.REWARD_NUM_KEY + order.getUserId());
            if (null == rewardNum || "".equals(rewardNum.trim())) {
                log.info("未查询到用户" + order.getUserId() + "的中奖次数，将该用户中奖次数初始化为1,有效期为1天");
                redisTemplate.opsForValue().set(Code.Redis.REWARD_NUM_KEY + order.getUserId(), "1", 1L, TimeUnit.DAYS);
            } else {
                redisTemplate.opsForValue().set(Code.Redis.REWARD_NUM_KEY + order.getUserId(), String.valueOf(Integer.valueOf(rewardNum) + 1), 1L, TimeUnit.DAYS);
                log.info("已查询到用户" + order.getUserId() + "的中奖次数，中奖次数为" + Integer.valueOf(rewardNum) + 1);
            }
        }
    }
}
