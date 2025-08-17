package com.fantasyfootball.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "player_stats", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"player_id", "week", "season"})
})
public class PlayerStats {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;
    
    @Min(1)
    @Column(nullable = false)
    private Integer week;
    
    @Min(2020)
    @Column(nullable = false)
    private Integer season;
    
    @Min(0)
    @Column(name = "games_played")
    private Integer gamesPlayed = 0;
    
    @Column(name = "points_scored", precision = 6, scale = 2)
    private BigDecimal pointsScored = BigDecimal.ZERO;
    
    @Min(0)
    @Column(name = "yards_gained")
    private Integer yardsGained = 0;
    
    @Min(0)
    @Column
    private Integer touchdowns = 0;
    
    @Min(0)
    @Column(name = "field_goals")
    private Integer fieldGoals = 0;
    
    @Min(0)
    @Column
    private Integer interceptions = 0;
    
    @Min(0)
    @Column
    private Integer fumbles = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Integer getSeason() {
        return season;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }

    public Integer getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(Integer gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public BigDecimal getPointsScored() {
        return pointsScored;
    }

    public void setPointsScored(BigDecimal pointsScored) {
        this.pointsScored = pointsScored;
    }

    public Integer getYardsGained() {
        return yardsGained;
    }

    public void setYardsGained(Integer yardsGained) {
        this.yardsGained = yardsGained;
    }

    public Integer getTouchdowns() {
        return touchdowns;
    }

    public void setTouchdowns(Integer touchdowns) {
        this.touchdowns = touchdowns;
    }

    public Integer getFieldGoals() {
        return fieldGoals;
    }

    public void setFieldGoals(Integer fieldGoals) {
        this.fieldGoals = fieldGoals;
    }

    public Integer getInterceptions() {
        return interceptions;
    }

    public void setInterceptions(Integer interceptions) {
        this.interceptions = interceptions;
    }

    public Integer getFumbles() {
        return fumbles;
    }

    public void setFumbles(Integer fumbles) {
        this.fumbles = fumbles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}