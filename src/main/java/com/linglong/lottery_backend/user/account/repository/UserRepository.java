package com.linglong.lottery_backend.user.account.repository;

import com.linglong.lottery_backend.user.account.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Description
 *
 * @author yixun.xing
 * @since 14 三月 2019
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findByPhone(String phoneNumber);

    User findByRealnameAndIdCardAndStatus(String realName, String idCard, Integer status);

    User findByUserIdAndStatus(String userId, Integer status);

    User findByUserId(String userId);

    @Modifying
    @Query("update User set realname =?2,idCard=?3,status=?4 where userId=?1")
    void updateRealInfo(String userId, String realname, String idcard, Integer status);

    User findByPhoneAndPassword(String phoneNumber, String password);
}
