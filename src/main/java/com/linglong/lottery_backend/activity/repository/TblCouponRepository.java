package com.linglong.lottery_backend.activity.repository;


import com.linglong.lottery_backend.activity.entity.TblCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TblCouponRepository extends JpaRepository<TblCoupon, Long>{
    List<TblCoupon> findByCouponIdIn(Long[] toArray);
}
