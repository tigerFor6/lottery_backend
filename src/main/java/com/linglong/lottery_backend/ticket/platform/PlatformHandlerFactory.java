package com.linglong.lottery_backend.ticket.platform;

import com.linglong.lottery_backend.utils.SpringContextHolder;
import org.springframework.stereotype.Component;

/**
 * Created by ynght on 2019-04-26
 */
@Component
public class PlatformHandlerFactory {
    private static PlatformHandlerFactory instance = new PlatformHandlerFactory();

    private PlatformHandlerFactory() {
    }

    public static PlatformHandlerFactory getInstance() {
        return instance;
    }

    public AbstractPlatformHandler getServiceHandler(String handlerName) {
        return (AbstractPlatformHandler) SpringContextHolder.getBean(handlerName);
    }
}
