package com.linglong.lottery_backend.activity.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "tbl_activity_relation")
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
@NoArgsConstructor
public class TblActivityRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "activity_id")
    private Long activityId;
    @Column(name = "relation_id")
    private Long relationId;
    @Column(name = "relation_type")
    private Integer relationType;
    @Column(name = "created_time")
    private Date createdTime;
    @Column(name = "updated_time")
    private Date updatedTime;
    private Long version;

}
