package com.linglong.lottery_backend.ticket.platform;

import com.linglong.lottery_backend.ticket.cache.PlatformCache;
import com.linglong.lottery_backend.utils.SpringContextHolder;

/**
 * Created by ynght on 2019-04-26
 */
public class PlatformFactory {
    public PlatformFactory() {
    }

    public static PlatformFactory instance = new PlatformFactory();

    public static PlatformFactory getInstance() {
        return instance;
    }

    /**
     * 按彩票商名称获取工厂中的类
     *
     * @param platformEn
     * @return
     */

    public PlatformInterface getPlatform(String platformEn) {
        return (PlatformInterface) SpringContextHolder.getBean(platformEn + "Platform");
    }

    public PlatformInterface getPlatform(Integer platformId) {
        return getPlatform(PlatformCache.getPlatform(platformId).getPlatformEn());
    }
}
