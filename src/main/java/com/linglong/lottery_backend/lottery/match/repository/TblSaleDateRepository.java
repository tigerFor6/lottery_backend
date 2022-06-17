package com.linglong.lottery_backend.lottery.match.repository;

import com.linglong.lottery_backend.lottery.match.entity.TblSaleDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/6/10
 */
public interface TblSaleDateRepository extends JpaRepository<TblSaleDate, Long> {

    TblSaleDate findTblSaleDateByGameTypeAndSaleDate(Integer gameType,Integer saleDate);

    List<TblSaleDate> findTblSaleDateByGameType(Integer gameType);

    List<TblSaleDate> findAll();
}
