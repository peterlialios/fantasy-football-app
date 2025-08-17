package com.fantasyfootball.service;

import com.fantasyfootball.entity.Player;
import com.fantasyfootball.entity.Team;
import com.fantasyfootball.entity.TeamPlayer;
import com.fantasyfootball.entity.User;
import com.fantasyfootball.repository.PlayerRepository;
import com.fantasyfootball.repository.TeamPlayerRepository;
import com.fantasyfootball.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamPlayerRepository teamPlayerRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private TeamService teamService;

    private Team testTeam;
    private User testUser;
    private Player testPlayer;
    private TeamPlayer testTeamPlayer;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testTeam = new Team();
        testTeam.setId(1);
        testTeam.setName("Test Team");
        testTeam.setOwner(testUser);
        testTeam.setBudget(BigDecimal.valueOf(100.00));
        testTeam.setCreatedAt(LocalDateTime.now());
        testTeam.setUpdatedAt(LocalDateTime.now());

        testPlayer = new Player();
        testPlayer.setId(1);
        testPlayer.setFirstName("Josh");
        testPlayer.setLastName("Allen");
        testPlayer.setPosition("QB");

        testTeamPlayer = new TeamPlayer();
        testTeamPlayer.setId(1);
        testTeamPlayer.setTeam(testTeam);
        testTeamPlayer.setPlayer(testPlayer);
        testTeamPlayer.setPositionOnTeam("QB");
        testTeamPlayer.setCost(BigDecimal.valueOf(15.00));
        testTeamPlayer.setIsStarter(false);
    }

    @Test
    void getAllTeams_ShouldReturnAllTeams() {
        // Given
        List<Team> teams = Arrays.asList(testTeam);
        when(teamRepository.findAll()).thenReturn(teams);

        // When
        List<Team> result = teamService.getAllTeams();

        // Then
        assertEquals(1, result.size());
        assertEquals(testTeam.getId(), result.get(0).getId());
        verify(teamRepository).findAll();
    }

    @Test
    void getTeamById_WhenTeamExists_ShouldReturnTeam() {
        // Given
        when(teamRepository.findById(1)).thenReturn(Optional.of(testTeam));

        // When
        Optional<Team> result = teamService.getTeamById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testTeam.getId(), result.get().getId());
        verify(teamRepository).findById(1);
    }

    @Test
    void getTeamById_WhenTeamNotExists_ShouldReturnEmpty() {
        // Given
        when(teamRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<Team> result = teamService.getTeamById(999);

        // Then
        assertFalse(result.isPresent());
        verify(teamRepository).findById(999);
    }

    @Test
    void saveTeam_ShouldReturnSavedTeam() {
        // Given
        when(teamRepository.save(testTeam)).thenReturn(testTeam);

        // When
        Team result = teamService.saveTeam(testTeam);

        // Then
        assertEquals(testTeam.getId(), result.getId());
        assertEquals(testTeam.getName(), result.getName());
        verify(teamRepository).save(testTeam);
    }

    @Test
    void deleteTeam_ShouldCallRepositoryDelete() {
        // Given
        doNothing().when(teamRepository).deleteById(1);

        // When
        teamService.deleteTeam(1);

        // Then
        verify(teamRepository).deleteById(1);
    }

    @Test
    void getTeamsByOwnerId_ShouldReturnOwnerTeams() {
        // Given
        List<Team> teams = Arrays.asList(testTeam);
        when(teamRepository.findByOwnerId(1)).thenReturn(teams);

        // When
        List<Team> result = teamService.getTeamsByOwnerId(1);

        // Then
        assertEquals(1, result.size());
        assertEquals(testTeam.getOwner().getId(), result.get(0).getOwner().getId());
        verify(teamRepository).findByOwnerId(1);
    }

    @Test
    void addPlayerToTeam_WithValidData_ShouldAddPlayer() {
        // Given
        when(teamRepository.findById(1)).thenReturn(Optional.of(testTeam));
        when(playerRepository.findById(1)).thenReturn(Optional.of(testPlayer));
        when(teamPlayerRepository.findByTeamIdAndPlayerId(1, 1)).thenReturn(Optional.empty());
        when(teamPlayerRepository.save(any(TeamPlayer.class))).thenReturn(testTeamPlayer);

        // When
        TeamPlayer result = teamService.addPlayerToTeam(1, 1, "QB", BigDecimal.valueOf(15.00));

        // Then
        assertNotNull(result);
        assertEquals(testTeam, result.getTeam());
        assertEquals(testPlayer, result.getPlayer());
        assertEquals("QB", result.getPositionOnTeam());
        assertEquals(BigDecimal.valueOf(15.00), result.getCost());
        assertFalse(result.getIsStarter()); // Default should be false
        
        verify(teamRepository).findById(1);
        verify(playerRepository).findById(1);
        verify(teamPlayerRepository).findByTeamIdAndPlayerId(1, 1);
        verify(teamPlayerRepository).save(any(TeamPlayer.class));
    }

    @Test
    void addPlayerToTeam_WhenTeamNotFound_ShouldThrowException() {
        // Given
        when(teamRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> teamService.addPlayerToTeam(999, 1, "QB", BigDecimal.valueOf(15.00)));
        
        assertEquals("Team or Player not found", exception.getMessage());
        verify(teamRepository).findById(999);
        verify(teamPlayerRepository, never()).save(any());
    }

    @Test
    void addPlayerToTeam_WhenPlayerNotFound_ShouldThrowException() {
        // Given
        when(teamRepository.findById(1)).thenReturn(Optional.of(testTeam));
        when(playerRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> teamService.addPlayerToTeam(1, 999, "QB", BigDecimal.valueOf(15.00)));
        
        assertEquals("Team or Player not found", exception.getMessage());
        verify(teamRepository).findById(1);
        verify(playerRepository).findById(999);
        verify(teamPlayerRepository, never()).save(any());
    }

    @Test
    void addPlayerToTeam_WhenPlayerAlreadyOnTeam_ShouldThrowException() {
        // Given
        when(teamRepository.findById(1)).thenReturn(Optional.of(testTeam));
        when(playerRepository.findById(1)).thenReturn(Optional.of(testPlayer));
        when(teamPlayerRepository.findByTeamIdAndPlayerId(1, 1)).thenReturn(Optional.of(testTeamPlayer));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> teamService.addPlayerToTeam(1, 1, "QB", BigDecimal.valueOf(15.00)));
        
        assertEquals("Player already on team", exception.getMessage());
        verify(teamPlayerRepository, never()).save(any());
    }

    @Test
    void removePlayerFromTeam_WhenPlayerOnTeam_ShouldRemovePlayer() {
        // Given
        when(teamPlayerRepository.findByTeamIdAndPlayerId(1, 1)).thenReturn(Optional.of(testTeamPlayer));
        doNothing().when(teamPlayerRepository).delete(testTeamPlayer);

        // When
        teamService.removePlayerFromTeam(1, 1);

        // Then
        verify(teamPlayerRepository).findByTeamIdAndPlayerId(1, 1);
        verify(teamPlayerRepository).delete(testTeamPlayer);
    }

    @Test
    void removePlayerFromTeam_WhenPlayerNotOnTeam_ShouldThrowException() {
        // Given
        when(teamPlayerRepository.findByTeamIdAndPlayerId(1, 999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> teamService.removePlayerFromTeam(1, 999));
        
        assertEquals("Player not found on team", exception.getMessage());
        verify(teamPlayerRepository).findByTeamIdAndPlayerId(1, 999);
        verify(teamPlayerRepository, never()).delete(any());
    }

    @Test
    void updatePlayerStatus_WhenPlayerOnTeam_ShouldUpdateStatus() {
        // Given
        when(teamPlayerRepository.findByTeamIdAndPlayerId(1, 1)).thenReturn(Optional.of(testTeamPlayer));
        when(teamPlayerRepository.save(testTeamPlayer)).thenReturn(testTeamPlayer);

        // When
        TeamPlayer result = teamService.updatePlayerStatus(1, 1, false);

        // Then
        assertNotNull(result);
        assertFalse(result.getIsStarter());
        verify(teamPlayerRepository).findByTeamIdAndPlayerId(1, 1);
        verify(teamPlayerRepository).save(testTeamPlayer);
    }

    @Test
    void updatePlayerStatus_WhenPlayerNotOnTeam_ShouldThrowException() {
        // Given
        when(teamPlayerRepository.findByTeamIdAndPlayerId(1, 999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> teamService.updatePlayerStatus(1, 999, true));
        
        assertEquals("Player not found on team", exception.getMessage());
        verify(teamPlayerRepository).findByTeamIdAndPlayerId(1, 999);
        verify(teamPlayerRepository, never()).save(any());
    }

    @Test
    void getTeamSize_ShouldReturnPlayerCount() {
        // Given
        when(teamPlayerRepository.countByTeamId(1)).thenReturn(5L);

        // When
        long result = teamService.getTeamSize(1);

        // Then
        assertEquals(5L, result);
        verify(teamPlayerRepository).countByTeamId(1);
    }

    @Test
    void getTeamRoster_ShouldReturnAllTeamPlayers() {
        // Given
        List<TeamPlayer> roster = Arrays.asList(testTeamPlayer);
        when(teamPlayerRepository.findByTeamIdWithPlayerDetails(1)).thenReturn(roster);

        // When
        List<TeamPlayer> result = teamService.getTeamRoster(1);

        // Then
        assertEquals(1, result.size());
        assertEquals(testTeamPlayer.getId(), result.get(0).getId());
        verify(teamPlayerRepository).findByTeamIdWithPlayerDetails(1);
    }

    @Test
    void getTeamStarters_ShouldReturnStartingPlayers() {
        // Given
        TeamPlayer starterPlayer = new TeamPlayer();
        starterPlayer.setIsStarter(true);
        List<TeamPlayer> starters = Arrays.asList(starterPlayer);
        when(teamPlayerRepository.findStartersByTeamId(1)).thenReturn(starters);

        // When
        List<TeamPlayer> result = teamService.getTeamStarters(1);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsStarter());
        verify(teamPlayerRepository).findStartersByTeamId(1);
    }

    @Test
    void getTeamBench_ShouldReturnBenchPlayers() {
        // Given
        TeamPlayer benchPlayer = new TeamPlayer();
        benchPlayer.setIsStarter(false);
        List<TeamPlayer> bench = Arrays.asList(benchPlayer);
        when(teamPlayerRepository.findBenchPlayersByTeamId(1)).thenReturn(bench);

        // When
        List<TeamPlayer> result = teamService.getTeamBench(1);

        // Then
        assertEquals(1, result.size());
        assertFalse(result.get(0).getIsStarter());
        verify(teamPlayerRepository).findBenchPlayersByTeamId(1);
    }
}