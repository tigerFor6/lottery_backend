package com.linglong.lottery_backend.order.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.linglong.lottery_backend.lottery.match.entity.TblMatchOdds;
import com.linglong.lottery_backend.lottery.match.entity.TblSeasonTeam;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @Author: qihua.li
 * @since: 2019-04-03
 */
@Entity
@Table(name = "tbl_order")
@Data
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "game_type")
    private Integer gameType;
    @Column(name = "order_fee")
    private BigDecimal orderFee;
    @Column(name = "transaction_fee")
    private BigDecimal transactionFee;
    @Column(name = "refund_fee")
    private BigDecimal refundFee;
    @Column(name = "order_details")
    private String orderDetails;
    @Column(name = "order_status")
    private Integer orderStatus;

    @Column(name = "channel_no")
    private String channelNo;

    public static final int ORDER_STATUS_INIT = 0;//已下单
    public static final int ORDER_STATUS_CLOSE = 1; //已取消
    public static final int ORDER_STATUS_FINISHED = 2; //已完成
    public static final int ORDER_STATUS_FAILED = 3; //订单失败

    public static final int PAY_STATUS_PAID = 1;
    public static final int PAY_STATUS_NO_PAY = 0;
    public static final int PAY_STATUS_REFUNDING = 2;
    public static final int PAY_STATUS_REFUND = 3;
    public static final int PAY_STATUS_REFUND_PART = 4;

    public static final int SPLIT_STATUS_INIT = 0;
    public static final int SPLIT_STATUS_SPLIT = 1;

    public static final int BILL_STATUS_INIT = 0;
    public static final int BILL_STATUS_PART = 3;
    public static final int BILL_STATUS_PROCESSING  = 1;
    public static final int BILL_STATUS_SUCCESS = 2;
    public static final int BILL_STATUS_FAIL = -1;
    public static final int BILL_STATUS_FAIL_M = -2;

    @Column(name = "deadline")
    private Date deadline;

    @Column(name = "created_time")
    private Date createdTime;
    @Column(name = "updated_time")
    private Date updatedTime;
    @Version
    @Column(name = "version")
    private Integer version;
    @Column(name = "user_coupon_id")
    private Long userCouponId;
    @Column(name = "coupon_amount")
    private BigDecimal couponAmount;

    @Column(name = "origin")
    private Integer origin;

    // TODO: 2019-04-20
    //非数据库字段
    @Transient
    private Date endTime;
    @Transient
    private Integer betTimes;//注数
    @Transient
    private BigDecimal realAmount;//实际支付金额
    @Transient
    private BigDecimal bonus;//中奖金额

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JSONField(serialize = false)
    private List<OrderDetails> OrderDetailsList;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id",  referencedColumnName = "order_id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JSONField(serialize = false)
    private OrderDetailsInfo orderDetailsInfo;

}
