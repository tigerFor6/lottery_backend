package com.linglong.lottery_backend.user.cashier.repository;

import com.linglong.lottery_backend.user.cashier.entity.TblCashierAmount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TblCashierAmountRepository extends JpaRepository<TblCashierAmount, Long> {

    List<TblCashierAmount> findAllByStatus(Integer status);
}
