package com.firefighter.aenitto.missions.repository;

import com.firefighter.aenitto.missions.domain.DefaultMission;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefaultMissionRepository extends JpaRepository<DefaultMission, Long> {

  Optional<DefaultMission> findByIndividualMissionId(Long individualId);
}
