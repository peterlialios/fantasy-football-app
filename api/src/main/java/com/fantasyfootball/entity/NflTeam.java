package com.fantasyfootball.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "nfl_teams")
public class NflTeam {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // ESPN API identifiers
    @Column(name = "espn_id", unique = true, nullable = false)
    private Integer espnId;
    
    @Column(name = "espn_uid", unique = true, nullable = false, length = 50)
    private String espnUid;
    
    // Basic team information
    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 100)
    private String nickname;
    
    @NotBlank
    @Column(nullable = false, length = 100)
    private String location;
    
    @NotBlank
    @Column(name = "display_name", nullable = false, length = 150)
    private String displayName;
    
    @Column(name = "short_display_name", length = 100)
    private String shortDisplayName;
    
    @NotBlank
    @Column(nullable = false, length = 5)
    private String abbreviation;
    
    @Column(unique = true, nullable = false, length = 100)
    private String slug;
    
    // Team branding
    @Column(name = "primary_color", length = 7)
    private String primaryColor;
    
    @Column(name = "alternate_color", length = 7)
    private String alternateColor;
    
    // Status flags
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "is_all_star")
    private Boolean isAllStar = false;
    
    // NFL structure
    @Column(length = 3)
    private String conference;
    
    @Column(length = 10)
    private String division;
    
    // Timestamps
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "nflTeam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Player> players;
    
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TeamLogo> logos;
    
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TeamLink> links;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEspnId() {
        return espnId;
    }

    public void setEspnId(Integer espnId) {
        this.espnId = espnId;
    }

    public String getEspnUid() {
        return espnUid;
    }

    public void setEspnUid(String espnUid) {
        this.espnUid = espnUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getShortDisplayName() {
        return shortDisplayName;
    }

    public void setShortDisplayName(String shortDisplayName) {
        this.shortDisplayName = shortDisplayName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getAlternateColor() {
        return alternateColor;
    }

    public void setAlternateColor(String alternateColor) {
        this.alternateColor = alternateColor;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsAllStar() {
        return isAllStar;
    }

    public void setIsAllStar(Boolean isAllStar) {
        this.isAllStar = isAllStar;
    }

    public String getConference() {
        return conference;
    }

    public void setConference(String conference) {
        this.conference = conference;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
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

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<TeamLogo> getLogos() {
        return logos;
    }

    public void setLogos(List<TeamLogo> logos) {
        this.logos = logos;
    }

    public List<TeamLink> getLinks() {
        return links;
    }

    public void setLinks(List<TeamLink> links) {
        this.links = links;
    }
}