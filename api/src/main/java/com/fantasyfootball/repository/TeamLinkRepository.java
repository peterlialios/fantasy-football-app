package com.fantasyfootball.repository;

import com.fantasyfootball.entity.TeamLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamLinkRepository extends JpaRepository<TeamLink, Integer> {
    
    List<TeamLink> findByTeamId(Integer teamId);
    
    @Query("SELECT tl FROM TeamLink tl WHERE tl.team.id = :teamId AND tl.relClubhouse = true")
    Optional<TeamLink> findClubhouseLinkByTeamId(@Param("teamId") Integer teamId);
    
    @Query("SELECT tl FROM TeamLink tl WHERE tl.team.id = :teamId AND tl.relRoster = true")
    Optional<TeamLink> findRosterLinkByTeamId(@Param("teamId") Integer teamId);
    
    @Query("SELECT tl FROM TeamLink tl WHERE tl.team.id = :teamId AND tl.relStats = true")
    Optional<TeamLink> findStatsLinkByTeamId(@Param("teamId") Integer teamId);
    
    @Query("SELECT tl FROM TeamLink tl WHERE tl.team.id = :teamId AND tl.relSchedule = true")
    Optional<TeamLink> findScheduleLinkByTeamId(@Param("teamId") Integer teamId);
    
    @Query("SELECT tl FROM TeamLink tl WHERE tl.team.id = :teamId AND tl.relTickets = true")
    Optional<TeamLink> findTicketsLinkByTeamId(@Param("teamId") Integer teamId);
    
    @Query("SELECT tl FROM TeamLink tl WHERE tl.team.id = :teamId AND tl.relDepthchart = true")
    Optional<TeamLink> findDepthChartLinkByTeamId(@Param("teamId") Integer teamId);
}