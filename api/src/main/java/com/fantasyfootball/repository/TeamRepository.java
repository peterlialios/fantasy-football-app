package com.fantasyfootball.repository;

import com.fantasyfootball.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {
    
    List<Team> findByOwnerUsername(String username);
    
    List<Team> findByOwnerId(Integer ownerId);
    
    Optional<Team> findByNameAndOwnerId(String name, Integer ownerId);
    
    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.teamPlayers tp LEFT JOIN FETCH tp.player WHERE t.id = :teamId")
    Optional<Team> findByIdWithPlayers(@Param("teamId") Integer teamId);
    
    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.owner WHERE t.id = :teamId")
    Optional<Team> findByIdWithOwner(@Param("teamId") Integer teamId);
}