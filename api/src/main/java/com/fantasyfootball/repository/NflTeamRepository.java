package com.fantasyfootball.repository;

import com.fantasyfootball.entity.NflTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NflTeamRepository extends JpaRepository<NflTeam, Integer> {
    
    Optional<NflTeam> findByAbbreviation(String abbreviation);
    
    Optional<NflTeam> findBySlug(String slug);
    
    Optional<NflTeam> findByEspnId(Integer espnId);
    
    Optional<NflTeam> findByEspnUid(String espnUid);
    
    List<NflTeam> findByConference(String conference);
    
    List<NflTeam> findByConferenceAndDivision(String conference, String division);
    
    List<NflTeam> findByIsActiveTrue();
    
    @Query("SELECT nt FROM NflTeam nt WHERE nt.isActive = true ORDER BY nt.conference, nt.division, nt.name")
    List<NflTeam> findAllActiveOrderedByConferenceAndDivision();
    
    @Query("SELECT nt FROM NflTeam nt LEFT JOIN FETCH nt.logos WHERE nt.id = :teamId")
    Optional<NflTeam> findByIdWithLogos(@Param("teamId") Integer teamId);
    
    @Query("SELECT nt FROM NflTeam nt LEFT JOIN FETCH nt.links WHERE nt.id = :teamId")
    Optional<NflTeam> findByIdWithLinks(@Param("teamId") Integer teamId);
    
    @Query("SELECT nt FROM NflTeam nt LEFT JOIN FETCH nt.logos LEFT JOIN FETCH nt.links WHERE nt.id = :teamId")
    Optional<NflTeam> findByIdWithLogosAndLinks(@Param("teamId") Integer teamId);
    
    @Query("SELECT DISTINCT nt.conference FROM NflTeam nt WHERE nt.isActive = true ORDER BY nt.conference")
    List<String> findDistinctConferences();
    
    @Query("SELECT DISTINCT nt.division FROM NflTeam nt WHERE nt.conference = :conference AND nt.isActive = true ORDER BY nt.division")
    List<String> findDistinctDivisionsByConference(@Param("conference") String conference);
}