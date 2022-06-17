package com.linglong.lottery_backend.activity.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "tbl_user_activity")
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
@NoArgsConstructor
public class TblUserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_id")
    private Long activityId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "activity_type")
    private Integer activityType;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "status")
    private Integer status;

    private Long version;

}
