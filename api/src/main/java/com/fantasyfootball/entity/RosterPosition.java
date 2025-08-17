package com.fantasyfootball.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "roster_positions")
public class RosterPosition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotBlank
    @Column(name = "position_code", unique = true, nullable = false, length = 10)
    private String positionCode;
    
    @NotBlank
    @Column(name = "position_name", nullable = false, length = 50)
    private String positionName;
    
    @Min(1)
    @Column(name = "max_count", nullable = false)
    private Integer maxCount;
    
    @Column(name = "is_starting")
    private Boolean isStarting = true;
    
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getPositionCode() {
        return positionCode;
    }
    
    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }
    
    public String getPositionName() {
        return positionName;
    }
    
    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }
    
    public Integer getMaxCount() {
        return maxCount;
    }
    
    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }
    
    public Boolean getIsStarting() {
        return isStarting;
    }
    
    public void setIsStarting(Boolean isStarting) {
        this.isStarting = isStarting;
    }
    
    public Integer getDisplayOrder() {
        return displayOrder;
    }
    
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}