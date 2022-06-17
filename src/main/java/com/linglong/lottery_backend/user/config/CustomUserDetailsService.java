package com.linglong.lottery_backend.user.config;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.linglong.lottery_backend.user.account.entity.TblUserBalance;
import com.linglong.lottery_backend.user.account.entity.User;
import com.linglong.lottery_backend.user.account.repository.TblUserBalanceRepository;
import com.linglong.lottery_backend.user.account.repository.UserRepository;
import com.linglong.lottery_backend.common.error.UserNotFoundException;
import com.linglong.lottery_backend.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description
 *
 * @author yixun.xing
 * @since 17 三月 2019
 */
@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate template;

    @Autowired
    private TblUserBalanceRepository userBalanceRepository;

    @Autowired
    private IdWorker idWorker;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {

        User user = userRepository.findByPhone(phoneNumber);

        if (null == user) {
            throw new UserNotFoundException();
        }
        String password = user.getPassword();
        return new org.springframework.security.core.userdetails.User(phoneNumber, password, true, true, true, true, Lists.newArrayList());

    }

    public UserDetails loadUserByMobile(String mobile) {
        User user = userRepository.findByPhone(mobile);
        String userid = String.valueOf(idWorker.nextId());
        if (null == user) {
            user = new User();
            user.setUserId(userid);
            user.setPhone(mobile);
            userRepository.save(user);

            TblUserBalance userBalance = new TblUserBalance();
            userBalance.setUserId(userid);
            userBalanceRepository.save(userBalance);
        }
        return new org.springframework.security.core.userdetails.User(mobile, mobile, true, true, true, true, Lists.newArrayList());
    }
}
