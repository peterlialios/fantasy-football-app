package com.fantasyfootball.service;

import com.fantasyfootball.entity.Player;
import com.fantasyfootball.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PlayerService {
    
    @Autowired
    private PlayerRepository playerRepository;
    
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }
    
    public List<Player> getActivePlayers() {
        return playerRepository.findByIsActiveTrue();
    }
    
    public Optional<Player> getPlayerById(Integer id) {
        return playerRepository.findById(id);
    }
    
    public Optional<Player> getPlayerWithNflTeam(Integer id) {
        return playerRepository.findByIdWithNflTeam(id);
    }
    
    public Optional<Player> getPlayerWithStats(Integer id) {
        return playerRepository.findByIdWithStats(id);
    }
    
    public List<Player> getPlayersByPosition(String position) {
        return playerRepository.findActivePlayersByPosition(position);
    }
    
    public List<Player> getRegularPlayersByPosition(String position) {
        return playerRepository.findRegularPlayersByPosition(position);
    }
    
    public List<Player> getAllDefenseUnits() {
        return playerRepository.findAllDefenseUnits();
    }
    
    public List<Player> getAllRegularPlayers() {
        return playerRepository.findAllRegularPlayers();
    }
    
    public Optional<Player> getDefenseByNflTeam(Integer nflTeamId) {
        return playerRepository.findDefenseByNflTeamId(nflTeamId);
    }
    
    public List<Player> getPlayersByNflTeam(Integer nflTeamId) {
        return playerRepository.findByNflTeamId(nflTeamId);
    }
    
    public List<Player> searchPlayersByName(String searchTerm) {
        return playerRepository.findByNameContainingIgnoreCase(searchTerm);
    }
    
    public List<Player> getAvailablePlayersNotOnTeam(Integer teamId) {
        return playerRepository.findAvailablePlayersNotOnTeam(teamId);
    }
    
    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }
    
    public void deletePlayer(Integer id) {
        playerRepository.deleteById(id);
    }
    
    public Player updatePlayer(Integer id, Player playerDetails) {
        Optional<Player> playerOpt = playerRepository.findById(id);
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            player.setFirstName(playerDetails.getFirstName());
            player.setLastName(playerDetails.getLastName());
            player.setPosition(playerDetails.getPosition());
            player.setNflTeam(playerDetails.getNflTeam());
            player.setJerseyNumber(playerDetails.getJerseyNumber());
            player.setHeightInches(playerDetails.getHeightInches());
            player.setWeightLbs(playerDetails.getWeightLbs());
            player.setBirthDate(playerDetails.getBirthDate());
            player.setYearsExperience(playerDetails.getYearsExperience());
            player.setSalary(playerDetails.getSalary());
            player.setFantasyPoints(playerDetails.getFantasyPoints());
            player.setIsActive(playerDetails.getIsActive());
            player.setIsDst(playerDetails.getIsDst());
            player.setDstTeamName(playerDetails.getDstTeamName());
            return playerRepository.save(player);
        } else {
            throw new RuntimeException("Player not found with id: " + id);
        }
    }
}