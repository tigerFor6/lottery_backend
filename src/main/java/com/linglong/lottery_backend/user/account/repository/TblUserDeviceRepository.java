package com.linglong.lottery_backend.user.account.repository;

import com.linglong.lottery_backend.user.account.entity.TblUserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TblUserDeviceRepository extends JpaRepository<TblUserDevice, Long> {

    /**
     * 根据 deviceId 查询设备信息
     * @param deviceId
     * @return
     */
    TblUserDevice findByDeviceId(@Param("deviceId") String deviceId);

    /**
     * 根据 用户ID 查询设备信息
     * @param userId
     * @return
     */
    TblUserDevice findByUserId(String userId);


    /**
     * 根据用户ID 查询 registration_id
     * @param userId
     * @return
     */
    List<TblUserDevice> findByUserIdAndStatus(@Param("userId") String userId, @Param("status") boolean status);
}
