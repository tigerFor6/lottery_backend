package com.linglong.lottery_backend.activity.repository;

import com.linglong.lottery_backend.activity.entity.TblUserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TblUserCouponRepository extends JpaRepository<TblUserCoupon, Long>, JpaSpecificationExecutor<TblUserCoupon> {
    Integer countByCouponId(Long couponId);

    Integer countByCouponIdAndUserId(Long couponId, Long userId);

    @Query(value = "SELECT COUNT(id) FROM tbl_user_coupon " +
            "WHERE NOW() BETWEEN start_time AND end_time AND (NOW() + interval 24 hour) > end_time " +
            "AND coupon_status = 0 AND user_id =:userId", nativeQuery = true)
    int countExpireByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT id, user_coupon_id, user_id, activity_id, coupon_id, coupon_type, " +
            "coupon_name, coupon_amount, coupon_rules, coupon_status, start_time, " +
            "end_time, version FROM tbl_user_coupon WHERE NOW() BETWEEN start_time AND end_time " +
            "AND coupon_status = 0 AND user_id =:userId", nativeQuery = true)
    List<TblUserCoupon> findAllowedToUseCoupon(@Param("userId") Long userId);

    TblUserCoupon findFirstByUserCouponId(Long userCouponId);

    int countByUserIdAndCouponStatus(Long userId, Integer status);


    @Modifying
    @Transactional
    @Query(value = "update tbl_user_coupon set coupon_status = 2 where coupon_status = 0  and end_time < now()  ", nativeQuery = true)
    void updateInvalidCoupon();

    List<TblUserCoupon> findByActivityIdAndUserId(Long activityId, Long userId);

    @Query(value = "SELECT id, user_coupon_id, user_id, activity_id, coupon_id, coupon_type, " +
            "coupon_name, coupon_amount, coupon_rules, coupon_status, start_time, " +
            "end_time, version FROM tbl_user_coupon " +
            "WHERE NOW() BETWEEN start_time AND end_time AND (NOW() + interval 24 hour) > end_time " +
            "AND coupon_status = 0 ", nativeQuery = true)
    List<TblUserCoupon> findExpireCoupon();
}
