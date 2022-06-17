package com.linglong.lottery_backend.order.listener.service;

import com.linglong.lottery_backend.order.listener.bean.HitPrizeResult;
import com.linglong.lottery_backend.order.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @Author: qihua.li
 * @since: 2019-04-19
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HitPrizeExecutor {

    private final IOrderService orderService;

    public void updateOpenPrizeResult(List<HitPrizeResult> hitPrizeResults) {
        orderService.updateOpenAndHitPrizeStatus(hitPrizeResults);
    }
}
