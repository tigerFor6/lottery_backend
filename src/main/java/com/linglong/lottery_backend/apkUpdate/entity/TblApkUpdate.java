package com.linglong.lottery_backend.apkUpdate.entity;
import java.io.Serializable;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import javax.persistence.*;

@Entity(name = "tbl_apk_update")
@DynamicUpdate
@DynamicInsert
@Data
public class TblApkUpdate implements Serializable {

	@Id
	private Long id;

	private String version;

	@Column(name = "app_code")
	private Integer appCode;

	@Column(name = "version_code")
	private Integer versionCode;

	@Column(name = "channel_no")
	private String channelNo;

	private Integer forces;

	@Column(name = "download_url")
	private String downloadUrl;

	@Column(name = "version_desc")
	private String versionDesc;

	@Column(name = "created_time")
	private java.util.Date createdTime;

}
