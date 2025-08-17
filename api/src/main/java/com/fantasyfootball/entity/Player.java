package com.fantasyfootball.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "players")
public class Player {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @NotBlank
    @Column(nullable = false)
    private String position;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nfl_team_id")
    private NflTeam nflTeam;
    
    @Column(name = "jersey_number")
    private Integer jerseyNumber;
    
    @Column(name = "height_inches")
    private Integer heightInches;
    
    @Column(name = "weight_lbs")
    private Integer weightLbs;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Min(0)
    @Column(name = "years_experience")
    private Integer yearsExperience = 0;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal salary;
    
    @Column(name = "fantasy_points", precision = 8, scale = 2)
    private BigDecimal fantasyPoints = BigDecimal.ZERO;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    // Special handling for Defense/Special Teams
    @Column(name = "is_dst")
    private Boolean isDst = false;
    
    @Column(name = "dst_team_name", length = 100)
    private String dstTeamName;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TeamPlayer> teamPlayers;
    
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlayerStats> playerStats;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public NflTeam getNflTeam() {
        return nflTeam;
    }

    public void setNflTeam(NflTeam nflTeam) {
        this.nflTeam = nflTeam;
    }

    public Integer getJerseyNumber() {
        return jerseyNumber;
    }

    public void setJerseyNumber(Integer jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }

    public Integer getHeightInches() {
        return heightInches;
    }

    public void setHeightInches(Integer heightInches) {
        this.heightInches = heightInches;
    }

    public Integer getWeightLbs() {
        return weightLbs;
    }

    public void setWeightLbs(Integer weightLbs) {
        this.weightLbs = weightLbs;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(Integer yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public BigDecimal getFantasyPoints() {
        return fantasyPoints;
    }

    public void setFantasyPoints(BigDecimal fantasyPoints) {
        this.fantasyPoints = fantasyPoints;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<TeamPlayer> getTeamPlayers() {
        return teamPlayers;
    }

    public void setTeamPlayers(List<TeamPlayer> teamPlayers) {
        this.teamPlayers = teamPlayers;
    }

    public List<PlayerStats> getPlayerStats() {
        return playerStats;
    }

    public void setPlayerStats(List<PlayerStats> playerStats) {
        this.playerStats = playerStats;
    }
    
    public Boolean getIsDst() {
        return isDst;
    }
    
    public void setIsDst(Boolean isDst) {
        this.isDst = isDst;
    }
    
    public String getDstTeamName() {
        return dstTeamName;
    }
    
    public void setDstTeamName(String dstTeamName) {
        this.dstTeamName = dstTeamName;
    }
    
    // Convenience method to get player display name
    public String getDisplayName() {
        if (isDst != null && isDst) {
            return dstTeamName;
        }
        return firstName + " " + lastName;
    }
}