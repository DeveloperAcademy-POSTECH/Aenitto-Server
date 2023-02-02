package com.firefighter.aenitto.missions.repository;

import java.time.LocalDate;
import java.util.Optional;

import com.firefighter.aenitto.missions.domain.CommonMission;

public interface CommonMissionRepository {
	CommonMission saveCommonMission(CommonMission commonMission);

	Optional<CommonMission> findCommonMissionByDate(LocalDate date);
}
