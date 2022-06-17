package com.linglong.lottery_backend.order.controller;

import com.linglong.lottery_backend.common.error.LoginException;
import com.linglong.lottery_backend.lottery.match.common.Code;
import com.linglong.lottery_backend.lottery.match.common.ResultGenerator;
import com.linglong.lottery_backend.lottery.match.service.TblMatchOddsService;
import com.linglong.lottery_backend.order.model.Result;
import com.linglong.lottery_backend.order.model.StatusCode;
import com.linglong.lottery_backend.order.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @Author: qihua.li
 * @since: 2019-04-03
 */
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final IOrderService orderService;
    private final TblMatchOddsService matchOddsService;
    //private static final String userId = "1116997875117727744";
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping("/create")
    public Result createOrder(@RequestBody @NotNull Map<String, String> orderMap, HttpSession session) throws TimeoutException, ParseException {
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            throw new LoginException("session里获取不到用户id,需要重新登录");
        }
        //比赛截止时间的校验
        orderService.checkOrder(userId.toString(), orderMap);
        Map<String, Object> orderResult = orderService.createOrder(userId.toString(), orderMap);
        return new Result(StatusCode.OK.getCode(), "订单保存成功", orderResult);
    }

    @PostMapping("/detail")
    public Result queryOrderDetails(@RequestBody Map<String, String> map, @SessionAttribute String userId) {
        Long orderId = Long.parseLong(map.get("orderId"));
        logger.info("orderId={}", orderId);
        Map<String, Object> orderMap = orderService.queryOrderDetails(userId, orderId);
        return new Result(StatusCode.OK.getCode(), "查询订单详情", orderMap);
    }

    @GetMapping("/list")
    public Result queryOrderList(@RequestParam("game_type") String game_type,
                                 @RequestParam Integer page,@RequestParam Integer size,
                                 @SessionAttribute String userId) {
        if(page == null || page.intValue() <= 0) {
            page = new Integer(1);
        }
        if(size == null || size.intValue() <= 0) {
            size = new Integer(13);
        }

        Map<String, Object> resultMap = orderService.queryOrderList(userId, game_type,page, size);
        return new Result(StatusCode.OK.getCode(), "查询订单列表", resultMap);
    }

    @PostMapping("/pay")
    public com.linglong.lottery_backend.lottery.match.common.Result checkOrderOdds(@RequestBody Map<String, Object> map, HttpSession session) throws ParseException, TimeoutException {
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            throw new LoginException("session里获取不到用户id,需要重新登录");
        }
        Long orderId = Long.parseLong(map.get("order_id").toString());
        Object fee = map.get("amount");
        if (fee == null) {
            throw new NullPointerException("请输入金额");
        }
        BigDecimal amount = new BigDecimal(fee.toString());
        Long userCouponId = map.get("user_coupon_id") == null? 0 : Long.parseLong(map.get("user_coupon_id").toString());
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            return orderService.checkOrderOdds(userId.toString(), orderId, userCouponId);
        }
        return ResultGenerator.genExceptionResult(StatusCode.ERROR.getCode(), "金额必须大于0");
    }

    @GetMapping("/queryAvailableCouponList")
    public Result queryAvailableCouponList(@RequestParam("order_id") String order_id, HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            throw new LoginException("session里获取不到用户id,需要重新登录");
        }
        Long orderId = Long.parseLong(order_id);
        Map<String, Object> resultMap = orderService.queryAvailableCouponList(orderId);
        return new Result(StatusCode.OK.getCode(), "查询可使用红包列表列表", resultMap);
    }

    /**
     * @description: 得到用户的中奖次数
     **/
    @GetMapping("/getRewardNum")
    public Object getRewardNum(HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            throw new LoginException("session里获取不到用户id,需要重新登录");
        }

        String num = redisTemplate.opsForValue().get(Code.Redis.REWARD_NUM_KEY + userId);
        if (null == num || "".equals(num)) {
            return ResultGenerator.genSuccessResult(0);
        }
        return ResultGenerator.genSuccessResult(Integer.valueOf(num));
    }

    /**
     * @description: 清空用户中奖数
     **/
    @GetMapping("/deleteRewardNum")
    public void deleteRewardNum(HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            throw new LoginException("session里获取不到用户id,需要重新登录");
        }
        redisTemplate.delete(Code.Redis.REWARD_NUM_KEY + userId);
    }

}
