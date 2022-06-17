package com.linglong.lottery_backend.prize.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "tbl_betting_ticket_digital")
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
@NoArgsConstructor
public class BettingTicketDigital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "ticket_digital_id")
    private Long ticketDigitalId;
    @Column(name = "betting_ticket_id")
    private Long bettingTicketId;
    @Column(name = "period")
    private String period;
    @Column(name = "betting_type")
    private Integer bettingType;
    @Column(name = "play_type")
    private String playType;
    @Column(name = "betting_group")
    private String bettingGroup;
    @Column(name = "betting_result")
    private Integer bettingResult;
    @Column(name = "level")
    private Integer level;
    @Column(name = "pre_tax_bonus")
    private BigDecimal preTaxBonus;
    @Column(name = "aft_tax_bonus")
    private BigDecimal aftTaxBonus;
    @Column(name = "multiple")
    private Integer multiple;
    @Column(name = "lottery_time")
    private java.util.Date lotteryTime;
    @Column(name = "created_time")
    private java.util.Date createdTime;
    @Column(name = "updated_time")
    private java.util.Date updatedTime;
    @Column(name = "version")
    private Long version;

    public BettingTicketDigital(Long orderId, Long ticketDigitalId, Long bettingTicketId, String period, Integer bettingType, String playType, String bettingGroup, Integer bettingResult, Integer level, BigDecimal preTaxBonus, BigDecimal aftTaxBonus, Integer multiple, Date lotteryTime, Date createdTime, Date updatedTime, Long version) {
        this.orderId = orderId;
        this.ticketDigitalId = ticketDigitalId;
        this.bettingTicketId = bettingTicketId;
        this.period = period;
        this.bettingType = bettingType;
        this.playType = playType;
        this.bettingGroup = bettingGroup;
        this.bettingResult = bettingResult;
        this.level = level;
        this.preTaxBonus = preTaxBonus;
        this.aftTaxBonus = aftTaxBonus;
        this.multiple = multiple;
        this.lotteryTime = lotteryTime;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.version = version;
    }
}
