package com.firefighter.aenitto.missions.repository;

import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.domain.MissionType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {

  @Query(value = "SELECT m FROM Mission m WHERE m.type = :missionType ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
  Optional<Mission> findRandomMission(MissionType missionType);
}
