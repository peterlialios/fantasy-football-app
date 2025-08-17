package com.fantasyfootball.service;

import com.fantasyfootball.entity.Team;
import com.fantasyfootball.entity.TeamPlayer;
import com.fantasyfootball.entity.Player;
import com.fantasyfootball.entity.RosterPosition;
import com.fantasyfootball.repository.TeamRepository;
import com.fantasyfootball.repository.TeamPlayerRepository;
import com.fantasyfootball.repository.PlayerRepository;
import com.fantasyfootball.repository.RosterPositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TeamService {
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private TeamPlayerRepository teamPlayerRepository;
    
    @Autowired
    private PlayerRepository playerRepository;
    
    @Autowired
    private RosterPositionRepository rosterPositionRepository;
    
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
    
    public Optional<Team> getTeamById(Integer id) {
        return teamRepository.findById(id);
    }
    
    public Optional<Team> getTeamWithPlayers(Integer id) {
        return teamRepository.findByIdWithPlayers(id);
    }
    
    public List<Team> getTeamsByOwnerId(Integer ownerId) {
        return teamRepository.findByOwnerId(ownerId);
    }
    
    public Team saveTeam(Team team) {
        return teamRepository.save(team);
    }
    
    public void deleteTeam(Integer id) {
        teamRepository.deleteById(id);
    }
    
    public List<TeamPlayer> getTeamRoster(Integer teamId) {
        return teamPlayerRepository.findByTeamIdWithPlayerDetails(teamId);
    }
    
    public List<TeamPlayer> getTeamStarters(Integer teamId) {
        return teamPlayerRepository.findStartersByTeamId(teamId);
    }
    
    public List<TeamPlayer> getTeamBench(Integer teamId) {
        return teamPlayerRepository.findBenchPlayersByTeamId(teamId);
    }
    
    public TeamPlayer addPlayerToTeam(Integer teamId, Integer playerId, String rosterPosition, BigDecimal cost) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        Optional<Player> playerOpt = playerRepository.findById(playerId);
        Optional<RosterPosition> rosterPosOpt = rosterPositionRepository.findByPositionCode(rosterPosition);
        
        if (teamOpt.isEmpty() || playerOpt.isEmpty() || rosterPosOpt.isEmpty()) {
            throw new RuntimeException("Team, Player, or Roster Position not found");
        }
        
        if (teamPlayerRepository.findByTeamIdAndPlayerId(teamId, playerId).isPresent()) {
            throw new RuntimeException("Player already on team");
        }
        
        // Validate roster position capacity
        RosterPosition rosterPos = rosterPosOpt.get();
        long currentCount = teamPlayerRepository.countByTeamIdAndRosterPosition(teamId, rosterPosition);
        if (currentCount >= rosterPos.getMaxCount()) {
            throw new RuntimeException("Roster position " + rosterPosition + " is full (max: " + rosterPos.getMaxCount() + ")");
        }
        
        // Validate position compatibility
        Player player = playerOpt.get();
        if (!isValidPositionForRosterSlot(player, rosterPosition)) {
            throw new RuntimeException("Player position " + player.getPosition() + " is not valid for roster slot " + rosterPosition);
        }
        
        TeamPlayer teamPlayer = new TeamPlayer();
        teamPlayer.setTeam(teamOpt.get());
        teamPlayer.setPlayer(player);
        teamPlayer.setRosterPosition(rosterPosition);
        teamPlayer.setCost(cost);
        
        return teamPlayerRepository.save(teamPlayer);
    }
    
    private boolean isValidPositionForRosterSlot(Player player, String rosterPosition) {
        String playerPosition = player.getPosition();
        
        // Direct position matches
        if (rosterPosition.equals(playerPosition)) {
            return true;
        }
        
        // FLEX can hold RB, WR, or TE
        if ("FLEX".equals(rosterPosition)) {
            return "RB".equals(playerPosition) || "WR".equals(playerPosition) || "TE".equals(playerPosition);
        }
        
        // BENCH can hold any position
        if ("BENCH".equals(rosterPosition)) {
            return true;
        }
        
        return false;
    }
    
    public void removePlayerFromTeam(Integer teamId, Integer playerId) {
        Optional<TeamPlayer> teamPlayerOpt = teamPlayerRepository.findByTeamIdAndPlayerId(teamId, playerId);
        if (teamPlayerOpt.isPresent()) {
            teamPlayerRepository.delete(teamPlayerOpt.get());
        } else {
            throw new RuntimeException("Player not found on team");
        }
    }
    
    public TeamPlayer movePlayerToRosterPosition(Integer teamId, Integer playerId, String newRosterPosition) {
        Optional<TeamPlayer> teamPlayerOpt = teamPlayerRepository.findByTeamIdAndPlayerId(teamId, playerId);
        Optional<RosterPosition> rosterPosOpt = rosterPositionRepository.findByPositionCode(newRosterPosition);
        
        if (teamPlayerOpt.isEmpty() || rosterPosOpt.isEmpty()) {
            throw new RuntimeException("TeamPlayer or Roster Position not found");
        }
        
        TeamPlayer teamPlayer = teamPlayerOpt.get();
        Player player = teamPlayer.getPlayer();
        
        // Validate position compatibility
        if (!isValidPositionForRosterSlot(player, newRosterPosition)) {
            throw new RuntimeException("Player position " + player.getPosition() + " is not valid for roster slot " + newRosterPosition);
        }
        
        // Validate roster position capacity (exclude current player)
        RosterPosition rosterPos = rosterPosOpt.get();
        long currentCount = teamPlayerRepository.countByTeamIdAndRosterPosition(teamId, newRosterPosition);
        if (currentCount >= rosterPos.getMaxCount()) {
            throw new RuntimeException("Roster position " + newRosterPosition + " is full (max: " + rosterPos.getMaxCount() + ")");
        }
        
        teamPlayer.setRosterPosition(newRosterPosition);
        return teamPlayerRepository.save(teamPlayer);
    }
    
    public List<RosterPosition> getAllRosterPositions() {
        return rosterPositionRepository.findAllOrderedByDisplayOrder();
    }
    
    public List<RosterPosition> getStartingPositions() {
        return rosterPositionRepository.findStartingPositions();
    }
    
    public List<TeamPlayer> getPlayersByRosterPosition(Integer teamId, String rosterPosition) {
        return teamPlayerRepository.findByTeamIdAndRosterPosition(teamId, rosterPosition);
    }
    
    public long getTeamSize(Integer teamId) {
        return teamPlayerRepository.countByTeamId(teamId);
    }
}