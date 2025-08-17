package com.fantasyfootball.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
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
    
    @Column(name = "position_on_team")
    private String positionOnTeam;
    
    @Column(name = "acquisition_date")
    private LocalDateTime acquisitionDate;
    
    @Column(precision = 8, scale = 2)
    private BigDecimal cost = BigDecimal.ZERO;
    
    @Column(name = "is_starter")
    private Boolean isStarter = false;
    
    @PrePersist
    protected void onCreate() {
        if (acquisitionDate == null) {
            acquisitionDate = LocalDateTime.now();
        }
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

    public String getPositionOnTeam() {
        return positionOnTeam;
    }

    public void setPositionOnTeam(String positionOnTeam) {
        this.positionOnTeam = positionOnTeam;
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

    public Boolean getIsStarter() {
        return isStarter;
    }

    public void setIsStarter(Boolean isStarter) {
        this.isStarter = isStarter;
    }
}