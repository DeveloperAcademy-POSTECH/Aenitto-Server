package com.firefighter.aenitto.missions.repository;

import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.domain.MissionType;

import java.util.Optional;

public interface MissionRepository {
    Optional<Mission> findRandomMission(MissionType missionType);
}
