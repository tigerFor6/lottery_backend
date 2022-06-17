package com.linglong.lottery_backend.user.account.repository;


import com.linglong.lottery_backend.user.account.entity.TblUserBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public interface TblUserBalanceRepository extends JpaRepository<TblUserBalance, Long> {
    TblUserBalance findByUserId(String userId);

    @Modifying
    @Transactional
    @Query(value = "update tbl_user_balance set recharge_amount =:recharge_amount ," + "total_balance=:total_balance " +
            " where user_id =:userId", nativeQuery = true)
    Integer rechargeUpdate(@Param("recharge_amount") BigDecimal recharge, @Param("total_balance") BigDecimal total,
                           @Param("userId") String userId);

    @Modifying
    @Transactional
    @Query(value = "update tbl_user_balance set reward_amount =:reward_amount ," + "total_balance=:total_balance " +
            " where user_id =:userId", nativeQuery = true)
    Integer rewardUpdate(@Param("reward_amount") BigDecimal recharge, @Param("total_balance") BigDecimal total,
                         @Param("userId") String userId);

    @Modifying
    @Transactional
    @Query(value = "update tbl_user_balance set recharge_amount =:recharge_amount ,reward_amount=:reward_amount," +
            "total_balance=:total_balance ,version=version+1 where user_id =:userId and version=:version", nativeQuery = true)
    Integer deductionUpdate(@Param("recharge_amount") BigDecimal recharge, @Param("reward_amount") BigDecimal reward_amount,
                            @Param("total_balance") BigDecimal total, @Param("userId") String userId, @Param("version") Long version);


}
