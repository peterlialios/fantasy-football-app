package com.fantasyfootball.controller;

import com.fantasyfootball.entity.Player;
import com.fantasyfootball.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/players")
@CrossOrigin(origins = "http://localhost:3000")
public class PlayerController {
    
    @Autowired
    private PlayerService playerService;
    
    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers() {
        List<Player> players = playerService.getAllPlayers();
        return ResponseEntity.ok(players);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Player>> getActivePlayers() {
        List<Player> players = playerService.getActivePlayers();
        return ResponseEntity.ok(players);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Integer id) {
        Optional<Player> player = playerService.getPlayerById(id);
        return player.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/with-team")
    public ResponseEntity<Player> getPlayerWithNflTeam(@PathVariable Integer id) {
        Optional<Player> player = playerService.getPlayerWithNflTeam(id);
        return player.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/with-stats")
    public ResponseEntity<Player> getPlayerWithStats(@PathVariable Integer id) {
        Optional<Player> player = playerService.getPlayerWithStats(id);
        return player.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/position/{position}")
    public ResponseEntity<List<Player>> getPlayersByPosition(@PathVariable String position) {
        List<Player> players = playerService.getPlayersByPosition(position);
        return ResponseEntity.ok(players);
    }
    
    @GetMapping("/nfl-team/{nflTeamId}")
    public ResponseEntity<List<Player>> getPlayersByNflTeam(@PathVariable Integer nflTeamId) {
        List<Player> players = playerService.getPlayersByNflTeam(nflTeamId);
        return ResponseEntity.ok(players);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Player>> searchPlayersByName(@RequestParam String name) {
        List<Player> players = playerService.searchPlayersByName(name);
        return ResponseEntity.ok(players);
    }
    
    @GetMapping("/available/{teamId}")
    public ResponseEntity<List<Player>> getAvailablePlayersNotOnTeam(@PathVariable Integer teamId) {
        List<Player> players = playerService.getAvailablePlayersNotOnTeam(teamId);
        return ResponseEntity.ok(players);
    }
    
    @GetMapping("/regular/position/{position}")
    public ResponseEntity<List<Player>> getRegularPlayersByPosition(@PathVariable String position) {
        List<Player> players = playerService.getRegularPlayersByPosition(position);
        return ResponseEntity.ok(players);
    }
    
    @GetMapping("/defenses")
    public ResponseEntity<List<Player>> getAllDefenseUnits() {
        List<Player> defenses = playerService.getAllDefenseUnits();
        return ResponseEntity.ok(defenses);
    }
    
    @GetMapping("/regular")
    public ResponseEntity<List<Player>> getAllRegularPlayers() {
        List<Player> players = playerService.getAllRegularPlayers();
        return ResponseEntity.ok(players);
    }
    
    @GetMapping("/defenses/nfl-team/{nflTeamId}")
    public ResponseEntity<Player> getDefenseByNflTeam(@PathVariable Integer nflTeamId) {
        Optional<Player> defense = playerService.getDefenseByNflTeam(nflTeamId);
        return defense.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        try {
            Player savedPlayer = playerService.savePlayer(player);
            return ResponseEntity.ok(savedPlayer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable Integer id, @RequestBody Player playerDetails) {
        try {
            Player updatedPlayer = playerService.updatePlayer(id, playerDetails);
            return ResponseEntity.ok(updatedPlayer);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Integer id) {
        try {
            playerService.deletePlayer(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}