package com.fantasyfootball.repository;

import com.fantasyfootball.entity.TeamPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamPlayerRepository extends JpaRepository<TeamPlayer, Integer> {
    
    List<TeamPlayer> findByTeamId(Integer teamId);
    
    List<TeamPlayer> findByPlayerId(Integer playerId);
    
    Optional<TeamPlayer> findByTeamIdAndPlayerId(Integer teamId, Integer playerId);
    
    @Query("SELECT tp FROM TeamPlayer tp LEFT JOIN FETCH tp.player p LEFT JOIN FETCH p.nflTeam WHERE tp.team.id = :teamId")
    List<TeamPlayer> findByTeamIdWithPlayerDetails(@Param("teamId") Integer teamId);
    
    @Query("SELECT tp FROM TeamPlayer tp JOIN FETCH tp.rosterPositionEntity rpe WHERE tp.team.id = :teamId AND rpe.isStarting = true ORDER BY rpe.displayOrder")
    List<TeamPlayer> findStartersByTeamId(@Param("teamId") Integer teamId);
    
    @Query("SELECT tp FROM TeamPlayer tp JOIN FETCH tp.rosterPositionEntity rpe WHERE tp.team.id = :teamId AND rpe.isStarting = false")
    List<TeamPlayer> findBenchPlayersByTeamId(@Param("teamId") Integer teamId);
    
    @Query("SELECT tp FROM TeamPlayer tp WHERE tp.team.id = :teamId AND tp.rosterPosition = :rosterPosition")
    List<TeamPlayer> findByTeamIdAndRosterPosition(@Param("teamId") Integer teamId, @Param("rosterPosition") String rosterPosition);
    
    @Query("SELECT tp FROM TeamPlayer tp WHERE tp.team.id = :teamId AND tp.rosterPosition = :rosterPosition")
    Optional<TeamPlayer> findByTeamIdAndRosterPositionSingle(@Param("teamId") Integer teamId, @Param("rosterPosition") String rosterPosition);
    
    @Query("SELECT COUNT(tp) FROM TeamPlayer tp WHERE tp.team.id = :teamId AND tp.rosterPosition = :rosterPosition")
    long countByTeamIdAndRosterPosition(@Param("teamId") Integer teamId, @Param("rosterPosition") String rosterPosition);
    
    @Query("SELECT COUNT(tp) FROM TeamPlayer tp WHERE tp.team.id = :teamId")
    long countByTeamId(@Param("teamId") Integer teamId);
}