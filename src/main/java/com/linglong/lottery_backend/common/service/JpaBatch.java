package com.linglong.lottery_backend.common.service;


import java.util.List;

/**
 * 批处理
 *
 * @param <T>
 */
public interface JpaBatch<T> {
    void batchInsert(List data);

    void batchUpdate(List data);
}
