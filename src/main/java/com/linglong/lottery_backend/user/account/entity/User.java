package com.linglong.lottery_backend.user.account.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * Description
 *
 * @author yixun.xing
 * @since 14 三月 2019
 */
@Entity
@Table(name = "tbl_user_info")
@DynamicUpdate
@DynamicInsert
public class User implements Serializable {

	private static final long serialVersionUID = -6078593690109307173L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "password")
	private String password;

	@Column(name = "phone_number")
	private String phone;

	@Column(name = "real_name")
	private String realname;

	@Column(name = "id_card")
	private String idCard;

	@Column(name = "status")
	private Integer status;

	@Column(name = "channel_no")
	private String channelNo;

	@Column(name = "created_time")
	private Date createdTime;

	@Column(name = "updated_time")
	private Date updatedTime;

	@Column(name = "origin")
	private Integer origin;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}


	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getChannelNo() {
		return channelNo;
	}

	public void setChannelNo(String channelNo) {
		this.channelNo = channelNo;
	}

	public Integer getOrigin() {
		return origin;
	}

	public void setOrigin(Integer origin) {
		this.origin = origin;
	}

	public User() {
	}

	public User(String userId, String userName, String password, String phone, String realname, String idCard, Integer status, Date createdTime, Date updatedTime) {
		this.userId = userId;
		this.userName = userName;
		this.password = password;
		this.phone = phone;
		this.realname = realname;
		this.idCard = idCard;
		this.status = status;
		this.createdTime = createdTime;
		this.updatedTime = updatedTime;
	}
}
