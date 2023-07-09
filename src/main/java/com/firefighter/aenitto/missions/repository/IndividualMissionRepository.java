package com.firefighter.aenitto.missions.repository;

import com.firefighter.aenitto.missions.domain.IndividualMission;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndividualMissionRepository extends JpaRepository<IndividualMission, Long> {

  Optional<IndividualMission> findIndividualMissionByDateAndRoomId(LocalDate date,
      Long roomId);
}
