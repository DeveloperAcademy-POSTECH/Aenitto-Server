package com.firefighter.aenitto.missions.repository;

import java.time.LocalDate;
import java.util.Optional;

import com.firefighter.aenitto.missions.domain.IndividualMission;
import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.domain.MissionType;

public interface MissionRepository {

	Optional<Mission> findRandomMission(MissionType missionType);

	Optional<IndividualMission> findIndividualMissionByDate(LocalDate date, Long memberRoomId);
	Optional<Mission> findById(Long id);

	void save(Mission mission);

	void save(IndividualMission individualMission);
}
