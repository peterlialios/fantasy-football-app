package com.fantasyfootball.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_logos")
public class TeamLogo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    @JsonBackReference
    private NflTeam team;
    
    @Column(nullable = false, length = 500)
    private String href;
    
    @Column(length = 255)
    private String alt;
    
    @Column
    private Integer width;
    
    @Column
    private Integer height;
    
    // Logo relationship types
    @Column(name = "rel_full")
    private Boolean relFull = false;
    
    @Column(name = "rel_default")
    private Boolean relDefault = false;
    
    @Column(name = "rel_dark")
    private Boolean relDark = false;
    
    @Column(name = "rel_scoreboard")
    private Boolean relScoreboard = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public NflTeam getTeam() {
        return team;
    }
    
    public void setTeam(NflTeam team) {
        this.team = team;
    }
    
    public String getHref() {
        return href;
    }
    
    public void setHref(String href) {
        this.href = href;
    }
    
    public String getAlt() {
        return alt;
    }
    
    public void setAlt(String alt) {
        this.alt = alt;
    }
    
    public Integer getWidth() {
        return width;
    }
    
    public void setWidth(Integer width) {
        this.width = width;
    }
    
    public Integer getHeight() {
        return height;
    }
    
    public void setHeight(Integer height) {
        this.height = height;
    }
    
    public Boolean getRelFull() {
        return relFull;
    }
    
    public void setRelFull(Boolean relFull) {
        this.relFull = relFull;
    }
    
    public Boolean getRelDefault() {
        return relDefault;
    }
    
    public void setRelDefault(Boolean relDefault) {
        this.relDefault = relDefault;
    }
    
    public Boolean getRelDark() {
        return relDark;
    }
    
    public void setRelDark(Boolean relDark) {
        this.relDark = relDark;
    }
    
    public Boolean getRelScoreboard() {
        return relScoreboard;
    }
    
    public void setRelScoreboard(Boolean relScoreboard) {
        this.relScoreboard = relScoreboard;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}