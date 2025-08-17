package com.fantasyfootball.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_links")
public class TeamLink {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    @JsonBackReference
    private NflTeam team;
    
    @Column(length = 10)
    private String language = "en-US";
    
    @Column(nullable = false, length = 500)
    private String href;
    
    @Column(length = 100)
    private String text;
    
    @Column(name = "short_text", length = 50)
    private String shortText;
    
    // Link relationship types
    @Column(name = "rel_clubhouse")
    private Boolean relClubhouse = false;
    
    @Column(name = "rel_roster")
    private Boolean relRoster = false;
    
    @Column(name = "rel_stats")
    private Boolean relStats = false;
    
    @Column(name = "rel_schedule")
    private Boolean relSchedule = false;
    
    @Column(name = "rel_tickets")
    private Boolean relTickets = false;
    
    @Column(name = "rel_depthchart")
    private Boolean relDepthchart = false;
    
    @Column(name = "rel_desktop")
    private Boolean relDesktop = false;
    
    @Column(name = "rel_team")
    private Boolean relTeam = false;
    
    // Link flags
    @Column(name = "is_external")
    private Boolean isExternal = false;
    
    @Column(name = "is_premium")
    private Boolean isPremium = false;
    
    @Column(name = "is_hidden")
    private Boolean isHidden = false;
    
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
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getHref() {
        return href;
    }
    
    public void setHref(String href) {
        this.href = href;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getShortText() {
        return shortText;
    }
    
    public void setShortText(String shortText) {
        this.shortText = shortText;
    }
    
    public Boolean getRelClubhouse() {
        return relClubhouse;
    }
    
    public void setRelClubhouse(Boolean relClubhouse) {
        this.relClubhouse = relClubhouse;
    }
    
    public Boolean getRelRoster() {
        return relRoster;
    }
    
    public void setRelRoster(Boolean relRoster) {
        this.relRoster = relRoster;
    }
    
    public Boolean getRelStats() {
        return relStats;
    }
    
    public void setRelStats(Boolean relStats) {
        this.relStats = relStats;
    }
    
    public Boolean getRelSchedule() {
        return relSchedule;
    }
    
    public void setRelSchedule(Boolean relSchedule) {
        this.relSchedule = relSchedule;
    }
    
    public Boolean getRelTickets() {
        return relTickets;
    }
    
    public void setRelTickets(Boolean relTickets) {
        this.relTickets = relTickets;
    }
    
    public Boolean getRelDepthchart() {
        return relDepthchart;
    }
    
    public void setRelDepthchart(Boolean relDepthchart) {
        this.relDepthchart = relDepthchart;
    }
    
    public Boolean getRelDesktop() {
        return relDesktop;
    }
    
    public void setRelDesktop(Boolean relDesktop) {
        this.relDesktop = relDesktop;
    }
    
    public Boolean getRelTeam() {
        return relTeam;
    }
    
    public void setRelTeam(Boolean relTeam) {
        this.relTeam = relTeam;
    }
    
    public Boolean getIsExternal() {
        return isExternal;
    }
    
    public void setIsExternal(Boolean isExternal) {
        this.isExternal = isExternal;
    }
    
    public Boolean getIsPremium() {
        return isPremium;
    }
    
    public void setIsPremium(Boolean isPremium) {
        this.isPremium = isPremium;
    }
    
    public Boolean getIsHidden() {
        return isHidden;
    }
    
    public void setIsHidden(Boolean isHidden) {
        this.isHidden = isHidden;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}