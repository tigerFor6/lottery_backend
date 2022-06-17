package com.linglong.lottery_backend.lottery.match.repository;


import com.linglong.lottery_backend.lottery.match.entity.TblChannel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TblChannelRepository extends JpaRepository<TblChannel, Long> {
    TblChannel findByChannelCode(String channel);
}
