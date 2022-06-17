package com.linglong.lottery_backend.order.repository;

import com.linglong.lottery_backend.order.entity.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {

    List<OrderDetails> findByOrderId(Long orderId);
}
