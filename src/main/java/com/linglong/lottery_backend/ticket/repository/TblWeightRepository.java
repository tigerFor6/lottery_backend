package com.linglong.lottery_backend.ticket.repository;

import com.linglong.lottery_backend.ticket.entity.Weight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TblWeightRepository extends JpaRepository<Weight, Long> {
}
