package com.linglong.lottery_backend.user.cashier.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/7/26
 */
@Entity(name = "tbl_cashier_amount")
@DynamicUpdate
@DynamicInsert
@Data
public class TblCashierAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "status")
    private Integer status;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;

    @Version
    private Integer version;
}
