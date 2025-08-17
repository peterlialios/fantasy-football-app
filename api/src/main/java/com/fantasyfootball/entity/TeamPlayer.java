package com.fantasyfootball.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_players", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"team_id", "player_id"})
})
public class TeamPlayer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    @JsonBackReference
    private Team team;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;
    
    @NotBlank
    @Column(name = "roster_position", nullable = false, length = 10)
    private String rosterPosition;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roster_position", referencedColumnName = "position_code", insertable = false, updatable = false)
    private RosterPosition rosterPositionEntity;
    
    @Column(name = "acquisition_date")
    private LocalDateTime acquisitionDate;
    
    @Column(precision = 8, scale = 2)
    private BigDecimal cost = BigDecimal.ZERO;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (acquisitionDate == null) {
            acquisitionDate = LocalDateTime.now();
        }
        createdAt = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getRosterPosition() {
        return rosterPosition;
    }

    public void setRosterPosition(String rosterPosition) {
        this.rosterPosition = rosterPosition;
    }
    
    public RosterPosition getRosterPositionEntity() {
        return rosterPositionEntity;
    }
    
    public void setRosterPositionEntity(RosterPosition rosterPositionEntity) {
        this.rosterPositionEntity = rosterPositionEntity;
    }

    public LocalDateTime getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(LocalDateTime acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Convenience method to check if this is a starting position
    public Boolean isStartingPosition() {
        return rosterPositionEntity != null && 
               rosterPositionEntity.getIsStarting() != null && 
               rosterPositionEntity.getIsStarting();
    }
}