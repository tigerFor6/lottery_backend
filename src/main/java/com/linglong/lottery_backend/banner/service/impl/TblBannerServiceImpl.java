package com.linglong.lottery_backend.banner.service.impl;

import com.linglong.lottery_backend.banner.entity.TblBanner;
import com.linglong.lottery_backend.banner.repository.TblBannerRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.linglong.lottery_backend.banner.service.TblBannerService;

import java.util.List;

@Service
public class TblBannerServiceImpl implements TblBannerService{

	@Autowired
	private TblBannerRepository repository;

	@Override
	public List<TblBanner> bannerDetail() {
		return repository.getBannerDetail();
	}
}
