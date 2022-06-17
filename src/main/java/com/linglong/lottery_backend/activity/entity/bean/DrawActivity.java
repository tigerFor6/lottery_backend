package com.linglong.lottery_backend.activity.entity.bean;

import com.linglong.lottery_backend.activity.entity.TblActivity;
import lombok.Data;

import java.io.Serializable;

@Data
public class DrawActivity implements Serializable {

    private Long userId;

    private TblActivity activity;
}
