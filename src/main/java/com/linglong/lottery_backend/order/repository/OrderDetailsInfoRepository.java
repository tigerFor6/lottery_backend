package com.linglong.lottery_backend.order.repository;

import com.linglong.lottery_backend.order.entity.OrderDetailsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;


public interface OrderDetailsInfoRepository extends JpaRepository<OrderDetailsInfo, Long> {

    OrderDetailsInfo findByOrderId(Long orderId);

}
