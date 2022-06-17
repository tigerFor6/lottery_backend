package com.linglong.lottery_backend.ticket.bean;

import com.linglong.lottery_backend.ticket.entity.bo.TicketNumber;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by ynght on 2019-04-26
 */
@Data
@NoArgsConstructor
public class TicketDto {
    private Long ticketId;
    private BigDecimal ticketAmount;
    private List<TicketNumber> numbers;
    private String playType;
    private String extra;
    private Integer times;
    private Integer gameType;
    private Long orderId;
    private Date createTime;
    private Date saleTime;

    public TicketDto(Long ticketId, BigDecimal ticketAmount, List<TicketNumber> numbers, String playType, String extra, Integer times, Integer gameType,Long orderId, Date createTime) {
        this.ticketId = ticketId;
        this.ticketAmount = ticketAmount;
        this.numbers = numbers;
        this.playType = playType;
        this.extra = extra;
        this.times = times;
        this.orderId = orderId;
        this.gameType = gameType;
        this.createTime = createTime;
    }


}
