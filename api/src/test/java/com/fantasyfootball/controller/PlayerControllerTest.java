package com.fantasyfootball.controller;

import com.fantasyfootball.entity.NflTeam;
import com.fantasyfootball.entity.Player;
import com.fantasyfootball.service.PlayerService;
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

@WebMvcTest(PlayerController.class)
public class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Player testPlayer;
    private NflTeam testNflTeam;

    @BeforeEach
    void setUp() {
        testNflTeam = new NflTeam();
        testNflTeam.setId(1);
        testNflTeam.setName("Bills");
        testNflTeam.setAbbreviation("BUF");
        testNflTeam.setCity("Buffalo");
        testNflTeam.setConference("AFC");
        testNflTeam.setDivision("East");

        testPlayer = new Player();
        testPlayer.setId(1);
        testPlayer.setFirstName("Josh");
        testPlayer.setLastName("Allen");
        testPlayer.setPosition("QB");
        testPlayer.setNflTeam(testNflTeam);
        testPlayer.setJerseyNumber(17);
        testPlayer.setHeightInches(77);
        testPlayer.setWeightLbs(237);
        testPlayer.setYearsExperience(6);
        testPlayer.setFantasyPoints(BigDecimal.valueOf(298.80));
        testPlayer.setIsActive(true);
        testPlayer.setCreatedAt(LocalDateTime.now());
        testPlayer.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser
    void getAllPlayers_ShouldReturnPlayersList() throws Exception {
        // Given
        List<Player> players = Arrays.asList(testPlayer);
        when(playerService.getAllPlayers()).thenReturn(players);

        // When & Then
        mockMvc.perform(get("/players"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Josh"))
                .andExpect(jsonPath("$[0].lastName").value("Allen"))
                .andExpect(jsonPath("$[0].position").value("QB"));

        verify(playerService).getAllPlayers();
    }

    @Test
    @WithMockUser
    void getActivePlayers_ShouldReturnActivePlayersList() throws Exception {
        // Given
        List<Player> players = Arrays.asList(testPlayer);
        when(playerService.getActivePlayers()).thenReturn(players);

        // When & Then
        mockMvc.perform(get("/players/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].isActive").value(true));

        verify(playerService).getActivePlayers();
    }

    @Test
    @WithMockUser
    void getPlayerById_WhenPlayerExists_ShouldReturnPlayer() throws Exception {
        // Given
        when(playerService.getPlayerById(1)).thenReturn(Optional.of(testPlayer));

        // When & Then
        mockMvc.perform(get("/players/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Josh"))
                .andExpect(jsonPath("$.lastName").value("Allen"));

        verify(playerService).getPlayerById(1);
    }

    @Test
    @WithMockUser
    void getPlayerById_WhenPlayerNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(playerService.getPlayerById(999)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/players/999"))
                .andExpect(status().isNotFound());

        verify(playerService).getPlayerById(999);
    }

    @Test
    @WithMockUser
    void getPlayerWithNflTeam_WhenPlayerExists_ShouldReturnPlayerWithTeam() throws Exception {
        // Given
        when(playerService.getPlayerWithNflTeam(1)).thenReturn(Optional.of(testPlayer));

        // When & Then
        mockMvc.perform(get("/players/1/with-team"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nflTeam.name").value("Bills"));

        verify(playerService).getPlayerWithNflTeam(1);
    }

    @Test
    @WithMockUser
    void getPlayerWithStats_WhenPlayerExists_ShouldReturnPlayerWithStats() throws Exception {
        // Given
        when(playerService.getPlayerWithStats(1)).thenReturn(Optional.of(testPlayer));

        // When & Then
        mockMvc.perform(get("/players/1/with-stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(playerService).getPlayerWithStats(1);
    }

    @Test
    @WithMockUser
    void getPlayersByPosition_ShouldReturnPlayersInPosition() throws Exception {
        // Given
        List<Player> players = Arrays.asList(testPlayer);
        when(playerService.getPlayersByPosition("QB")).thenReturn(players);

        // When & Then
        mockMvc.perform(get("/players/position/QB"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].position").value("QB"));

        verify(playerService).getPlayersByPosition("QB");
    }

    @Test
    @WithMockUser
    void getPlayersByNflTeam_ShouldReturnTeamPlayers() throws Exception {
        // Given
        List<Player> players = Arrays.asList(testPlayer);
        when(playerService.getPlayersByNflTeam(1)).thenReturn(players);

        // When & Then
        mockMvc.perform(get("/players/nfl-team/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nflTeam.id").value(1));

        verify(playerService).getPlayersByNflTeam(1);
    }

    @Test
    @WithMockUser
    void searchPlayersByName_ShouldReturnMatchingPlayers() throws Exception {
        // Given
        List<Player> players = Arrays.asList(testPlayer);
        when(playerService.searchPlayersByName("Allen")).thenReturn(players);

        // When & Then
        mockMvc.perform(get("/players/search").param("name", "Allen"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].lastName").value("Allen"));

        verify(playerService).searchPlayersByName("Allen");
    }

    @Test
    @WithMockUser
    void getAvailablePlayersNotOnTeam_ShouldReturnAvailablePlayers() throws Exception {
        // Given
        List<Player> players = Arrays.asList(testPlayer);
        when(playerService.getAvailablePlayersNotOnTeam(1)).thenReturn(players);

        // When & Then
        mockMvc.perform(get("/players/available/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(playerService).getAvailablePlayersNotOnTeam(1);
    }

    @Test
    @WithMockUser
    void createPlayer_WithValidData_ShouldCreatePlayer() throws Exception {
        // Given
        Player newPlayer = new Player();
        newPlayer.setFirstName("New");
        newPlayer.setLastName("Player");
        newPlayer.setPosition("RB");

        when(playerService.savePlayer(any(Player.class))).thenReturn(testPlayer);

        // When & Then
        mockMvc.perform(post("/players")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPlayer)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(playerService).savePlayer(any(Player.class));
    }

    @Test
    @WithMockUser
    void updatePlayer_WhenPlayerExists_ShouldUpdatePlayer() throws Exception {
        // Given
        Player updatedPlayer = new Player();
        updatedPlayer.setFirstName("Updated");
        updatedPlayer.setLastName("Player");

        when(playerService.updatePlayer(eq(1), any(Player.class))).thenReturn(testPlayer);

        // When & Then
        mockMvc.perform(put("/players/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPlayer)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(playerService).updatePlayer(eq(1), any(Player.class));
    }

    @Test
    @WithMockUser
    void updatePlayer_WhenPlayerNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        Player updatedPlayer = new Player();
        updatedPlayer.setFirstName("Updated");

        when(playerService.updatePlayer(eq(999), any(Player.class)))
                .thenThrow(new RuntimeException("Player not found with id: 999"));

        // When & Then
        mockMvc.perform(put("/players/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPlayer)))
                .andExpect(status().isNotFound());

        verify(playerService).updatePlayer(eq(999), any(Player.class));
    }

    @Test
    @WithMockUser
    void deletePlayer_WhenPlayerExists_ShouldDeletePlayer() throws Exception {
        // Given
        doNothing().when(playerService).deletePlayer(1);

        // When & Then
        mockMvc.perform(delete("/players/1").with(csrf()))
                .andExpect(status().isOk());

        verify(playerService).deletePlayer(1);
    }

    @Test
    @WithMockUser
    void deletePlayer_WhenPlayerNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Player not found")).when(playerService).deletePlayer(999);

        // When & Then
        mockMvc.perform(delete("/players/999").with(csrf()))
                .andExpect(status().isNotFound());

        verify(playerService).deletePlayer(999);
    }
}