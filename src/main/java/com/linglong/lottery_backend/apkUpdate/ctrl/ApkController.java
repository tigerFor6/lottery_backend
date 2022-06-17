package com.linglong.lottery_backend.apkUpdate.ctrl;

import com.linglong.lottery_backend.apkUpdate.service.TblApkUpdateService;
import com.linglong.lottery_backend.lottery.match.common.ResultGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/apk")
@Slf4j
public class ApkController {

    @Autowired
    private TblApkUpdateService tblApkUpdateService;

    /**
     *安卓APP更新
     **/
    @GetMapping("/getUpdateUrl")
    public Object getUpdateUrl(String channelNo, Integer versionCode) {
        try {
            return tblApkUpdateService.getUpdateUrl(channelNo, versionCode,0);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return ResultGenerator.genFailResult("参数格式或类型不正确");
    }

    /**
     *Ios APP 更新
     **/
    @GetMapping("/getIosUpdateUrl")
    public Object getIosUpdateUrl(String channelNo, Integer versionCode) {
        try {
            return tblApkUpdateService.getUpdateUrl(channelNo, versionCode,1);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return ResultGenerator.genFailResult("参数格式或类型不正确");
    }
}
