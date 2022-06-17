package com.linglong.lottery_backend.ticket.entity;


import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.entity.bo.TicketNumber;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "tbl_betting_ticket")
@DynamicUpdate
@DynamicInsert
@Data
//@IdClass(BettingTicketKey.class)
public class BettingTicket implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(name = "period")
    private int period;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "game_type")
    private Integer gameType;

    @Column(name = "platform_id")
    private Integer platformId;

    @Column(name = "ticket_status")
    private Integer ticketStatus;

    public static final int TICKET_STATUS_INIT = -3;//初始化
//    public static final int TICKET_STATUS_BETTING = 1;//投注中
//    public static final int TICKET_STATUS_FAIL_REFUNDING = 2;//投注失败退款中
//    public static final int TICKET_STATUS_REFUND = 3;//投注失败已退款
//    public static final int TICKET_STATUS_BET_SUCCESS = 4;//出票成功
//    public static final int TICKET_STATUS_BET_SEND_PRIZE_SUCCESS = 5;//发送到返奖系统

    @Column(name = "ticket_amount")
    private BigDecimal ticketAmount;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "times")
    private Integer times;

    @Column(name = "extra")
    private String extra;

    @Column(name = "lottery_number")
    private String lotteryNumber;

    @Column(name = "ticket_no")
    private String ticketNo;

    @Column(name = "bonus")
    private BigDecimal bonus;

    @Column(name = "prize_status")
    private Integer prizeStatus;

    @Column(name = "open_prize_status")
    private Integer openPrizeStatus;

    @Column(name = "return_prize_status")
    private Integer returnPrizeStatus;

    @Column(name = "big_prize_status")
    private Boolean bigPrizeStatus = false;

    public static final Integer PRIZE_STATUS_NO_OPEN = 0;

    @Column(name = "create_time")
    private java.util.Date createTime;

    @Column(name = "updated_time")
    private java.util.Date updatedTime;

    @Column(name = "issue_time")
    private java.util.Date issueTime;

    @Column(name = "bonus_time")
    private java.util.Date bonusTime;

    @Column(name = "play_type")
    private String playType;

    @Version
    @Column(name = "version")
    private Integer version;

//    @OneToMany(fetch = FetchType.EAGER)
//    @JoinColumn(name = "betting_ticket_id", referencedColumnName = "ticket_id", insertable = false, updatable = false)
//    @NotFound(action = NotFoundAction.IGNORE)
//    private List<TblBettingTicketDetails> bettingTicketDetails;
//
//    @OneToMany(fetch = FetchType.EAGER)
//    @JoinColumn(name = "betting_ticket_id", referencedColumnName = "ticket_id", insertable = false, updatable = false)
//    @NotFound(action = NotFoundAction.IGNORE)
//    private List<TblBettingTicketGroup> bettingTicketGroups;

    public BettingTicket() {
    }

    public BettingTicket(Long ticketId, Long userId, Long orderId, Integer gameType, Integer platformId,
                         Integer ticketStatus, Integer times, String playType, String extra, Integer prizeStatus, Timestamp createTime,int period) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.orderId = orderId;
        this.gameType = gameType;
        this.platformId = platformId;
        this.ticketStatus = ticketStatus;
        this.times = times;
        this.playType = playType;
        this.extra = extra;
        this.prizeStatus = prizeStatus;
        this.createTime = createTime;
        this.period = period;
    }

//    public BettingTicket(Long ticketId, BigDecimal bonus, Integer prizeStatus) {
//        this.ticketId = ticketId;
//        this.bonus = bonus;
//        this.prizeStatus = prizeStatus;
//    }

    public List<TicketNumber> getTicketNumbers() {
        String[] ticketNumbers = lotteryNumber.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant
                .COMMON_DOLLAR_STR);
        List<TicketNumber> numbers = new ArrayList<>();
        for (String ticketNumber : ticketNumbers) {
            String[] tns = ticketNumber.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_AT_STR);
            numbers.add(new TicketNumber(tns[0], Integer.parseInt(tns[1]), playType, extra, times,period));
        }
        return numbers;
    }
}
