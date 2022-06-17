package com.linglong.lottery_backend.user.account.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class RecordListRepository {

    @PersistenceContext //注入的是实体管理器,执行持久化操作
    private EntityManager entityManager;

    //用于拼接表名
    //private String tableTime= DateFormatUtils.format(new Date(), "yyyyMM");

    /**
     * @description: 用户动态查询记录表中的记录
     * @author: ZhiYao.Zhang
     * @param: String 拼接表名的时间戳  Long 用户id
     * @return:
     * @create: 2019-04-17
     **/

    public ArrayList<Object[]> findRecordByUserID(List<String> times, Long userId) {

        String sql = "(";

        for (int a = 0; a < times.size(); a++) {
            if (a < times.size() - 1) {
                sql += "select user_id,order_id,record_no,price,balance,type,record_status,record_create_time from tbl_transaction_record_" + times.get(a) + " where 1 =1 and user_id=" + userId + " order by record_create_time desc" + ") union (";
            } else {
                sql += "select user_id,order_id,record_no,price,balance,type,record_status,record_create_time from tbl_transaction_record_" + times.get(a) + " where 1 =1 and user_id=" + userId + " order by record_create_time desc)";
            }

        }

        return (ArrayList<Object[]>) entityManager.createNativeQuery(sql).getResultList();
    }

    /**
     * @description: 下单记录状态修改
     * @author: ZhiYao.Zhang
     * @param:
     * @return:
     * @create: 2019-04-18
     **/

    @Transactional
    @Modifying
    public void updateOrder(String record_status, Long order_id) {
        String updateSql = "update tbl_transaction_record  set record_status=" + record_status + " where 1=1 and order_id=" + order_id;
        entityManager.createNativeQuery(updateSql).executeUpdate();
    }

    /**
     * @description: 提现记录状态修改
     * @author: ZhiYao.Zhang
     * @param:
     * @return:
     * @create: 2019-04-18
     **/

    @Transactional
    @Modifying
    public void updateDraw(String record_status, Long record_no) {
        String updateSql = "update tbl_transaction_record  set record_status=" + record_status + " where 1=1 and record_no=" + record_no;
        entityManager.createNativeQuery(updateSql).executeUpdate();
    }

    /**
     * @description: 修改充值记录
     * @author: ZhiYao.Zhang
     * @param:
     * @return:
     * @create: 2019-04-30
     **/

    @Transactional
    @Modifying
    public Integer updateRecharge(BigDecimal amount, String recordNo, String recordStatus) {
        String updateSql = "update tbl_transaction_record  set record_status=" + recordStatus + ",price="+ amount + " where 1=1 and record_no=" + recordNo;
        return  entityManager.createNativeQuery(updateSql).executeUpdate();
    }

    /**
     * @description: 返回凭证号
     * @author: ZhiYao.Zhang
     * @param:
     * @return:
     * @create: 2019-04-30
     **/

    public ArrayList<Object[]> findRecordByVoucherNo(List<String> times, Long voucherNo) {

        String sql = "(";

        for (int a = 0; a < times.size(); a++) {
            if (a < times.size() - 1) {
                sql += "select * from tbl_transaction_record_" + times.get(a) + " where 1 =1 and voucher_no=" + voucherNo + ") union (";
            } else {
                sql += "select * from tbl_transaction_record_" + times.get(a) + " where 1 =1 and voucher_no=" + voucherNo + " )";
            }

        }

        return (ArrayList<Object[]>) entityManager.createNativeQuery(sql).getResultList();
    }

}
