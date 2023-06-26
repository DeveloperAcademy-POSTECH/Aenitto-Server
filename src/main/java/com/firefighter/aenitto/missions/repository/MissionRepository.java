package com.firefighter.aenitto.missions.repository;

import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.domain.MissionType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {

  @Query("SELECT m FROM Mission AS m ORDER BY RANDOM() LIMIT 1")
  Optional<Mission> findRandomMission(MissionType missionType);
}
