package com.linglong.lottery_backend.order.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "tbl_order_details_info")
@DynamicUpdate
@DynamicInsert
@Data
public class OrderDetailsInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "bet_number")
    private Integer betNumber;

    @Column(name = "bill_status")
    private Integer billStatus;

    @Column(name = "pay_status")
    private Integer payStatus;

    @Column(name = "chuan_guan")
    private String chuanGuan;

    @Column(name = "chasing")
    private Integer chasing;

    @Column(name = "dead_line")
    private String deadLine;

    @Column(name = "multiple")
    private Integer multiple;

    @Column(name = "bonus")
    private BigDecimal bonus;

    @Column(name = "open_prize_status")
    private Integer openPrizeStatus;

    @Column(name = "hit_prize_status")
    private Integer hitPrizeStatus;

    @Column(name = "delivery_prize_status")
    private Integer deliveryPrizeStatus;

    @Column(name = "success_tickets")
    private Integer successTickets;

    @Column(name = "total_tickets")
    private Integer totalTickets;

    @Column(name = "split_status")
    private Integer splitStatus;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;


}
