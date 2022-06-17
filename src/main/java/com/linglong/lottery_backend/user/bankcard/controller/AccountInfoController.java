package com.linglong.lottery_backend.user.bankcard.controller;

import com.linglong.lottery_backend.user.account.entity.User;
import com.linglong.lottery_backend.user.account.service.IUserService;
import com.linglong.lottery_backend.user.bankcard.pojo.AccountInfo;
import com.linglong.lottery_backend.user.bankcard.service.IAccountInfoService;
import com.linglong.lottery_backend.order.model.Result;
import com.linglong.lottery_backend.order.model.StatusCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: qihua.li
 * @since: 2019-03-20
 */
@RestController
@RequestMapping("/api/bankcard")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountInfoController {

    private static final Logger logger = LoggerFactory.getLogger(AccountInfoController.class);
    private final IAccountInfoService bankCardService;
    private final IUserService userService;
    private final StringRedisTemplate redisTemplate;

    @GetMapping("/list/{page}")
    public Result findBindingBankCardList(@PathVariable Integer page, HttpSession session) {
        List<AccountInfo> list = null;
        try {
            String userId = session.getAttribute("userId").toString();
            list = bankCardService.findBindingPage(userId, page);
            return new Result(StatusCode.OK.getCode(), "查询成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询所有绑定异常", e);
            return new Result(StatusCode.ERROR.getCode(), e.getMessage());
        }
    }

    @GetMapping("/{cardId}")
    public Result findBankCardDetail(@PathVariable Long cardId, HttpSession session) {
        AccountInfo accountInfo = null;
        try {
            String userId = session.getAttribute("userId").toString();
            accountInfo = bankCardService.findBankCardDetail(cardId, userId);
            return new Result(StatusCode.OK.getCode(), "获取成功", accountInfo);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询银行卡详情异常", e);
            return new Result(StatusCode.ERROR.getCode(), e.getMessage());
        }
        //return new ResponseEntity<>(accountInfo, HttpStatus.OK);
    }

    @PostMapping("/sendSms")
    public Result sendSms(@RequestBody @NotNull Map<String, String> body, HttpSession session) {
        String sendMessage = null;
        try {
            String mobile = body.get("mobile");
            logger.info("userid-session:{}", session.getAttribute("userId"));
            String userId = session.getAttribute("userId").toString();
            //String userId = "22";
            //校验手机号是否一样，有的话抛出异常。
            sendMessage = bankCardService.sendSms(mobile, userId);
            return new Result(StatusCode.OK.getCode(), sendMessage);
        } catch (Exception e) {
            logger.error("发送短信异常", e);
            return new Result(StatusCode.ERROR.getCode(), e.getMessage());
        }
        //return new ResponseEntity<String>(sendMessage, HttpStatus.OK);
    }

    @GetMapping("/binding/before")
    public Result getUserNameAndBankCard(@SessionAttribute String userId) {
        Map<String, Object> userMap = bankCardService.preFindAccountInfo(userId);
        if (userMap.isEmpty()) {
            return new Result(StatusCode.NOTFOUND_ERROR.getCode(), "无绑定");
        }
        logger.info("userInfo={}", userMap);
        return new Result(StatusCode.OK.getCode(), "已绑定信息", userMap);
    }

    @PostMapping("/check")
    public Result checkBankcard3Verity(@RequestBody Map<String, String> body, HttpSession session) throws Exception {
        String userId = session.getAttribute("userId").toString();
        String code = redisTemplate.opsForValue().get("check:"+userId);
        if (null == code){
            redisTemplate.opsForValue().set("check:"+userId,userId,3, TimeUnit.SECONDS);
        }else{
            return new Result(StatusCode.ERROR.getCode(), "重复操作", null);
        }

        String bankcardNum = body.get("bankcard_num");
        String idCard = body.get("id_card");
        String name = body.get("name");
        //是否实名
        User user = userService.findByRealnameAndIdCard(name,idCard);
        if (user != null) {
            if (!user.getUserId().equals(userId)){
                return new Result(StatusCode.ERROR.getCode(), "实名信息已被绑定!", 5);
            }
            if (!user.getRealname().equals(name) || !user.getIdCard().equals(idCard)) {
                return new Result(StatusCode.ERROR.getCode(), "用户名或身份证号码与实名不符!", 5);
            }
        }
        //不能重复绑定相同的卡,同一个银行卡不能被多个账号绑定
        bankCardService.checkBankcardNumIsDuplicate(userId, bankcardNum);
        bankCardService.deleteBankcardStatusIs2(userId, bankcardNum);
        //校验用户的身份证号码，只能绑定自己的
        bankCardService.checkIdCard(idCard, userId);
        Map<String, Object> mapMessage = bankCardService.checkBankcardVerity(bankcardNum, idCard, name, userId);
        Boolean flag = (Boolean) mapMessage.get("flag");
        String message = (String) mapMessage.get("message");
        Object isBound = mapMessage.get("isBound");
        redisTemplate.delete("check:"+userId);
        if (flag) {
            session.setAttribute("threeElements", body);
            mapMessage.remove("flag");
            //已绑定过得
            if (isBound != null) {
                return new Result(StatusCode.OK.getCode(), message, mapMessage);
            }
            return new Result(StatusCode.OK.getCode(), message, mapMessage);
        } else {
            return new Result(StatusCode.ERROR.getCode(), message, mapMessage);
        }
    }

    /**
     * 只能绑定同一个手机号绑定的卡
     *
     * @return
     */
    @PostMapping("/binding")
    public Result checkCodeAndBinding(@RequestBody Map<String, String> body, HttpSession session) throws Exception {
        String mobile = body.get("mobile");
        String code = body.get("code");
        String userId = session.getAttribute("userId").toString();
        //不允许输入其他手机号
        Boolean phoneIsBinding = bankCardService.checkPhoneIsBinding(mobile, userId);
        if (!phoneIsBinding) {
            return new Result(StatusCode.CHECK_ERROR.getCode(), "只能使用第一次绑定银行卡的手机号进行绑定", 1);
        }
        Map<String, String> map = (Map<String, String>) session.getAttribute("threeElements");
        String bankcardNum = map.get("bankcard_num");
        String idCard = map.get("id_card");
        String name = map.get("name");
        bankCardService.checkBankcard4Verity(bankcardNum, idCard, name, mobile, userId, code);
        //清除session
        session.removeAttribute("threeElements");
        return new Result(StatusCode.OK.getCode(), "绑定成功");
    }

    @PutMapping("/remove/binding")
    public Result removeBindingBankCard(@RequestBody @NotNull Map<String, String> body, HttpSession session) {
        try {
            String cardNum = body.get("cardNum");
            String userId = session.getAttribute("userId").toString();
            bankCardService.removeBindingBankCard(cardNum, userId);
            return new Result(StatusCode.OK.getCode(), "解除绑定");
        } catch (Exception e) {
            logger.error("解除绑定异常", e);
            e.printStackTrace();
            return new Result(StatusCode.ERROR.getCode(), "解除绑定失败");
        }
    }

    /**
     * 获取用户已绑定的银行卡的数量 未绑定/绑定数量
     */
    @GetMapping("/security/bankcard")
    public Result securitySet(HttpSession session) {
        try {
            String userId = session.getAttribute("userId").toString();
            int num = bankCardService.findAllBinding(userId).size();
            User user = userService.check(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("auth", 0);
            if (user != null) {
                result.put("auth", 1);
            }
            if (num == 0) {
                result.put("num", num);
                return new Result(StatusCode.OK.getCode(), "查询成功", result);
            } else {
                result.put("num", "已绑定" + num + "张");
                return new Result(StatusCode.OK.getCode(), "查询成功", result);
            }
        } catch (Exception e) {
            logger.error("银行卡安全异常", e);
            e.printStackTrace();
            return new Result(StatusCode.ERROR.getCode(), "银行卡安全异常");
        }
    }
}
