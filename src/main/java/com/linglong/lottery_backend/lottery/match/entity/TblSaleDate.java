package com.linglong.lottery_backend.lottery.match.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/6/10
 */
@Entity(name = "tbl_sale_date")
@DynamicUpdate
@DynamicInsert
@Data
public class TblSaleDate implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "game_type")
    private Integer gameType;
    @Column(name = "sale_date")
    private Integer saleDate;
    @Column(name = "sale_time")
    private String saleTime;
    @Column(name = "created_time")
    private Date createdTime;
    @Version
    private Integer version;
    @Column(name = "dif_time")
    private Integer difTime;
}
