package com.fantasyfootball.controller;

import com.fantasyfootball.entity.Team;
import com.fantasyfootball.entity.TeamPlayer;
import com.fantasyfootball.entity.RosterPosition;
import com.fantasyfootball.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/teams")
@CrossOrigin(origins = "http://localhost:3000")
public class TeamController {
    
    @Autowired
    private TeamService teamService;
    
    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        List<Team> teams = teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable Integer id) {
        Optional<Team> team = teamService.getTeamById(id);
        return team.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/with-players")
    public ResponseEntity<Team> getTeamWithPlayers(@PathVariable Integer id) {
        Optional<Team> team = teamService.getTeamWithPlayers(id);
        return team.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Team>> getTeamsByOwner(@PathVariable Integer ownerId) {
        List<Team> teams = teamService.getTeamsByOwnerId(ownerId);
        return ResponseEntity.ok(teams);
    }
    
    @PostMapping
    public ResponseEntity<Team> createTeam(@RequestBody Team team) {
        try {
            Team savedTeam = teamService.saveTeam(team);
            return ResponseEntity.ok(savedTeam);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Team> updateTeam(@PathVariable Integer id, @RequestBody Team teamDetails) {
        Optional<Team> teamOpt = teamService.getTeamById(id);
        if (teamOpt.isPresent()) {
            Team team = teamOpt.get();
            team.setName(teamDetails.getName());
            team.setBudget(teamDetails.getBudget());
            Team updatedTeam = teamService.saveTeam(team);
            return ResponseEntity.ok(updatedTeam);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Integer id) {
        try {
            teamService.deleteTeam(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/roster")
    public ResponseEntity<List<TeamPlayer>> getTeamRoster(@PathVariable Integer id) {
        List<TeamPlayer> roster = teamService.getTeamRoster(id);
        return ResponseEntity.ok(roster);
    }
    
    @GetMapping("/{id}/starters")
    public ResponseEntity<List<TeamPlayer>> getTeamStarters(@PathVariable Integer id) {
        List<TeamPlayer> starters = teamService.getTeamStarters(id);
        return ResponseEntity.ok(starters);
    }
    
    @GetMapping("/{id}/bench")
    public ResponseEntity<List<TeamPlayer>> getTeamBench(@PathVariable Integer id) {
        List<TeamPlayer> bench = teamService.getTeamBench(id);
        return ResponseEntity.ok(bench);
    }
    
    @PostMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<TeamPlayer> addPlayerToTeam(
            @PathVariable Integer teamId,
            @PathVariable Integer playerId,
            @RequestParam String rosterPosition,
            @RequestParam(required = false, defaultValue = "0.0") BigDecimal cost) {
        try {
            TeamPlayer teamPlayer = teamService.addPlayerToTeam(teamId, playerId, rosterPosition, cost);
            return ResponseEntity.ok(teamPlayer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<Void> removePlayerFromTeam(
            @PathVariable Integer teamId,
            @PathVariable Integer playerId) {
        try {
            teamService.removePlayerFromTeam(teamId, playerId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{teamId}/players/{playerId}/roster-position")
    public ResponseEntity<TeamPlayer> movePlayerToRosterPosition(
            @PathVariable Integer teamId,
            @PathVariable Integer playerId,
            @RequestParam String rosterPosition) {
        try {
            TeamPlayer teamPlayer = teamService.movePlayerToRosterPosition(teamId, playerId, rosterPosition);
            return ResponseEntity.ok(teamPlayer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/roster-positions")
    public ResponseEntity<List<RosterPosition>> getAllRosterPositions() {
        List<RosterPosition> positions = teamService.getAllRosterPositions();
        return ResponseEntity.ok(positions);
    }
    
    @GetMapping("/roster-positions/starting")
    public ResponseEntity<List<RosterPosition>> getStartingPositions() {
        List<RosterPosition> positions = teamService.getStartingPositions();
        return ResponseEntity.ok(positions);
    }
    
    @GetMapping("/{teamId}/players/position/{rosterPosition}")
    public ResponseEntity<List<TeamPlayer>> getPlayersByRosterPosition(
            @PathVariable Integer teamId,
            @PathVariable String rosterPosition) {
        List<TeamPlayer> players = teamService.getPlayersByRosterPosition(teamId, rosterPosition);
        return ResponseEntity.ok(players);
    }
    
    @GetMapping("/{id}/size")
    public ResponseEntity<Long> getTeamSize(@PathVariable Integer id) {
        long size = teamService.getTeamSize(id);
        return ResponseEntity.ok(size);
    }
}