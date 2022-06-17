package com.linglong.lottery_backend.user.bankcard.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.linglong.lottery_backend.user.account.entity.User;
import com.linglong.lottery_backend.user.account.repository.UserRepository;
import com.linglong.lottery_backend.common.error.RepetitionBindingException;
import com.linglong.lottery_backend.user.bankcard.pojo.AccountInfo;
import com.linglong.lottery_backend.user.bankcard.repository.IAccountInfoRepository;
import com.linglong.lottery_backend.user.bankcard.service.IAccountInfoService;
import com.linglong.lottery_backend.common.error.*;
import com.linglong.lottery_backend.utils.BankCard4VerifyUtil;
import com.linglong.lottery_backend.utils.BankCardLocationUtil;
import com.linglong.lottery_backend.utils.IdWorker;
import com.linglong.lottery_backend.utils.SmsUtil;
import com.linglong.lottery_backend.utils.*;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: qihua.li
 * @since: 2019-03-20
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountInfoImpl implements IAccountInfoService {

    private static final Logger logger = LoggerFactory.getLogger(AccountInfoImpl.class);
    private final IAccountInfoRepository bankCardRepository;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final SmsUtil smsUtil;
    private final BankCard4VerifyUtil bankCard4VerifyUtil;
    private final BankCard2VerifyUtil bankCard2VerifyUtil;
    private final BankCardLocationUtil bankCardLocationUtil;
    private final IdWorker idWorker;

    @Override
    public List<AccountInfo> findAllBinding(String userId) {
        /*AccountInfo accountInfo = new AccountInfo();
        accountInfo.setUserId(userId);
        accountInfo.setCardStatus(1);*/
        //userId 和 status
        return bankCardRepository.findByUserIdAndCardStatus(userId, 1);
    }

    @Override
    public List<AccountInfo> findBindingPage(String userId, int page) {
        final int size = 5;
        //需要分页 userId status
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<AccountInfo> pages = bankCardRepository.findByUserIdAndCardStatus(userId, 1, pageRequest);
        return pages.getContent();
    }

    @Override
    public AccountInfo findBankCardDetail(Long id, String userId) {
        return bankCardRepository.findByIdAndUserId(id, userId);
    }

    @Override
    public void removeBindingBankCard(String cardNum, String userId) {
        removeBinding(cardNum, userId);
    }

    //手机号错误的提示
    @Override
    public void checkBankcard4Verity(String bankcardNum, String idCard, String name, String mobile, String userId, String code) throws Exception {
        //获取验证码
        if (StringUtils.isEmpty(code)) {
            throw new NotCaptchaException("请输入验证码");
        }
        //验证
        String redisCode = redisTemplate.opsForValue().get(mobile);
        logger.info("redisCode={}", redisCode);
        if (!code.equals(redisCode)) {
            logger.error("=={}==", code.equals(redisCode));
            throw new IllegalCaptchaException("验证码不正确，请重新输入");
        }
        //验证基本信息
        JsonNode jsonNodeVerify = bankCard4VerifyUtil.verify(bankcardNum, name, idCard, mobile);
        String verifyCode = jsonNodeVerify.findValue("code").asText();
        if ("0".equals(verifyCode)) {
            if ("1".equals(jsonNodeVerify.findValue("res").asText())) {
                //如果是解绑的，修改绑定状态
                AccountInfo accountInfo = new AccountInfo();
                accountInfo.setUserId(userId);
                accountInfo.setBankCardNum(bankcardNum);
                accountInfo.setReservedPhone(mobile);
                accountInfo.setCardStatus(3);
                Optional<AccountInfo> optional = bankCardRepository.findOne(Example.of(accountInfo));
                if (optional.isPresent()) {
                    logger.info("更改解绑的状态为绑定");
                    AccountInfo info = optional.get();
                    info.setCardStatus(1);
                    bankCardRepository.save(info);
                } else {
                    //存手机号
                    logger.info("绑定手机号：{}" + mobile);
                    bindingMobile(userId, bankcardNum, mobile);
                }
            } else {
                logger.error(jsonNodeVerify.findValue("description").asText());
                throw new IllegalArgumentException("预留手机号码错误");
            }
        }
    }

    @Override
    public Boolean checkPhoneIsBinding(String phone, String userId) {
        AccountInfo accountInfo = bankCardRepository.findTopByUserIdOrderByCreatedTimeAsc(userId);
        if (accountInfo != null) {
            String reservedPhone = accountInfo.getReservedPhone();
            logger.info("reservedPhone:{}", reservedPhone);
            if (StringUtils.isEmpty(reservedPhone)) {
                return true;
            }
            //throw new IllegalPhoneExcetion("只能使用第一次绑定银行卡的手机号进行绑定");
            return phone.equals(reservedPhone);
        }
        return false;
    }

    @Override
    public void checkIdCard(String idCard, String userId) {
        User user = userRepository.findByUserId(userId);
        if (user != null) {
            String dbIdCard = user.getIdCard();
            if (!StringUtils.isEmpty(dbIdCard) && !idCard.equals(user.getIdCard())) {
                throw new IdCardDifferencException("请使用本人身份证号码进行绑定");
            }
        }
    }

    @Override
    public void checkBankcardNumIsDuplicate(String userId, String bankcardNum) {
        AccountInfo accountInfo = new AccountInfo();
//        accountInfo.setUserId(userId);
        accountInfo.setBankCardNum(bankcardNum);
        accountInfo.setCardStatus(1);
        Optional<AccountInfo> optional = bankCardRepository.findOne(Example.of(accountInfo));
        if (optional.isPresent()) {
            logger.info("是否重复绑定了卡{}", optional.get());
            throw new RepetitionBindingException("此银行卡已被绑定，请更换银行卡");
        }
    }

    @Override
    public void deleteBankcardStatusIs2(String userId, String bankcardNum) {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setCardStatus(2);
        accountInfo.setBankCardNum(bankcardNum);
        accountInfo.setUserId(userId);
        List<AccountInfo> all = bankCardRepository.findAll(Example.of(accountInfo));
        if (!all.isEmpty()) {
            bankCardRepository.deleteAll(all);
        }
    }

    @Override
    public AccountInfo getAccountInfoById(long id) {
        Optional<AccountInfo> accountInfo=bankCardRepository.findById(id);
        return accountInfo.get();
    }

    /**
     * map{flag,message,bankname,phone}
     */
    @Override
    public Map<String, Object> checkBankcardVerity(String bankcardNum, String idCard, String name, String userId) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("flag", false);
        JsonNode jsonNode2Element = bankCard2VerifyUtil.verify(name, bankcardNum);
        if (jsonNode2Element == null) {
            throw new IllegalBankcardInfoException("服务失效");
        }
        String retCode = jsonNode2Element.findValue("ret_code").asText();
        if ("0".equals(retCode)) {
            String code = jsonNode2Element.findValue("code").asText();
            if ("0".equals(code)) {
                //2要素鉴别成功,可以返回归属地,和卡类型
                String bankName = jsonNode2Element.findValue("bankName").asText();
                String cardType = jsonNode2Element.findValue("cardType").asText();
                // 鉴别3要素
                return bankcard3Verify(bankcardNum, idCard, name, userId, map, bankName, cardType);
            } else if ("14".equals(code)) {
                //无效卡号
                map.put("message", "无效卡号");
                map.put("cause", 1);
            } else if ("5".equals(code)) {
                //姓名错误
                map.put("message", "卡号或姓名输入有误");
                map.put("cause", 2);
            }
        } else {
            map.put("message", "服务错误");
            map.put("cause", 0);
        }
        return map;
    }

    @Override
    public Map<String, Object> preFindAccountInfo(String userId) {
//        int[] status = {1, 3};
//        PageRequest pageRequest = PageRequest.of(0, 1);
//        Page<AccountInfo> page = bankCardRepository.findTopByUserIdAndCardStatusIn(userId, status, pageRequest);
//        List<AccountInfo> list = page.getContent();
//        if (!list.isEmpty()) {
//            User user = userRepository.findByUserId(userId);
//            Map<String, Object> map = new HashMap<>();
//            map.put("id_card", user.getIdCard());
//            map.put("real_name", user.getRealname());
//            return map;
//        }
        User user = userRepository.findByUserId(userId);
        Map<String, Object> map = Maps.newHashMap();
        if(Strings.isNotBlank(user.getIdCard()) && Strings.isNotBlank(user.getRealname()) && user.getStatus() == 2)
        {
            map.put("id_card", user.getIdCard());
            map.put("real_name", user.getRealname());
        }
        return map;
    }

    private Map<String, Object> bankcard3Verify(String bankcardNum, String idCard, String name, String userId, Map<String, Object> map, String bankName, String cardType) throws Exception {
        JsonNode jsonNode4Element = bankCard4VerifyUtil.verify(bankcardNum, name, idCard);
        if (jsonNode4Element == null) {
            throw new IllegalIdCardException("身份证号码有误");
        }
        logger.info(jsonNode4Element.asText());
        String verifyCode = jsonNode4Element.findValue("code").asText();
        if ("0".equals(verifyCode)) {
            logger.info("veriftyCode={}" + verifyCode);
            String res = jsonNode4Element.findValue("res").asText();
            if ("1".equals(res)) {
                //res信息一致
                map.put("flag", true);
                map.put("message", "认证信息匹配");
                //是否是绑定过的用户(绑定和解绑)
                map.put("isBound", false);
                PageRequest pageRequest = PageRequest.of(0, 1);
                int[] status = {1, 3};
                Page<AccountInfo> page = bankCardRepository.findTopByUserIdAndBankCardNumAndCardStatusInOrderByCreatedTimeAsc(userId, bankcardNum, status, pageRequest);
                List<AccountInfo> content = page.getContent();
                logger.info("是否绑定过:{}", !content.isEmpty());
                if (!content.isEmpty()) {
                    AccountInfo isBound = content.get(0);
                    map.put("isBound", true);
                    map.put("bankName", bankName);
                    map.put("phone", isBound.getReservedPhone());
                    return map;
                }
                //认证信息匹配--写入数据库
                updatedUserData(idCard, userId, name);
                bindingBankLocation(bankName, cardType, userId, bankcardNum);
                map.put("bankName", bankName);
                //通过userId从数据库里查出已绑定的手机号
                AccountInfo accountInfo = bankCardRepository.findTopByUserIdOrderByCreatedTimeAsc(userId);
                logger.info("accountInfo={}", accountInfo);
                if (accountInfo != null) {
                    logger.info("预留手机号:{}", accountInfo.getReservedPhone());
                    String reservedPhone = accountInfo.getReservedPhone();
                    if (!StringUtils.isEmpty(reservedPhone)) {
                        //回显开户银行和预留手机号
                        map.put("phone", reservedPhone);
                    }
                }
                return map;
            }
            if (jsonNode4Element.findValue("description").asText().contains("交易次数超限")){
                map.put("message", "银行系统提示今日验证次数已超限，请24小时后重试！");
            }else{
                map.put("message", jsonNode4Element.findValue("description").asText());
            }
        } else {
            map.put("message", "出现异常错误");
        }
        return map;
    }

    @Override
    public String sendSms(String phone, String userId) {
        AccountInfo accountInfo = bankCardRepository.findTopByUserIdOrderByCreatedTimeAsc(userId);
        if (accountInfo != null) {
            String reservedPhone = accountInfo.getReservedPhone();
            if (!StringUtils.isEmpty(reservedPhone)) {
                if (!reservedPhone.equals(phone)) {
                    return "输入第一次绑定卡时预留的手机号";
                }
            }
        }
        Random random = new Random();
        int code = random.nextInt(999999);
        if (code < 100000) {
            code += 100000;
        }
        logger.info("code:{}" + code);
        try {
            return smsUtil.send(phone, Integer.toString(code));
        } finally {
            redisTemplate.opsForValue().set(phone, Integer.toString(code), 5, TimeUnit.MINUTES);
        }
    }

    /**
     * 如果没有绑定就绑定，
     * 如果已解绑或者校验中改变状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindingMobile(String userId, String bankcardNum, String mobile) {
        User user = userRepository.findByUserId(userId);
        AccountInfo accountInfo = bankCardRepository.findByUserIdAndBankCardNumAndCardStatus(userId, bankcardNum, 2);
        if (null != user){
            user.setStatus(2);
            user.setUpdatedTime(new Date());
            userRepository.save(user);
        }
        if (null != accountInfo) {
            accountInfo.setCardStatus(1);
            accountInfo.setReservedPhone(mobile);
            accountInfo.setUpdatedTime(new Date());
            bankCardRepository.save(accountInfo);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void removeBinding(String cardNum, String userId) {
        //userId cardNum
        //AccountInfo accountInfo = bankCardRepository.findByUserIdAndBankCardNumAndCardStatus(userId, cardNum);
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setCardStatus(1);
        accountInfo.setUserId(userId);
        accountInfo.setBankCardNum(cardNum);
        Optional<AccountInfo> optional = bankCardRepository.findOne(Example.of(accountInfo));
        if (optional.isPresent()) {
            AccountInfo info = optional.get();
            info.setCardStatus(3);
            info.setUpdatedTime(new Date());
            bankCardRepository.save(info);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void bindingBankLocation(String bankName, String cardType, String userId, String bankcardNum) {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setId(idWorker.nextId());
        accountInfo.setUserId(userId);
        accountInfo.setBankCardNum(bankcardNum);
        accountInfo.setAffiliatedBank(bankName);
        if (cardType.contains("借记卡")) {
            accountInfo.setCardType(1);
        } else if (cardType.contains("信用卡")) {
            throw new IllegalStateException("请绑定借记卡，暂不支持信用卡");
        } else {
            logger.info("仅支持借记卡和信用卡,请您更换卡种");
            throw new IllegalArgumentException("请绑定借记卡,暂不支持其他卡种");
        }
        accountInfo.setCardStatus(2);
        accountInfo.setCreatedTime(new Date());
        accountInfo.setUpdatedTime(new Date());
        bankCardRepository.save(accountInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updatedUserData(String idCard, String userId, String name) {
        //userId
        User user = userRepository.findByUserId(userId);
        if (user != null) {
            user.setIdCard(idCard);
            user.setRealname(name);
            user.setStatus(1);
            user.setUpdatedTime(new Date());
            userRepository.save(user);
        }
    }
}
