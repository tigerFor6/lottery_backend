package com.linglong.lottery_backend.ticket.repository;

import com.linglong.lottery_backend.ticket.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TblGameRepository extends JpaRepository<Game, Long> {
}
