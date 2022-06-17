package com.linglong.lottery_backend.user.config;

import cn.jiguang.common.ClientConfig;
import cn.jpush.api.JPushClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 极光推送config
 */
@Configuration
public class JpushConfig {

    @Value("${jpush.app_key}")
    public String APP_KEY;

    @Value("${jpush.master_secret}")
    public String MASTER_SECRET;

    @Bean
    public JPushClient jPushClient(ClientConfig clientConfig) {
        return new JPushClient(MASTER_SECRET, APP_KEY, null, clientConfig);
    }

    @Bean
    public ClientConfig clientConfig() {
        return ClientConfig.getInstance();
    }

}
