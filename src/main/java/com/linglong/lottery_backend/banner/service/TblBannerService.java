package com.linglong.lottery_backend.banner.service;

import com.linglong.lottery_backend.banner.entity.TblBanner;

import java.util.List;

public interface TblBannerService  {
    //得到banner详情
    List<TblBanner> bannerDetail();
}
