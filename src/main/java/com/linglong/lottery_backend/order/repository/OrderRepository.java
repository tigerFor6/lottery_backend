package com.linglong.lottery_backend.order.repository;

import com.linglong.lottery_backend.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Author: qihua.li
 * @since: 2019-04-03
 */
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @Query(value = "select * from tbl_order where user_id = :userId and game_type in :lotteryTypes and created_time between DATE_SUB(now(),INTERVAL 3 MONTH) and now() order by created_time desc",
            nativeQuery = true)
    Page<Order> findByThreeMonthsOrder(@Param("userId") String userId, @Param("lotteryTypes") List<Long> lotteryTypes, Pageable pageable);

    Order findByOrderId(Long orderId);

    Order findByOrderIdAndUserId(Long orderId, String userId);

    List<Order> findByOrderIdInOrderByOrderIdAsc(List<Long> ids);

    List<Order> findByOrderIdIn(List<Long> ids);

//    Order getOrderByOrderId(Long orderId, Boolean ifLock);

    @Modifying
    @Transactional
    //@Query("update tbl_order order set order.order_status=1 where order.deadline < CURRENT_TIMESTAMP ")
    @Query(value = "update tbl_order set order_status=1 where order_status!=2 and deadline < now()  ", nativeQuery = true)
    void updateInvaidOrder();

    /**
     * @description: 运营后台专用，用于再给异常订单退款之后，将该异常订单的状态改为1（已取消）
     * @author: ZhiYao.Zhang
     * @param:
     * @return:
     * @create:  2019-06-06
     **/

    @Modifying
    @Transactional
    @Query(value = "update tbl_order set order_status=1 where order_status!=2 and order_id = :orderId  ", nativeQuery = true)
    void updateStatus(@Param("orderId") String orderId);

    List<Order> findByUserId(String userId);

    @Query(value = "SELECT * FROM tbl_order WHERE order_details <> '0'", nativeQuery = true)
    List<Order> findByOrderDetailsIsNot0();

    @Query(value = "select * from tbl_order where user_id = :userId and game_type in :lotteryTypes and created_time between DATE_SUB(now(),INTERVAL 5 DAY ) and now() order by created_time desc",
            nativeQuery = true)
    List<Order> findByFiveDaysOrder(@Param("userId") String userId, @Param("lotteryTypes") List<Long> lotteryTypes);
}
