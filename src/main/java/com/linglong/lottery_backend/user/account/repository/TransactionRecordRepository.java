package com.linglong.lottery_backend.user.account.repository;

import com.linglong.lottery_backend.user.account.entity.TransactionRecord;
import com.linglong.lottery_backend.user.cashier.entity.SubstituteBo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description
 *
 * @author yixun.xing
 * @since 22 三月 2019
 */
public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> ,JpaSpecificationExecutor<TransactionRecord> {

    TransactionRecord findByRecordNo(Long recordNo);

    TransactionRecord findByOrderId(Long orderId);

    //提现记录状态修改
    @Modifying
    @Transactional
    @Query(value = "update tbl_transaction_record set record_status =:record_status  " +
            " where record_no =:record_no", nativeQuery = true)
    Integer updateDraw(@Param("record_status") String record_status, @Param("record_no") Long record_no);

    //下单记录状态修改
    @Modifying
    @Transactional
    @Query(value = "update tbl_transaction_record set record_status =:record_status  " +
            " where order_id =:order_id", nativeQuery = true)
    Integer updateOrder(@Param("record_status") String record_status, @Param("order_id") Long order_id);

    @Query(value = "select * from tbl_transaction_record where 1 =1 and user_id=:user_id and record_status in ('2','3','4','8','15','10','6','16') order by id desc limit :pagenum,:page_size", nativeQuery = true)
    List<TransactionRecord> findRecordByUserID(@Param("user_id") Long user_id,@Param("pagenum") Integer pagenum,@Param("page_size") Integer page_size);

    //根据用户id获得数据
    @Query(value = "select * from tbl_transaction_record where 1 =1 and user_id=:user_id and record_status in ('2','3','4','8','15','10','6','16')", nativeQuery = true)
    List<TransactionRecord> countTransactionRecordsByUserId(@Param("user_id")String user_id);

    TransactionRecord findFirstByVoucherNo(Long voucherNo);

    TransactionRecord findByRecordNoAndThirdPartyRecordNo(Long recordNo, String thirdPartyRecordNo);

    @Query(value = " select record.record_no as recordNo,format(record.price/100,2) as amount,user.real_name as userName,record.created_time as createdTime,user.phone_number as phoneNumber,user.user_id as userId from tbl_transaction_record record,tbl_user_info user\n" +
            "where user.user_id=record.user_id and record.type=2 and record.record_status=3 and record.extend_info is not null order by record.created_time", nativeQuery = true)
    List<SubstituteBo> getSubstituteRecord();

    TransactionRecord findByOrderIdAndRecordStatus(Long orderId, String recordStatus);
}
