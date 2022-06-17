package com.linglong.lottery_backend.user.account.service;

import com.linglong.lottery_backend.user.account.entity.User;

/**
 * @Author: qihua.li
 * @since: 2019-04-25
 */
public interface IUserService {

    void updateRealInfo(String userId, String realname, String idcard);

    User check(String userId);

    User getUserInfo(String userId);

    User findByRealnameAndIdCard(String realName, String idCard);
}
