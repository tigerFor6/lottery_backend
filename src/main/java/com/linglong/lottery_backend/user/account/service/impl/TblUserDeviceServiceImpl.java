package com.linglong.lottery_backend.user.account.service.impl;

import com.linglong.lottery_backend.user.account.entity.TblUserDevice;
import com.linglong.lottery_backend.user.account.repository.TblUserDeviceRepository;
import com.linglong.lottery_backend.user.account.service.TblUserDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TblUserDeviceServiceImpl implements TblUserDeviceService {

    @Autowired
    TblUserDeviceRepository tblUserDeviceRepository;

    private static Map<Integer, String> platformMap = new HashMap<>();

    static {
        platformMap.put(Integer.valueOf(0), "web");
        platformMap.put(Integer.valueOf(1), "android");
        platformMap.put(Integer.valueOf(2), "ios");
        platformMap.put(Integer.valueOf(3), "winphone");
    }

    @Override
    public TblUserDevice findByDeviceId(String deviceId){
        return tblUserDeviceRepository.findByDeviceId(deviceId);
    }

    @Override
    public TblUserDevice findByUserId(String userId){
        return tblUserDeviceRepository.findByUserId(userId);
    }

    @Override
    public List<TblUserDevice> findByUserIdAndStatus(String userId, boolean status) {
        return tblUserDeviceRepository.findByUserIdAndStatus(userId, status);
    }

    @Override
    public void save(TblUserDevice tblUserDevice) {
        tblUserDeviceRepository.saveAndFlush(tblUserDevice);
    }

    @Override
    public String getPlatform(Integer platformId) {
        return platformMap.get(platformId);
    }

    @Override
    public Integer getPlatformId(String platform) {
        for (Integer platformId : platformMap.keySet()){
            String platformData = platformMap.get(platformId);
            if(platform.equalsIgnoreCase(platformData)) {
                return platformId;
            }
        }

        return 0;
    }
}
