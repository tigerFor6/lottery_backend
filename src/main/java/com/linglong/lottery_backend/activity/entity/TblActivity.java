package com.linglong.lottery_backend.activity.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "tbl_activity")
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
@NoArgsConstructor
public class TblActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_id")
    private Long activityId;
    private String name;
    @Column(name = "img_url")
    private String imgUrl;
    @Column(name = "activity_url")
    private String activityUrl;

    private String rules;

    private Integer type;

    @Column(name = "activity_copy")
    private String activityCopy;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    private Boolean enable;

//    @Column(name = "created_time")
//    private Date createdTime;
//
//    @Column(name = "updated_time")
//    private Date updatedTime;
    private String remarks;

    private Integer level;

    @Version
    private Long version;

}
