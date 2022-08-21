package com.firefighter.aenitto.missions.repository;

import com.firefighter.aenitto.missions.domain.CommonMission;

import java.time.LocalDate;
import java.util.Optional;

public interface CommonMissionRepository {
    CommonMission saveCommonMission(CommonMission commonMission);
    Optional<CommonMission> findCommonMissionByDate(LocalDate date);
}
