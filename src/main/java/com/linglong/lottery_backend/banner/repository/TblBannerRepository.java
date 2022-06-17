package com.linglong.lottery_backend.banner.repository;

import com.linglong.lottery_backend.banner.entity.TblBanner;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TblBannerRepository extends JpaRepository<TblBanner, Long>{

    //获得所有已经上线的banner
    @Query(value = "SELECT * FROM tbl_banner WHERE STATUS = 1 AND effective_time <= NOW( ) AND lose_time >= NOW( ) ORDER BY orders desc", nativeQuery = true)
    List<TblBanner> getBannerDetail();
}
