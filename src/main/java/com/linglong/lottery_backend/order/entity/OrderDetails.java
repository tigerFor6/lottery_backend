package com.linglong.lottery_backend.order.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "tbl_order_details")
@DynamicUpdate
@DynamicInsert
@Data
public class OrderDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "match_id")
    private Long matchId;

    @Column(name = "match_name")
    private String matchName;

    @Column(name = "issue")
    private String issue;

    @Column(name = "sn")
    private String sn;

    @Column(name = "plays_details")
    private String playsDetails;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

}
