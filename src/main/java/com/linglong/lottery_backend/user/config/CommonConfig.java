package com.linglong.lottery_backend.user.config;

import com.linglong.lottery_backend.utils.IdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: qihua.li
 * @since: 2019-04-16
 */
@Configuration
public class CommonConfig {

    @Bean
    public IdWorker idWorker() {
        return new IdWorker(1, 1);
    }
}
