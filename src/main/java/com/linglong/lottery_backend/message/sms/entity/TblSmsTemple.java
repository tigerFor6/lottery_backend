package com.linglong.lottery_backend.message.sms.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/7/13
 */
@Entity(name = "tbl_sms_temple")
@Data
public class TblSmsTemple {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type")
    private Integer type;

    @Column(name = "temple")
    private String temple;

    @Column(name = "created_time")
    private Date created_time;

    @Column(name = "updated_time")
    private Date updated_time;

    @Version
    private Long version;

}
