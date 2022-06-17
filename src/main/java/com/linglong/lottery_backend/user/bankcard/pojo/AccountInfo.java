package com.linglong.lottery_backend.user.bankcard.pojo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: qihua.li
 * @since: 2019-03-20
 */
@Entity
@Table(name = "tbl_account_info")
@Data
public class AccountInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "account_no")
    private String bankCardNum;
    @Column(name = "card_type")
    private Integer cardType;
    @Column(name = "opening_bank")
    private String affiliatedBank;
    @Column(name = "reserved_phone_number")
    private String reservedPhone;
    @Column(name = "account_status")
    private Integer cardStatus;
    @Column(name = "created_time")
    private Date createdTime;
    @Column(name = "updated_time")
    private Date updatedTime;
}
