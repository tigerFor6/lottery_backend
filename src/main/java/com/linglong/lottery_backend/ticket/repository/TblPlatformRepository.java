package com.linglong.lottery_backend.ticket.repository;

import com.linglong.lottery_backend.ticket.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by ynght on 2019-04-26
 */
public interface TblPlatformRepository extends JpaRepository<Platform, Long> {
}
