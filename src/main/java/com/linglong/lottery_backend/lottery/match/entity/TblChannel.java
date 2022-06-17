package com.linglong.lottery_backend.lottery.match.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity(name = "tbl_channel")
@DynamicUpdate
@DynamicInsert
public class TblChannel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "channel_code")
    private String channelCode;

    @Column(name = "channel_name")
    private String channelName;

    private String secret;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;

    @Column(name = "delete_flag")
    private Boolean deleteFlag;

    @Version
    private Integer version;

    public Long getId() {
        return id;
    }

    public TblChannel setId(Long id) {
        this.id = id;
        return this;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public TblChannel setChannelCode(String channelCode) {
        this.channelCode = channelCode;
        return this;
    }

    public String getChannelName() {
        return channelName;
    }

    public TblChannel setChannelName(String channelName) {
        this.channelName = channelName;
        return this;
    }

    public String getSecret() {
        return secret;
    }

    public TblChannel setSecret(String secret) {
        this.secret = secret;
        return this;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public TblChannel setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public TblChannel setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public TblChannel setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public TblChannel setVersion(Integer version) {
        this.version = version;
        return this;
    }
}
