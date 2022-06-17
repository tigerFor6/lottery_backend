package com.linglong.lottery_backend.ticket.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "tbl_game")
@DynamicUpdate
@DynamicInsert
@Data
public class Game implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_type")
    private Integer gameType;

    @Column(name = "game_en")
    private String gameEn;

    @Column(name = "game_name")
    private String gameName;

    @Column(name = "short_name")
    private String shortName;

    @Column(name = "type")
    private Integer type;

    @Column(name = "usable")
    private Integer usable;

    @Column(name = "ticket_bet_times")
    private Integer ticketBetTimes;

    @Column(name = "max_bet_times")
    private Integer maxBetTimes;

    @Column(name = "created_time")
    private java.util.Date createdTime;

    @Column(name = "update_time")
    private java.util.Date updateTime;

    @Version
    private Integer version;

}
