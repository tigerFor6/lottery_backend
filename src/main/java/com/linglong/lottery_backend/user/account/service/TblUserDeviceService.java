package com.linglong.lottery_backend.user.account.service;

import com.linglong.lottery_backend.user.account.entity.TblUserDevice;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TblUserDeviceService {

    String getPlatform(Integer platformId);

    Integer getPlatformId(String platform);

    /**
     * 根据 deviceId 查询设备信息
     * @param deviceId
     * @return
     */
    TblUserDevice findByDeviceId(String deviceId);

    /**
     * 根据 userId, status 查询设备信息
     * @param userId
     * @param status
     * @return
     */
    List<TblUserDevice> findByUserIdAndStatus(String userId, boolean status);

    /**
     * 根据 用户ID 查询设备信息
     * @param userId
     * @return
     */
    TblUserDevice findByUserId(String userId);

    void save(TblUserDevice tblUserDevice);
}
