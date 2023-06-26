package com.firefighter.aenitto.missions.repository;

import com.firefighter.aenitto.missions.domain.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long>, MissionRepositoryCustom {

}
