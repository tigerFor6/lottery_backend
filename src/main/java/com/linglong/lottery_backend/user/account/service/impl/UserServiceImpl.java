package com.linglong.lottery_backend.user.account.service.impl;

import com.linglong.lottery_backend.user.account.entity.User;
import com.linglong.lottery_backend.user.account.repository.UserRepository;
import com.linglong.lottery_backend.user.account.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @Author: qihua.li
 * @since: 2019-04-25
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public void updateRealInfo(String userId, String realname, String idcard) {
        userRepository.updateRealInfo(userId, realname, idcard, 2);
    }

    @Override
    public User check(String userId) {
        return userRepository.findByUserIdAndStatus(userId, 2);
    }

    @Override
    public User getUserInfo(String userId) {
        return userRepository.findByUserIdAndStatus(userId, 2);
    }

    @Override
    public User findByRealnameAndIdCard(String realName, String idCard) {
        return userRepository.findByRealnameAndIdCardAndStatus(realName,idCard,2);
    }
}
