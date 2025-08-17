package com.fantasyfootball.controller;

import com.fantasyfootball.entity.NflTeam;
import com.fantasyfootball.entity.TeamLogo;
import com.fantasyfootball.entity.TeamLink;
import com.fantasyfootball.service.NflTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/nfl-teams")
@CrossOrigin(origins = "http://localhost:3000")
public class NflTeamController {
    
    @Autowired
    private NflTeamService nflTeamService;
    
    @GetMapping
    public ResponseEntity<List<NflTeam>> getAllNflTeams() {
        List<NflTeam> teams = nflTeamService.getAllNflTeams();
        return ResponseEntity.ok(teams);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<NflTeam> getNflTeamById(@PathVariable Integer id) {
        Optional<NflTeam> team = nflTeamService.getNflTeamById(id);
        return team.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/abbreviation/{abbreviation}")
    public ResponseEntity<NflTeam> getNflTeamByAbbreviation(@PathVariable String abbreviation) {
        Optional<NflTeam> team = nflTeamService.getNflTeamByAbbreviation(abbreviation);
        return team.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/slug/{slug}")
    public ResponseEntity<NflTeam> getNflTeamBySlug(@PathVariable String slug) {
        Optional<NflTeam> team = nflTeamService.getNflTeamBySlug(slug);
        return team.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/espn-id/{espnId}")
    public ResponseEntity<NflTeam> getNflTeamByEspnId(@PathVariable Integer espnId) {
        Optional<NflTeam> team = nflTeamService.getNflTeamByEspnId(espnId);
        return team.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/conference/{conference}")
    public ResponseEntity<List<NflTeam>> getNflTeamsByConference(@PathVariable String conference) {
        List<NflTeam> teams = nflTeamService.getNflTeamsByConference(conference);
        return ResponseEntity.ok(teams);
    }
    
    @GetMapping("/conference/{conference}/division/{division}")
    public ResponseEntity<List<NflTeam>> getNflTeamsByConferenceAndDivision(
            @PathVariable String conference,
            @PathVariable String division) {
        List<NflTeam> teams = nflTeamService.getNflTeamsByConferenceAndDivision(conference, division);
        return ResponseEntity.ok(teams);
    }
    
    @GetMapping("/{id}/with-logos")
    public ResponseEntity<NflTeam> getNflTeamWithLogos(@PathVariable Integer id) {
        Optional<NflTeam> team = nflTeamService.getNflTeamWithLogos(id);
        return team.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/with-links")
    public ResponseEntity<NflTeam> getNflTeamWithLinks(@PathVariable Integer id) {
        Optional<NflTeam> team = nflTeamService.getNflTeamWithLinks(id);
        return team.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/with-logos-and-links")
    public ResponseEntity<NflTeam> getNflTeamWithLogosAndLinks(@PathVariable Integer id) {
        Optional<NflTeam> team = nflTeamService.getNflTeamWithLogosAndLinks(id);
        return team.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/logos")
    public ResponseEntity<List<TeamLogo>> getTeamLogos(@PathVariable Integer id) {
        List<TeamLogo> logos = nflTeamService.getTeamLogos(id);
        return ResponseEntity.ok(logos);
    }
    
    @GetMapping("/{id}/logos/default")
    public ResponseEntity<TeamLogo> getDefaultLogo(@PathVariable Integer id) {
        Optional<TeamLogo> logo = nflTeamService.getDefaultLogo(id);
        return logo.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/logos/scoreboard")
    public ResponseEntity<TeamLogo> getScoreboardLogo(@PathVariable Integer id) {
        Optional<TeamLogo> logo = nflTeamService.getScoreboardLogo(id);
        return logo.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/links")
    public ResponseEntity<List<TeamLink>> getTeamLinks(@PathVariable Integer id) {
        List<TeamLink> links = nflTeamService.getTeamLinks(id);
        return ResponseEntity.ok(links);
    }
    
    @GetMapping("/{id}/links/clubhouse")
    public ResponseEntity<TeamLink> getClubhouseLink(@PathVariable Integer id) {
        Optional<TeamLink> link = nflTeamService.getClubhouseLink(id);
        return link.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/links/roster")
    public ResponseEntity<TeamLink> getRosterLink(@PathVariable Integer id) {
        Optional<TeamLink> link = nflTeamService.getRosterLink(id);
        return link.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/links/stats")
    public ResponseEntity<TeamLink> getStatsLink(@PathVariable Integer id) {
        Optional<TeamLink> link = nflTeamService.getStatsLink(id);
        return link.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/conferences")
    public ResponseEntity<List<String>> getAllConferences() {
        List<String> conferences = nflTeamService.getAllConferences();
        return ResponseEntity.ok(conferences);
    }
    
    @GetMapping("/conferences/{conference}/divisions")
    public ResponseEntity<List<String>> getDivisionsByConference(@PathVariable String conference) {
        List<String> divisions = nflTeamService.getDivisionsByConference(conference);
        return ResponseEntity.ok(divisions);
    }
}