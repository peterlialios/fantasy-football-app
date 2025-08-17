package com.fantasyfootball.controller;

import com.fantasyfootball.entity.Team;
import com.fantasyfootball.entity.TeamPlayer;
import com.fantasyfootball.entity.User;
import com.fantasyfootball.service.TeamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeamController.class)
public class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    @Autowired
    private ObjectMapper objectMapper;

    private Team testTeam;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testTeam = new Team();
        testTeam.setId(1);
        testTeam.setName("Test Team");
        testTeam.setOwner(testUser);
        testTeam.setBudget(BigDecimal.valueOf(100.00));
        testTeam.setCreatedAt(LocalDateTime.now());
        testTeam.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser
    void getAllTeams_ShouldReturnTeamsList() throws Exception {
        // Given
        List<Team> teams = Arrays.asList(testTeam);
        when(teamService.getAllTeams()).thenReturn(teams);

        // When & Then
        mockMvc.perform(get("/teams"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Team"))
                .andExpect(jsonPath("$[0].budget").value(100.00));

        verify(teamService).getAllTeams();
    }

    @Test
    @WithMockUser
    void getTeamById_WhenTeamExists_ShouldReturnTeam() throws Exception {
        // Given
        when(teamService.getTeamById(1)).thenReturn(Optional.of(testTeam));

        // When & Then
        mockMvc.perform(get("/teams/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Team"));

        verify(teamService).getTeamById(1);
    }

    @Test
    @WithMockUser
    void getTeamById_WhenTeamNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(teamService.getTeamById(999)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/teams/999"))
                .andExpect(status().isNotFound());

        verify(teamService).getTeamById(999);
    }

    @Test
    @WithMockUser
    void createTeam_WithValidData_ShouldCreateTeam() throws Exception {
        // Given
        Team newTeam = new Team();
        newTeam.setName("New Team");
        newTeam.setOwner(testUser);
        newTeam.setBudget(BigDecimal.valueOf(100.00));

        when(teamService.saveTeam(any(Team.class))).thenReturn(testTeam);

        // When & Then
        mockMvc.perform(post("/teams")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTeam)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Team"));

        verify(teamService).saveTeam(any(Team.class));
    }

    @Test
    @WithMockUser
    void createTeam_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        when(teamService.saveTeam(any(Team.class))).thenThrow(new RuntimeException("Invalid data"));

        Team invalidTeam = new Team();
        invalidTeam.setName("");

        // When & Then
        mockMvc.perform(post("/teams")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTeam)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateTeam_WhenTeamExists_ShouldUpdateTeam() throws Exception {
        // Given
        Team updatedTeam = new Team();
        updatedTeam.setName("Updated Team");
        updatedTeam.setBudget(BigDecimal.valueOf(150.00));

        when(teamService.getTeamById(1)).thenReturn(Optional.of(testTeam));
        when(teamService.saveTeam(any(Team.class))).thenReturn(testTeam);

        // When & Then
        mockMvc.perform(put("/teams/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTeam)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(teamService).getTeamById(1);
        verify(teamService).saveTeam(any(Team.class));
    }

    @Test
    @WithMockUser
    void updateTeam_WhenTeamNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(teamService.getTeamById(999)).thenReturn(Optional.empty());

        Team updatedTeam = new Team();
        updatedTeam.setName("Updated Team");

        // When & Then
        mockMvc.perform(put("/teams/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTeam)))
                .andExpect(status().isNotFound());

        verify(teamService).getTeamById(999);
        verify(teamService, never()).saveTeam(any(Team.class));
    }

    @Test
    @WithMockUser
    void deleteTeam_WhenTeamExists_ShouldDeleteTeam() throws Exception {
        // Given
        doNothing().when(teamService).deleteTeam(1);

        // When & Then
        mockMvc.perform(delete("/teams/1").with(csrf()))
                .andExpect(status().isOk());

        verify(teamService).deleteTeam(1);
    }

    @Test
    @WithMockUser
    void deleteTeam_WhenTeamNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Team not found")).when(teamService).deleteTeam(999);

        // When & Then
        mockMvc.perform(delete("/teams/999").with(csrf()))
                .andExpect(status().isNotFound());

        verify(teamService).deleteTeam(999);
    }

    @Test
    @WithMockUser
    void getTeamsByOwner_ShouldReturnOwnerTeams() throws Exception {
        // Given
        List<Team> teams = Arrays.asList(testTeam);
        when(teamService.getTeamsByOwnerId(1)).thenReturn(teams);

        // When & Then
        mockMvc.perform(get("/teams/owner/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Team"));

        verify(teamService).getTeamsByOwnerId(1);
    }

    @Test
    @WithMockUser
    void getTeamRoster_ShouldReturnTeamPlayers() throws Exception {
        // Given
        List<TeamPlayer> roster = Arrays.asList(new TeamPlayer());
        when(teamService.getTeamRoster(1)).thenReturn(roster);

        // When & Then
        mockMvc.perform(get("/teams/1/roster"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(teamService).getTeamRoster(1);
    }

    @Test
    @WithMockUser
    void getTeamStarters_ShouldReturnStartingPlayers() throws Exception {
        // Given
        List<TeamPlayer> starters = Arrays.asList(new TeamPlayer());
        when(teamService.getTeamStarters(1)).thenReturn(starters);

        // When & Then
        mockMvc.perform(get("/teams/1/starters"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(teamService).getTeamStarters(1);
    }

    @Test
    @WithMockUser
    void getTeamBench_ShouldReturnBenchPlayers() throws Exception {
        // Given
        List<TeamPlayer> bench = Arrays.asList(new TeamPlayer());
        when(teamService.getTeamBench(1)).thenReturn(bench);

        // When & Then
        mockMvc.perform(get("/teams/1/bench"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(teamService).getTeamBench(1);
    }

    @Test
    @WithMockUser
    void addPlayerToTeam_WithValidData_ShouldAddPlayer() throws Exception {
        // Given
        TeamPlayer teamPlayer = new TeamPlayer();
        when(teamService.addPlayerToTeam(eq(1), eq(2), eq("QB"), any(BigDecimal.class)))
                .thenReturn(teamPlayer);

        // When & Then
        mockMvc.perform(post("/teams/1/players/2")
                        .with(csrf())
                        .param("positionOnTeam", "QB")
                        .param("cost", "10.0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(teamService).addPlayerToTeam(eq(1), eq(2), eq("QB"), any(BigDecimal.class));
    }

    @Test
    @WithMockUser
    void addPlayerToTeam_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        when(teamService.addPlayerToTeam(eq(1), eq(2), any(), any(BigDecimal.class)))
                .thenThrow(new RuntimeException("Player already on team"));

        // When & Then
        mockMvc.perform(post("/teams/1/players/2")
                        .with(csrf())
                        .param("positionOnTeam", "QB")
                        .param("cost", "10.0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void removePlayerFromTeam_WithValidData_ShouldRemovePlayer() throws Exception {
        // Given
        doNothing().when(teamService).removePlayerFromTeam(1, 2);

        // When & Then
        mockMvc.perform(delete("/teams/1/players/2").with(csrf()))
                .andExpect(status().isOk());

        verify(teamService).removePlayerFromTeam(1, 2);
    }

    @Test
    @WithMockUser
    void removePlayerFromTeam_WhenPlayerNotOnTeam_ShouldReturnNotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Player not found on team"))
                .when(teamService).removePlayerFromTeam(1, 999);

        // When & Then
        mockMvc.perform(delete("/teams/1/players/999").with(csrf()))
                .andExpect(status().isNotFound());

        verify(teamService).removePlayerFromTeam(1, 999);
    }

    @Test
    @WithMockUser
    void getTeamSize_ShouldReturnSize() throws Exception {
        // Given
        when(teamService.getTeamSize(1)).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/teams/1/size"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(5));

        verify(teamService).getTeamSize(1);
    }
}