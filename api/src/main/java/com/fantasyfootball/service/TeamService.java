package com.fantasyfootball.service;

import com.fantasyfootball.entity.Team;
import com.fantasyfootball.entity.TeamPlayer;
import com.fantasyfootball.entity.Player;
import com.fantasyfootball.repository.TeamRepository;
import com.fantasyfootball.repository.TeamPlayerRepository;
import com.fantasyfootball.repository.PlayerRepository;
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
    
    public TeamPlayer addPlayerToTeam(Integer teamId, Integer playerId, String positionOnTeam, BigDecimal cost) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        Optional<Player> playerOpt = playerRepository.findById(playerId);
        
        if (teamOpt.isEmpty() || playerOpt.isEmpty()) {
            throw new RuntimeException("Team or Player not found");
        }
        
        if (teamPlayerRepository.findByTeamIdAndPlayerId(teamId, playerId).isPresent()) {
            throw new RuntimeException("Player already on team");
        }
        
        TeamPlayer teamPlayer = new TeamPlayer();
        teamPlayer.setTeam(teamOpt.get());
        teamPlayer.setPlayer(playerOpt.get());
        teamPlayer.setPositionOnTeam(positionOnTeam);
        teamPlayer.setCost(cost);
        teamPlayer.setIsStarter(false);
        
        return teamPlayerRepository.save(teamPlayer);
    }
    
    public void removePlayerFromTeam(Integer teamId, Integer playerId) {
        Optional<TeamPlayer> teamPlayerOpt = teamPlayerRepository.findByTeamIdAndPlayerId(teamId, playerId);
        if (teamPlayerOpt.isPresent()) {
            teamPlayerRepository.delete(teamPlayerOpt.get());
        } else {
            throw new RuntimeException("Player not found on team");
        }
    }
    
    public TeamPlayer updatePlayerStatus(Integer teamId, Integer playerId, Boolean isStarter) {
        Optional<TeamPlayer> teamPlayerOpt = teamPlayerRepository.findByTeamIdAndPlayerId(teamId, playerId);
        if (teamPlayerOpt.isPresent()) {
            TeamPlayer teamPlayer = teamPlayerOpt.get();
            teamPlayer.setIsStarter(isStarter);
            return teamPlayerRepository.save(teamPlayer);
        } else {
            throw new RuntimeException("Player not found on team");
        }
    }
    
    public long getTeamSize(Integer teamId) {
        return teamPlayerRepository.countByTeamId(teamId);
    }
}