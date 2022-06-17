package com.linglong.lottery_backend.message.sms.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "tbl_sms")
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
@NoArgsConstructor
public class TblSms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sms_id")
    private Long smsId;

    @Column(name = "bus_id")
    private Long busId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "telphone")
    private String telphone;

    @Column(name = "content")
    private String content;

    @Column(name = "status")
    private Integer status;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;

    @Version
    private Long version;
}
