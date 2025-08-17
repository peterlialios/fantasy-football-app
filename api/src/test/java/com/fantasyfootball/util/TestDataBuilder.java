package com.fantasyfootball.util;

import com.fantasyfootball.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Utility class for building test data objects.
 * Provides fluent builder methods for creating test entities.
 */
public class TestDataBuilder {

    public static UserBuilder user() {
        return new UserBuilder();
    }

    public static TeamBuilder team() {
        return new TeamBuilder();
    }

    public static PlayerBuilder player() {
        return new PlayerBuilder();
    }

    public static NflTeamBuilder nflTeam() {
        return new NflTeamBuilder();
    }

    public static TeamPlayerBuilder teamPlayer() {
        return new TeamPlayerBuilder();
    }

    public static class UserBuilder {
        private User user = new User();

        public UserBuilder() {
            user.setUsername("testuser");
            user.setEmail("test@example.com");
            user.setPasswordHash("hashedpassword");
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
        }

        public UserBuilder withId(Integer id) {
            user.setId(id);
            return this;
        }

        public UserBuilder withUsername(String username) {
            user.setUsername(username);
            return this;
        }

        public UserBuilder withEmail(String email) {
            user.setEmail(email);
            return this;
        }

        public User build() {
            return user;
        }
    }

    public static class TeamBuilder {
        private Team team = new Team();

        public TeamBuilder() {
            team.setName("Test Team");
            team.setBudget(BigDecimal.valueOf(100.00));
            team.setCreatedAt(LocalDateTime.now());
            team.setUpdatedAt(LocalDateTime.now());
        }

        public TeamBuilder withId(Integer id) {
            team.setId(id);
            return this;
        }

        public TeamBuilder withName(String name) {
            team.setName(name);
            return this;
        }

        public TeamBuilder withOwner(User owner) {
            team.setOwner(owner);
            return this;
        }

        public TeamBuilder withBudget(BigDecimal budget) {
            team.setBudget(budget);
            return this;
        }

        public Team build() {
            return team;
        }
    }

    public static class PlayerBuilder {
        private Player player = new Player();

        public PlayerBuilder() {
            player.setFirstName("Test");
            player.setLastName("Player");
            player.setPosition("QB");
            player.setJerseyNumber(1);
            player.setHeightInches(72);
            player.setWeightLbs(200);
            player.setYearsExperience(5);
            player.setFantasyPoints(BigDecimal.valueOf(200.0));
            player.setIsActive(true);
            player.setCreatedAt(LocalDateTime.now());
            player.setUpdatedAt(LocalDateTime.now());
        }

        public PlayerBuilder withId(Integer id) {
            player.setId(id);
            return this;
        }

        public PlayerBuilder withName(String firstName, String lastName) {
            player.setFirstName(firstName);
            player.setLastName(lastName);
            return this;
        }

        public PlayerBuilder withPosition(String position) {
            player.setPosition(position);
            return this;
        }

        public PlayerBuilder withNflTeam(NflTeam nflTeam) {
            player.setNflTeam(nflTeam);
            return this;
        }

        public PlayerBuilder withJerseyNumber(Integer jerseyNumber) {
            player.setJerseyNumber(jerseyNumber);
            return this;
        }

        public PlayerBuilder withFantasyPoints(BigDecimal fantasyPoints) {
            player.setFantasyPoints(fantasyPoints);
            return this;
        }

        public PlayerBuilder active(boolean isActive) {
            player.setIsActive(isActive);
            return this;
        }

        public Player build() {
            return player;
        }
    }

    public static class NflTeamBuilder {
        private NflTeam nflTeam = new NflTeam();

        public NflTeamBuilder() {
            nflTeam.setName("Test Team");
            nflTeam.setAbbreviation("TST");
            nflTeam.setCity("Test City");
            nflTeam.setConference("AFC");
            nflTeam.setDivision("North");
        }

        public NflTeamBuilder withId(Integer id) {
            nflTeam.setId(id);
            return this;
        }

        public NflTeamBuilder withName(String name) {
            nflTeam.setName(name);
            return this;
        }

        public NflTeamBuilder withAbbreviation(String abbreviation) {
            nflTeam.setAbbreviation(abbreviation);
            return this;
        }

        public NflTeamBuilder withCity(String city) {
            nflTeam.setCity(city);
            return this;
        }

        public NflTeamBuilder withConference(String conference) {
            nflTeam.setConference(conference);
            return this;
        }

        public NflTeamBuilder withDivision(String division) {
            nflTeam.setDivision(division);
            return this;
        }

        public NflTeam build() {
            return nflTeam;
        }
    }

    public static class TeamPlayerBuilder {
        private TeamPlayer teamPlayer = new TeamPlayer();

        public TeamPlayerBuilder() {
            teamPlayer.setPositionOnTeam("QB");
            teamPlayer.setCost(BigDecimal.valueOf(10.0));
            teamPlayer.setIsStarter(false);
            teamPlayer.setAcquisitionDate(LocalDateTime.now());
        }

        public TeamPlayerBuilder withId(Integer id) {
            teamPlayer.setId(id);
            return this;
        }

        public TeamPlayerBuilder withTeam(Team team) {
            teamPlayer.setTeam(team);
            return this;
        }

        public TeamPlayerBuilder withPlayer(Player player) {
            teamPlayer.setPlayer(player);
            return this;
        }

        public TeamPlayerBuilder withPosition(String position) {
            teamPlayer.setPositionOnTeam(position);
            return this;
        }

        public TeamPlayerBuilder withCost(BigDecimal cost) {
            teamPlayer.setCost(cost);
            return this;
        }

        public TeamPlayerBuilder starter(boolean isStarter) {
            teamPlayer.setIsStarter(isStarter);
            return this;
        }

        public TeamPlayer build() {
            return teamPlayer;
        }
    }
}