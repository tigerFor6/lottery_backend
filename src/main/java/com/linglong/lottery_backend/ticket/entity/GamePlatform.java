package com.linglong.lottery_backend.ticket.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "tbl_game_platform")
@DynamicUpdate
@DynamicInsert
@Data
public class GamePlatform implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_type")
    private Integer gameType;

    @Column(name = "platform_id")
    private Integer platformId;

    @Column(name = "bet_time_offset")
    private Integer betTimeOffset;

    @Column(name = "created_time")
    private java.util.Date createdTime;

    @Column(name = "updated_time")
    private java.util.Date updatedTime;

    @Version
    private Integer version;

}
