package com.linglong.lottery_backend.apkUpdate.repository;

import com.linglong.lottery_backend.apkUpdate.entity.TblApkUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TblApkUpdateRepository extends JpaRepository<TblApkUpdate, Long>{

    List<TblApkUpdate> findAllByChannelNoAndAppCode(String channelNo,Integer appcode);
}
