package com.linglong.lottery_backend.user.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import com.linglong.lottery_backend.common.bean.LockKey;
import com.linglong.lottery_backend.user.account.biz.AccountService;
import com.linglong.lottery_backend.user.account.entity.TblUserBalance;
import com.linglong.lottery_backend.user.account.entity.User;
import com.linglong.lottery_backend.user.account.repository.TblUserBalanceRepository;
import com.linglong.lottery_backend.user.account.repository.TransactionRecordRepository;
import com.linglong.lottery_backend.user.account.repository.UserRepository;
import com.linglong.lottery_backend.user.account.service.IUserService;
import com.linglong.lottery_backend.order.model.Result;
import com.linglong.lottery_backend.order.model.StatusCode;
import com.linglong.lottery_backend.utils.IdWorker;
import com.linglong.lottery_backend.utils.MD5Util;
import com.linglong.lottery_backend.utils.RealNameAuthenticationUtil;
import com.linglong.redisson.configure.annotations.LockAction;
import com.linglong.redisson.configure.util.LockType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Description
 *
 * @author yixun.xing
 * @since 14 三月 2019
 */
@RestController
@RequestMapping(value = "/api/account")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final AccountService accountService;
    private final RealNameAuthenticationUtil realNameAuthenticationUtil;
    private final TransactionRecordRepository repository;
    private final IUserService userService;
    private final IdWorker idWorker;
    private final UserRepository userRepository;
    private final TblUserBalanceRepository userBalanceRepository;
    private final StringRedisTemplate template;

    @Value("${message.success}")
    private String successMsg;

    @Value("${message.sendSmsFailed}")
    private String sendSmsFailed;

    @RequestMapping("getCaptcha")
    public Result getCaptcha(@RequestParam("phoneNumber") final String phoneNumber, @RequestParam("type") final String type, HttpServletRequest request) {
        checkArgument(Strings.isNotBlank(phoneNumber), "手机号不能为空");
        String resultJson = accountService.getCaptcha(phoneNumber, type, request.getRemoteAddr());
        if (null == resultJson){
            return new Result(StatusCode.ERROR.getCode(), "请勿重复发送");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String errorCode = null;
        try {
            errorCode = objectMapper.readTree(resultJson).findPath("error_code").toString();
            //final GenericResponse bodyOfResponse = new GenericResponse(!"0".equals(errorCode) ? successMsg : sendSmsFailed, "");
            //return ResponseEntity.ok().body(result);
            return new Result(StatusCode.OK.getCode(), !"0".equals(errorCode) ? successMsg : sendSmsFailed);
        } catch (Exception e) {
            //final GenericResponse bodyOfResponse = new GenericResponse(sendSmsFailed, "");
            return new Result(StatusCode.ERROR.getCode(), sendSmsFailed);
        }
    }
    @RequestMapping("checkUser")
    public Result checkUser(@RequestParam("phoneNumber") final String phoneNumber, HttpServletRequest request) {
        checkArgument(Strings.isNotBlank(phoneNumber), "手机号不能为空");
        User user = userRepository.findByPhone(phoneNumber);
        if (null == user){
            return new Result(StatusCode.OK.getCode(), "您还没在此平台注册",0);
        }else {
            return new Result(StatusCode.OK.getCode(), "您已在此平台注册",1);
        }
    }

    @PostMapping("/auth")
    public Result authenticate(@RequestParam String realname, @RequestParam String idcard, @SessionAttribute String userId) throws Exception {
        User user = userService.check(userId);
        if (user != null) {
            return new Result(StatusCode.ACCESS_ERROR.getCode(), "您已通过实名认证,无需再次认证");
        }
        user = userService.findByRealnameAndIdCard(realname,idcard);
        if (user != null) {
            return new Result(StatusCode.ACCESS_ERROR.getCode(), "此实名信息已被认证，请确定提交信息是否正确");
        }
        Map<String, Object> map = realNameAuthenticationUtil.verify(idcard, realname);
        Boolean flag = (Boolean) map.get("flag");
        String message = String.valueOf(map.get("message"));
        if (flag) {
            userService.updateRealInfo(userId, realname, idcard);
            return new Result(StatusCode.OK.getCode(), message);
        }
        return new Result(StatusCode.CHECK_ERROR.getCode(), message);
    }

    @GetMapping("/userInfo")
    public Result getUserInfo(@SessionAttribute String userId) {
        User userInfo = userService.getUserInfo(userId);
        if (userInfo == null) {
            return new Result(StatusCode.ACCESS_ERROR.getCode(), "请先进行实名认证");
        }
        Map<String, Object> map = new HashMap<>();
        String idCard=userInfo.getIdCard();
        idCard=idCard.replaceAll("(\\d{4})\\d{10}(\\d{4})","$1****$2");

        String phone=userInfo.getPhone();
        phone=phone.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");

        map.put("realname", userInfo.getRealname());
        map.put("idcard", idCard);
        map.put("phone", phone);
        return new Result(StatusCode.OK.getCode(), "查询成功", map);
    }

    @PostMapping("/register")
    public Result register(@RequestParam String mobile, @RequestParam String type, @RequestParam String inputCode, @RequestParam String passWord) throws Exception {
        User user = userRepository.findByPhone(mobile);
        String userid = String.valueOf(idWorker.nextId());
        String key = Hashing.md5().hashBytes(mobile.getBytes()).toString()+type;
        String smsCode = template.opsForValue().get(key);
        if(smsCode == null) {
            return new Result(StatusCode.ACCESS_ERROR.getCode(), "验证码不存在");
        }else if (!StringUtils.equals(inputCode, smsCode)) {
            return new Result(StatusCode.ACCESS_ERROR.getCode(), "验证码错误");
        }else if (StringUtils.equals(inputCode, smsCode)) {
            if (null == user){
                user = new User();
                user.setUserId(userid);
                user.setPhone(mobile);
                passWord = MD5Util.getMD5String(passWord);
                user.setPassword(passWord);
                userRepository.save(user);

                TblUserBalance userBalance = new TblUserBalance();
                userBalance.setUserId(userid);
                userBalanceRepository.save(userBalance);
                template.delete(key);
                return new Result(StatusCode.OK.getCode(), "注册成功");
            }else{
                return new Result(StatusCode.ACCESS_ERROR.getCode(), "您的账号已存在");
            }
        }
        return new Result(StatusCode.CHECK_ERROR.getCode(), "");
    }

    @PostMapping("/modifyPassword")
    public Result modifyPassword(@RequestParam String mobile, @RequestParam String type, @RequestParam String inputCode, @RequestParam String passWord) throws Exception {
        User user = userRepository.findByPhone(mobile);
        String key = Hashing.md5().hashBytes(mobile.getBytes()).toString()+type;
        String smsCode = template.opsForValue().get(key);
        if(smsCode == null) {
            return new Result(StatusCode.ACCESS_ERROR.getCode(), "请输入验证码");
        }else if (!StringUtils.equals(inputCode, smsCode)) {
            return new Result(StatusCode.ACCESS_ERROR.getCode(), "您输入的验证码错误");
        }else if (StringUtils.equals(inputCode, smsCode)) {
            if (null == user){
                return new Result(StatusCode.ACCESS_ERROR.getCode(), "您的账号不存在");
            }else if(vertifyOne(passWord)){
                return new Result(StatusCode.ACCESS_ERROR.getCode(), "密码在需要设置为6-18位");
            }else if(vertifyTwo(passWord)){
                return new Result(StatusCode.ACCESS_ERROR.getCode(), "密码在需要设置为英文加数字");
            } else{
                passWord = MD5Util.getMD5String(passWord);
                user.setPassword(passWord);
                user.setUpdatedTime(new Date());
                userRepository.save(user);
            }
        }
        template.delete(key);
        return new Result(StatusCode.OK.getCode(), "");
    }

    public Boolean vertifyOne(String passWord){
        int len = passWord.length();
        if (len > 18 || len < 6){
            return true;
        }
        return false;
    }
    public Boolean vertifyTwo(String passWord){
        //【全为英文】返回true  否则false,//【全为数字】返回true
        if(passWord.matches("[a-zA-Z]+") || passWord.matches("[0-9]+")){
            return true;
        }
        return false;
    }
}
