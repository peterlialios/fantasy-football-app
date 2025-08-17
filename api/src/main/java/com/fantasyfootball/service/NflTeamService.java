package com.fantasyfootball.service;

import com.fantasyfootball.entity.NflTeam;
import com.fantasyfootball.entity.TeamLogo;
import com.fantasyfootball.entity.TeamLink;
import com.fantasyfootball.repository.NflTeamRepository;
import com.fantasyfootball.repository.TeamLogoRepository;
import com.fantasyfootball.repository.TeamLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NflTeamService {
    
    @Autowired
    private NflTeamRepository nflTeamRepository;
    
    @Autowired
    private TeamLogoRepository teamLogoRepository;
    
    @Autowired
    private TeamLinkRepository teamLinkRepository;
    
    public List<NflTeam> getAllNflTeams() {
        return nflTeamRepository.findAllActiveOrderedByConferenceAndDivision();
    }
    
    public Optional<NflTeam> getNflTeamById(Integer id) {
        return nflTeamRepository.findById(id);
    }
    
    public Optional<NflTeam> getNflTeamByAbbreviation(String abbreviation) {
        return nflTeamRepository.findByAbbreviation(abbreviation);
    }
    
    public Optional<NflTeam> getNflTeamBySlug(String slug) {
        return nflTeamRepository.findBySlug(slug);
    }
    
    public Optional<NflTeam> getNflTeamByEspnId(Integer espnId) {
        return nflTeamRepository.findByEspnId(espnId);
    }
    
    public List<NflTeam> getNflTeamsByConference(String conference) {
        return nflTeamRepository.findByConference(conference);
    }
    
    public List<NflTeam> getNflTeamsByConferenceAndDivision(String conference, String division) {
        return nflTeamRepository.findByConferenceAndDivision(conference, division);
    }
    
    public Optional<NflTeam> getNflTeamWithLogos(Integer id) {
        return nflTeamRepository.findByIdWithLogos(id);
    }
    
    public Optional<NflTeam> getNflTeamWithLinks(Integer id) {
        return nflTeamRepository.findByIdWithLinks(id);
    }
    
    public Optional<NflTeam> getNflTeamWithLogosAndLinks(Integer id) {
        return nflTeamRepository.findByIdWithLogosAndLinks(id);
    }
    
    public List<TeamLogo> getTeamLogos(Integer teamId) {
        return teamLogoRepository.findByTeamId(teamId);
    }
    
    public Optional<TeamLogo> getDefaultLogo(Integer teamId) {
        return teamLogoRepository.findDefaultLogoByTeamId(teamId);
    }
    
    public Optional<TeamLogo> getScoreboardLogo(Integer teamId) {
        return teamLogoRepository.findScoreboardLogoByTeamId(teamId);
    }
    
    public List<TeamLink> getTeamLinks(Integer teamId) {
        return teamLinkRepository.findByTeamId(teamId);
    }
    
    public Optional<TeamLink> getClubhouseLink(Integer teamId) {
        return teamLinkRepository.findClubhouseLinkByTeamId(teamId);
    }
    
    public Optional<TeamLink> getRosterLink(Integer teamId) {
        return teamLinkRepository.findRosterLinkByTeamId(teamId);
    }
    
    public Optional<TeamLink> getStatsLink(Integer teamId) {
        return teamLinkRepository.findStatsLinkByTeamId(teamId);
    }
    
    public List<String> getAllConferences() {
        return nflTeamRepository.findDistinctConferences();
    }
    
    public List<String> getDivisionsByConference(String conference) {
        return nflTeamRepository.findDistinctDivisionsByConference(conference);
    }
    
    public NflTeam saveNflTeam(NflTeam nflTeam) {
        return nflTeamRepository.save(nflTeam);
    }
}