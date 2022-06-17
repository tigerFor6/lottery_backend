package com.linglong.lottery_backend.common.service;

import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * 批处理
 *
 * @param <T>
 */
@Service
public class JpaInsertBatch<T> implements JpaBatch {
    private static final Logger logger = LoggerFactory.getLogger(JpaInsertBatch.class);

    @PersistenceContext
    protected EntityManager em;

    @Override
    @Transactional
    public void batchInsert(List list) {
        try {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                em.persist(list.get(i));
                if (i % 1000 == 0 || i == (size - 1)) {
                    em.flush();
                    em.clear();
                }
            }
            logger.info("入库成功,共 {}条数据", list.size());
        } catch (Exception e) {
            logger.error("批量插入失败", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void batchUpdate(List list) {
        try {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                em.merge(list.get(i));
                if (i % 1000 == 0 || i == (size - 1)) {
                    em.flush();
                    em.clear();
                }
            }
            logger.info("入库成功,共 {}条数据", list.size());
        } catch (Exception e) {
            logger.error("批量更新失败", e);
            throw e;
        }
    }
}
