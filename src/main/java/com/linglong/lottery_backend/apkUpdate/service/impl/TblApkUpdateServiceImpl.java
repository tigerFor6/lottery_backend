package com.linglong.lottery_backend.apkUpdate.service.impl;

import com.linglong.lottery_backend.apkUpdate.entity.TblApkUpdate;
import com.linglong.lottery_backend.apkUpdate.repository.TblApkUpdateRepository;
import com.linglong.lottery_backend.apkUpdate.service.TblApkUpdateService;
import com.linglong.lottery_backend.lottery.match.common.ResultGenerator;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TblApkUpdateServiceImpl implements TblApkUpdateService {

    @Autowired
    private TblApkUpdateRepository repository;

    @Override
    public Object getUpdateUrl(String channelNo, Integer versionCode,Integer appcode) {
        if (null == channelNo || "".equals(channelNo.trim())) {
            return ResultGenerator.genFailResult("App的渠道号未传");
        }

        if (null == versionCode) {
            return ResultGenerator.genFailResult("版本Code未传");
        }

        List<TblApkUpdate> res = repository.findAllByChannelNoAndAppCode(channelNo.trim(),appcode);
        if (res.size() == 0) {
            return ResultGenerator.genSuccessResult();
        }

        List<TblApkUpdate> sortApk = res.stream().sorted(Comparator.comparing(TblApkUpdate::getCreatedTime).reversed()).collect(Collectors.toList());

        Map<String, Object> result = new HashedMap();
        if (versionCode == sortApk.get(0).getVersionCode()) {
            //不需要更新
            result.put("update", 0);
        } else {
            //需要更新
            result.put("update", 1);
        }

        result.put("version", sortApk.get(0).getVersion());
        result.put("versionCode", sortApk.get(0).getVersionCode());
        result.put("downUrl", sortApk.get(0).getDownloadUrl());
        result.put("forces", sortApk.get(0).getForces());
        result.put("describe", null == sortApk.get(0).getVersionDesc() ? "" : sortApk.get(0).getVersionDesc());
        return ResultGenerator.genSuccessResult(result);
    }
}
