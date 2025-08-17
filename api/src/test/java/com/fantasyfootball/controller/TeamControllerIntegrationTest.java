package com.fantasyfootball.controller;

import com.fantasyfootball.entity.Team;
import com.fantasyfootball.entity.User;
import com.fantasyfootball.repository.TeamRepository;
import com.fantasyfootball.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class TeamControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("fantasy_football_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        teamRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedpassword");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        testUser = userRepository.save(testUser);
    }

    @Test
    @WithMockUser
    void createTeam_ShouldPersistToDatabase() throws Exception {
        // Given
        Team newTeam = new Team();
        newTeam.setName("Integration Test Team");
        newTeam.setOwner(testUser);
        newTeam.setBudget(BigDecimal.valueOf(100.00));

        // When & Then
        mockMvc.perform(post("/teams")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTeam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Test Team"))
                .andExpect(jsonPath("$.budget").value(100.00))
                .andExpect(jsonPath("$.owner.id").value(testUser.getId()));

        // Verify data persisted
        assert teamRepository.count() == 1;
        Team savedTeam = teamRepository.findAll().get(0);
        assert savedTeam.getName().equals("Integration Test Team");
        assert savedTeam.getOwner().getId().equals(testUser.getId());
    }

    @Test
    @WithMockUser
    void getAllTeams_ShouldReturnPersistedTeams() throws Exception {
        // Given - Create test teams
        Team team1 = new Team();
        team1.setName("Team One");
        team1.setOwner(testUser);
        team1.setBudget(BigDecimal.valueOf(100.00));
        team1.setCreatedAt(LocalDateTime.now());
        team1.setUpdatedAt(LocalDateTime.now());

        Team team2 = new Team();
        team2.setName("Team Two");
        team2.setOwner(testUser);
        team2.setBudget(BigDecimal.valueOf(150.00));
        team2.setCreatedAt(LocalDateTime.now());
        team2.setUpdatedAt(LocalDateTime.now());

        teamRepository.save(team1);
        teamRepository.save(team2);

        // When & Then
        mockMvc.perform(get("/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Team One"))
                .andExpect(jsonPath("$[1].name").value("Team Two"));
    }

    @Test
    @WithMockUser
    void getTeamById_ShouldReturnSpecificTeam() throws Exception {
        // Given
        Team team = new Team();
        team.setName("Specific Team");
        team.setOwner(testUser);
        team.setBudget(BigDecimal.valueOf(100.00));
        team.setCreatedAt(LocalDateTime.now());
        team.setUpdatedAt(LocalDateTime.now());
        team = teamRepository.save(team);

        // When & Then
        mockMvc.perform(get("/teams/" + team.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(team.getId()))
                .andExpect(jsonPath("$.name").value("Specific Team"));
    }

    @Test
    @WithMockUser
    void updateTeam_ShouldModifyExistingTeam() throws Exception {
        // Given
        Team team = new Team();
        team.setName("Original Team");
        team.setOwner(testUser);
        team.setBudget(BigDecimal.valueOf(100.00));
        team.setCreatedAt(LocalDateTime.now());
        team.setUpdatedAt(LocalDateTime.now());
        team = teamRepository.save(team);

        Team updateData = new Team();
        updateData.setName("Updated Team");
        updateData.setBudget(BigDecimal.valueOf(200.00));

        // When & Then
        mockMvc.perform(put("/teams/" + team.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Team"))
                .andExpect(jsonPath("$.budget").value(200.00));

        // Verify database was updated
        Team updatedTeam = teamRepository.findById(team.getId()).orElseThrow();
        assert updatedTeam.getName().equals("Updated Team");
        assert updatedTeam.getBudget().equals(BigDecimal.valueOf(200.00));
    }

    @Test
    @WithMockUser
    void deleteTeam_ShouldRemoveFromDatabase() throws Exception {
        // Given
        Team team = new Team();
        team.setName("Team to Delete");
        team.setOwner(testUser);
        team.setBudget(BigDecimal.valueOf(100.00));
        team.setCreatedAt(LocalDateTime.now());
        team.setUpdatedAt(LocalDateTime.now());
        team = teamRepository.save(team);

        assert teamRepository.count() == 1;

        // When & Then
        mockMvc.perform(delete("/teams/" + team.getId()).with(csrf()))
                .andExpect(status().isOk());

        // Verify deletion
        assert teamRepository.count() == 0;
        assert teamRepository.findById(team.getId()).isEmpty();
    }

    @Test
    @WithMockUser
    void getTeamsByOwner_ShouldReturnOwnerSpecificTeams() throws Exception {
        // Given - Create another user and teams for both users
        User anotherUser = new User();
        anotherUser.setUsername("anotheruser");
        anotherUser.setEmail("another@example.com");
        anotherUser.setPasswordHash("hashedpassword");
        anotherUser.setCreatedAt(LocalDateTime.now());
        anotherUser.setUpdatedAt(LocalDateTime.now());
        anotherUser = userRepository.save(anotherUser);

        Team team1 = new Team();
        team1.setName("User1 Team");
        team1.setOwner(testUser);
        team1.setBudget(BigDecimal.valueOf(100.00));
        team1.setCreatedAt(LocalDateTime.now());
        team1.setUpdatedAt(LocalDateTime.now());

        Team team2 = new Team();
        team2.setName("User2 Team");
        team2.setOwner(anotherUser);
        team2.setBudget(BigDecimal.valueOf(150.00));
        team2.setCreatedAt(LocalDateTime.now());
        team2.setUpdatedAt(LocalDateTime.now());

        teamRepository.save(team1);
        teamRepository.save(team2);

        // When & Then - Get teams for first user
        mockMvc.perform(get("/teams/owner/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("User1 Team"))
                .andExpect(jsonPath("$[0].owner.id").value(testUser.getId()));
    }
}