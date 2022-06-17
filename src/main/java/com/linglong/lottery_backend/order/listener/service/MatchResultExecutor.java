package com.linglong.lottery_backend.order.listener.service;

import com.linglong.lottery_backend.order.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: qihua.li
 * @since: 2019-04-22
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MatchResultExecutor {

    private final IOrderService orderService;

    public void updatedMatchResult() {

    }
}
