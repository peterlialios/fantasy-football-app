package com.fantasyfootball.repository;

import com.fantasyfootball.entity.TeamLogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamLogoRepository extends JpaRepository<TeamLogo, Integer> {
    
    List<TeamLogo> findByTeamId(Integer teamId);
    
    @Query("SELECT tl FROM TeamLogo tl WHERE tl.team.id = :teamId AND tl.relDefault = true")
    Optional<TeamLogo> findDefaultLogoByTeamId(@Param("teamId") Integer teamId);
    
    @Query("SELECT tl FROM TeamLogo tl WHERE tl.team.id = :teamId AND tl.relScoreboard = true")
    Optional<TeamLogo> findScoreboardLogoByTeamId(@Param("teamId") Integer teamId);
    
    @Query("SELECT tl FROM TeamLogo tl WHERE tl.team.id = :teamId AND tl.relDark = true")
    List<TeamLogo> findDarkLogosByTeamId(@Param("teamId") Integer teamId);
}