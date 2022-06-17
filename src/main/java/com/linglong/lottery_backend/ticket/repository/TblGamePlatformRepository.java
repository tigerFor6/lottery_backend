package com.linglong.lottery_backend.ticket.repository;

import com.linglong.lottery_backend.ticket.entity.GamePlatform;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TblGamePlatformRepository extends JpaRepository<GamePlatform, Long> {
}
