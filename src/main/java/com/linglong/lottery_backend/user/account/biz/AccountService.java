package com.linglong.lottery_backend.user.account.biz;

import com.google.common.hash.Hashing;
import com.linglong.lottery_backend.message.sms.SmsSend;
import com.linglong.lottery_backend.user.account.entity.TblUserBalance;
import com.linglong.lottery_backend.user.account.entity.User;
import com.linglong.lottery_backend.user.account.repository.TblUserBalanceRepository;
import com.linglong.lottery_backend.user.account.repository.UserRepository;
import com.linglong.lottery_backend.utils.IdWorker;
import com.linglong.lottery_backend.utils.SmsUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static com.linglong.lottery_backend.user.account.util.Captcha.genCaptachaCode;

/**
 * Description
 *
 * @author yixun.xing
 * @since 14 三月 2019
 */
@Service
public class AccountService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private StringRedisTemplate template;

	@Autowired
	private SmsUtil smsUtil;

	@Autowired
	private IdWorker idWorker;

	@Autowired
    private TblUserBalanceRepository userBalanceRepository;

	@Autowired
	private SmsSend smsSend;

	private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

	public User findByPhoneNumber(String phoneNumber) {
		return userRepository.findByPhone(phoneNumber);
	}

//	public static void main(String[] args) {
//		String key = Hashing.md5().hashBytes("13696414729".getBytes()).toString();
//	}
    @Transactional(rollbackFor = Exception.class)
    public String getCaptcha(String phoneNumber, String type, String ip) {
        String captchaCode = genCaptachaCode();
        logger.info("=====loginCode:{}=====", captchaCode);
        String key = Hashing.md5().hashBytes(phoneNumber.getBytes()).toString()+type;
        ValueOperations<String, String> ops = this.template.opsForValue();

		String value = ops.get(key);
		if (StringUtils.isEmpty(value)){
			ops.set(key, captchaCode, 300, TimeUnit.SECONDS);
			return smsUtil.send(phoneNumber, captchaCode);
		}else{
			Long time = this.template.getExpire(key,TimeUnit.SECONDS);
			if (time.intValue()>240){
				return null;
			}else {
				ops.set(key, captchaCode, 300, TimeUnit.SECONDS);
				return smsUtil.send(phoneNumber, captchaCode);
			}
		}
    }

	public BigDecimal getUserBalance(String phoneNumber){
		BigDecimal balance=new BigDecimal(0.0);
		User user=userRepository.findByPhone(phoneNumber);
		if(null!=user){
            TblUserBalance userBalance = userBalanceRepository.findByUserId(user.getUserId());
			if(null!=userBalance){
                balance = userBalance.getTotalBalance();
			}
		}
		return balance;
	}
}
