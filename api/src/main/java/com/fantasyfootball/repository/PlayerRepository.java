package com.fantasyfootball.repository;

import com.fantasyfootball.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {
    
    List<Player> findByPosition(String position);
    
    List<Player> findByNflTeamId(Integer nflTeamId);
    
    List<Player> findByIsActiveTrue();
    
    @Query("SELECT p FROM Player p WHERE p.isActive = true AND " +
           "(LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.dstTeamName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Player> findByNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT p FROM Player p WHERE p.position = :position AND p.isActive = true")
    List<Player> findActivePlayersByPosition(@Param("position") String position);
    
    @Query("SELECT p FROM Player p LEFT JOIN FETCH p.nflTeam WHERE p.id = :playerId")
    Optional<Player> findByIdWithNflTeam(@Param("playerId") Integer playerId);
    
    @Query("SELECT p FROM Player p LEFT JOIN FETCH p.playerStats WHERE p.id = :playerId")
    Optional<Player> findByIdWithStats(@Param("playerId") Integer playerId);
    
    @Query("SELECT p FROM Player p WHERE p.id NOT IN " +
           "(SELECT tp.player.id FROM TeamPlayer tp WHERE tp.team.id = :teamId)")
    List<Player> findAvailablePlayersNotOnTeam(@Param("teamId") Integer teamId);
    
    // D/ST specific queries
    @Query("SELECT p FROM Player p WHERE p.isDst = true AND p.isActive = true")
    List<Player> findAllDefenseUnits();
    
    @Query("SELECT p FROM Player p WHERE p.isDst = false AND p.isActive = true")
    List<Player> findAllRegularPlayers();
    
    @Query("SELECT p FROM Player p WHERE p.nflTeam.id = :nflTeamId AND p.isDst = true")
    Optional<Player> findDefenseByNflTeamId(@Param("nflTeamId") Integer nflTeamId);
    
    @Query("SELECT p FROM Player p WHERE p.position = :position AND p.isDst = false AND p.isActive = true")
    List<Player> findRegularPlayersByPosition(@Param("position") String position);
}