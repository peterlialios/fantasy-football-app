package com.fantasyfootball.repository;

import com.fantasyfootball.entity.RosterPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RosterPositionRepository extends JpaRepository<RosterPosition, Integer> {
    
    Optional<RosterPosition> findByPositionCode(String positionCode);
    
    @Query("SELECT rp FROM RosterPosition rp WHERE rp.isStarting = true ORDER BY rp.displayOrder")
    List<RosterPosition> findStartingPositions();
    
    @Query("SELECT rp FROM RosterPosition rp WHERE rp.isStarting = false ORDER BY rp.displayOrder")
    List<RosterPosition> findBenchPositions();
    
    @Query("SELECT rp FROM RosterPosition rp ORDER BY rp.displayOrder")
    List<RosterPosition> findAllOrderedByDisplayOrder();
}