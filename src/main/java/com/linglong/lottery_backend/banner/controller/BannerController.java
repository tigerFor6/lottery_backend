package com.linglong.lottery_backend.banner.controller;

import com.linglong.lottery_backend.user.account.controller.AccountController;
import com.linglong.lottery_backend.banner.entity.TblBanner;
import com.linglong.lottery_backend.banner.service.TblBannerService;
import com.linglong.lottery_backend.lottery.match.common.Result;
import com.linglong.lottery_backend.lottery.match.common.ResultGenerator;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/banner/detail")
public class BannerController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    TblBannerService tblBannerService;

    /**
     * @description: Banner查询接口
     * @author: ZhiYao.Zhang
     * @param:
     * @return:
     * @create:  2019-06-10
     **/

    @GetMapping("bannerDetail")
    public Result bannerDetail(){
        List<Map<String,Object>> results = new ArrayList();

        List<TblBanner> tblBanners =  tblBannerService.bannerDetail();
        tblBanners.forEach(e->{
            Map<String,Object> result = new HashedMap();
            result.put("filePath",e.getFilePath());
            result.put("pageLink",e.getPageLink());
            result.put("theme",e.getTheme());
            result.put("order",e.getOrders());
            result.put("lenghTime",e.getLenghTime());
            result.put("showPort",e.getShowPort());
            result.put("showVersion",e.getShowVersion());
            result.put("position",e.getPositions());
            results.add(result);

        });

        return ResultGenerator.genSuccessResult(results);
    }
}
