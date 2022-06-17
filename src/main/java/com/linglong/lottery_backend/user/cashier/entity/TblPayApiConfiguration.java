package com.linglong.lottery_backend.user.cashier.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/7/26
 */
@Entity(name = "tbl_payapi_configuration")
@DynamicUpdate
@DynamicInsert
@Data
public class TblPayApiConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "type")
    private String type;

    @Column(name = "productid")
    private Long productId;

    @Column(name = "requesturl")
    private String  requestUrl;

    @Column(name = "queryurl")
    private String  queryUrl;

    @Column(name = "notifyurl")
    private String notifyUrl;

    @Column(name = "appid")
    private String appId;

    @Column(name = "key")
    private String key;

    @Column(name = "weight")
    private Long weight;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;

    @Version
    private Integer version;
}
