package com.linglong.lottery_backend.lottery.match.repository;

import com.linglong.lottery_backend.lottery.match.entity.TblSeason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TblSeasonRepository extends JpaRepository<TblSeason, Long> {

    List<TblSeason> findByCurrentSeason(Boolean b);
}
