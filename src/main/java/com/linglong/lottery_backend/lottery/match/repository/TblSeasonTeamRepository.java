package com.linglong.lottery_backend.lottery.match.repository;

import com.linglong.lottery_backend.lottery.match.entity.TblSeasonTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TblSeasonTeamRepository extends JpaRepository<TblSeasonTeam, Long> {
    List<TblSeasonTeam> findBySeasonId(Long seasonId);

    @Query(value = "select * from tbl_season_team where season_id=:season_id and team_id=:team_id", nativeQuery = true)
    TblSeasonTeam findSeasonTeamBySeasonIdAndTeamId(@Param("season_id") Long seasonId, @Param("team_id") Long teamId);
}
