package com.linglong.lottery_backend.lottery.digital.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/7/4
 */
@Entity(name = "tbl_win_data")
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
public class TblWinData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "game_type")
    private Integer gameType;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;

    @Column(name = "version")
    private Integer version;
}
