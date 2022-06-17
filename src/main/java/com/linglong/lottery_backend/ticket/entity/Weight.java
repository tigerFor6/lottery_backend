package com.linglong.lottery_backend.ticket.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "tbl_weight")
@DynamicUpdate
@DynamicInsert
@Data
public class Weight implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_type")
    private Integer gameType;

    @Column(name = "platform_id")
    private Integer platformId;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "created_time")
    private java.util.Date createdTime;

    @Column(name = "update_time")
    private java.util.Date updateTime;

    @Version
    private Integer version;

}
