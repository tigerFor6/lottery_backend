package com.linglong.lottery_backend.banner.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import javax.persistence.*;

@Entity(name = "tbl_banner")
@DynamicUpdate
@DynamicInsert
@Data
public class TblBanner implements Serializable {

	@Id
	private Long id;

	private String theme;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "file_uuid")
	private String fileUuid;

	@Column(name = "file_path")
	private String filePath;

	private String orders;

	@Column(name = "effective_time")
	private java.util.Date effectiveTime;

	@Column(name = "lose_time")
	private java.util.Date loseTime;

	@Column(name = "lengh_time")
	private Long lenghTime;

	@Column(name = "show_port")
	private Integer showPort;

	@Column(name = "show_version")
	private String showVersion;

	@Column(name = "match_id")
	private Long matchId;

	@Column(name = "page_link")
	private String pageLink;

	private Integer status;

	@Column(name = "positions")
	private Integer positions;

	@Column(name = "operator")
	private String operator;

	@Column(name = "created_time")
	private java.util.Date createdTime;

	@Column(name = "updated_time")
	private java.util.Date updatedTime;
}
